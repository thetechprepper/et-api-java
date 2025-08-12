package com.thetechprepper.emcommtools.api.service.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLuceneSearchService<T> {

    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    protected abstract String getIndexPath();
    protected abstract String getDataPath();

    protected abstract T parseRecord(String record);
    protected abstract void indexDoc(IndexWriter writer, T item) throws IOException;

    private Directory directory;
    private static final Analyzer ANALYZER = new StandardAnalyzer();

    /**
     * Creates a Lucene index on disk if it does not already exist.
     */
    public void createIndex() {
        if (fileOrDirDoesNotExist(getIndexPath())) {
            LOG.info("Creating index: '{}'", getIndexPath());
            writeIndex();
            LOG.info("Finished creating index: '{}'", getIndexPath());
        } else {
            LOG.info("Index '{}' already exists. Skipping build.", getIndexPath());
        }
    }

    protected Directory getDirectory() {
        try {
            if (directory == null) {
                directory = FSDirectory.open(Paths.get(getIndexPath()));
            }
            return directory;
        } catch (IOException e) {
            LOG.error("Can't get index directory: {}", getIndexPath());
            throw new RuntimeException("Can't get index directory: " + getIndexPath(), e);
        }
    }

    private boolean fileOrDirExists(String path) {
        return StringUtils.isNotBlank(path) && Files.exists(Paths.get(path));
    }
    private boolean fileOrDirDoesNotExist(String path) {
        return !fileOrDirExists(path);
    }

    private void writeIndex() {
        try (IndexWriter writer = new IndexWriter(getDirectory(), new IndexWriterConfig(ANALYZER).setOpenMode(IndexWriterConfig.OpenMode.CREATE));
             BufferedReader reader = new BufferedReader(new FileReader(getDataPath()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                T item = parseRecord(line);
                if (item != null) {
                    indexDoc(writer, item);
                    count++;
                }
            }
            LOG.info("Added {} records to index", count);
        } catch (IOException e) {
            LOG.error("Error writing index", e);
            throw new RuntimeException("Error writing index", e);
        }
    }

    protected abstract T convertDocumentToEntity(Document doc);

    public T findByField(String fieldName, String queryText) {
        try (IndexReader reader = DirectoryReader.open(getDirectory())) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser(fieldName, ANALYZER);
            Query query = parser.parse(queryText);
            TopDocs results = searcher.search(query, 1);
            if (results.totalHits.value > 0) {
                Document doc = searcher.doc(results.scoreDocs[0].doc);
                return convertDocumentToEntity(doc);
            }
        } catch (IOException | ParseException e) {
            LOG.error("Error during fielded query: {}:{}", fieldName, queryText, e);
        }
        return null;
    }
}