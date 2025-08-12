package com.thetechprepper.emcommtools.api.service.search;

import com.thetechprepper.emcommtools.api.model.Zip2Geo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class Zip2GeoService {

    private static final Logger LOG = LoggerFactory.getLogger(Zip2GeoService.class);
    private static final Analyzer ANALYZER = new StandardAnalyzer();

    public static final int NUM_CSV_FIELDS = 4;
    private static int ZIP = 0;
    private static int LAT = 1;
    private static int LON = 2;
    private static int ALT = 3;

    private static String INDEX_FIELD_ZIP2GEO_ZIP = "zip";
    private static String INDEX_FIELD_ZIP2GEO_LAT = "lat";
    private static String INDEX_FIELD_ZIP2GEO_LON = "lon";
    private static String INDEX_FIELD_ZIP2GEO_ALT = "alt";

    @Value("${api.data.zip2geo.path}")
    private String zip2GeoDataPath;

    @Value("${api.index.zip2geo.path}")
    private String zip2GeoIndexPath;

    private Directory directory;

    public void createIndex() {
        if (fileOrDirDoesNotExist(zip2GeoIndexPath)) {
            LOG.info("Creating index: '{}'", zip2GeoIndexPath);
            writeIndex();
            LOG.info("Finished creating index: '{}'", zip2GeoIndexPath);
        } else {
            LOG.info("Index '{}' already exists. Skipping build.", zip2GeoIndexPath);
        }
    }

    public Zip2Geo findByZip(final String zip) {

        Zip2Geo zip2Geo = null;
        try {
            IndexReader reader = DirectoryReader.open(getDirectory());
            IndexSearcher searcher = new IndexSearcher(reader);

            QueryParser qp = new QueryParser(INDEX_FIELD_ZIP2GEO_ZIP, ANALYZER);
            Query query = qp.parse(zip);
            TopDocs foundDocs = searcher.search(query, 1);
            LOG.info("Found '{}' docs for query '{}'", foundDocs.totalHits, query);

            // todo: check index and grab first
            for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                IndexableField latIndexField = doc.getField(INDEX_FIELD_ZIP2GEO_LAT);
                LOG.info("Lat index field is null={}", latIndexField==null);
                zip2Geo = Zip2Geo.newInstance()
                        .withZip(doc.get(INDEX_FIELD_ZIP2GEO_ZIP))
                        .withLat(Double.valueOf(doc.get(INDEX_FIELD_ZIP2GEO_LAT)))
                        .withLon(Double.valueOf(doc.get(INDEX_FIELD_ZIP2GEO_LON)))
                        .withAlt(Double.valueOf(doc.get(INDEX_FIELD_ZIP2GEO_ALT)));
                LOG.info("Found: '{}'", zip2Geo);
            }
            reader.close();
        } catch (IOException | ParseException e) {
            LOG.error("Error executing zip2geo query for zip: '{}'", zip);
        }
        return zip2Geo;
    }

    // todo push into abstract class
    private Directory getDirectory() {
        try {
            if (null == directory) {
                directory = FSDirectory.open(Paths.get(zip2GeoIndexPath));
            }
            return directory;
        } catch (IOException e) {
            LOG.error("Can't get index directory: '{}'", zip2GeoIndexPath);
            throw new RuntimeException("Can't get index directory");
        }
    }

    // todo push into abstract class
    private boolean fileOrDirDoesNotExist(final String fileOrDir) {
        return !fileOrDirExists(fileOrDir);
    }

    // todo push into abstract class
    private boolean fileOrDirExists(final String fileOrDir) {
        return StringUtils.isNotBlank(fileOrDir)
                ? Files.exists(Paths.get(fileOrDir))
                : false;
    }

    // todo push into abstract class
    private void writeIndex() {
        try {
            IndexWriterConfig iwc = new IndexWriterConfig(ANALYZER);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(getDirectory(), iwc);
            BufferedReader reader = new BufferedReader(new FileReader(zip2GeoDataPath));
            String record = null;
            int i = 0;
            while ((record = reader.readLine()) != null) {
                Zip2Geo zip2Geo = parseRecord(record);
                if (zip2Geo != null) {
                    indexDoc(writer, zip2Geo);
                    i++;
                }
            }
            LOG.info("Added '{}' records to index", i);
            reader.close();
            writer.close();
        } catch (IOException e) {
            LOG.error("Error writing index", e);
        }
    }

    // todo push into abstract class
    private void indexDoc(IndexWriter writer, Zip2Geo zip2Geo) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(INDEX_FIELD_ZIP2GEO_ZIP, zip2Geo.getZip(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_ZIP2GEO_LAT, String.valueOf(zip2Geo.getLat()), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_ZIP2GEO_LON, String.valueOf(zip2Geo.getLon()), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_ZIP2GEO_ALT, String.valueOf(zip2Geo.getAlt()), Field.Store.YES));
        writer.addDocument(doc);
    }

    // todo push into abstract class
    private Zip2Geo parseRecord(final String record) {
        if (StringUtils.isNotBlank(record)) {
            String fields[] = record.split("\\|");
            if ( (fields != null) && (fields.length == NUM_CSV_FIELDS) ) {
                try {
                    return Zip2Geo.newInstance()
                            .withZip(fields[ZIP])
                            .withLat(Double.valueOf(fields[LAT]))
                            .withLon(Double.valueOf(fields[LON]))
                            .withAlt(Double.valueOf(fields[ALT]));
                } catch (Exception e) {
                    LOG.warn("Error parsing record: '{}'", record, e);
                    return null;
                }
            } {
                LOG.info("Incorrect fields for record: '{}'", record);
            }
        } else {
            LOG.info("Found missing record: '{}'", record);
        }
        return null;
    }

}