package com.thetechprepper.emcommtools.api.service.search;

import com.thetechprepper.emcommtools.api.model.Licensee;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class LicenseSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(LicenseSearchService.class);
    private static final Analyzer ANALYZER = new StandardAnalyzer();

    // KT1RUN|Gaston|CAVE CREEK|85331|AZ
    public static final int NUM_CSV_FIELDS = 5;
    private static int LICENSEE_CALLSIGN = 0;
    private static int LICENSEE_FIRST_NAME = 1;
    private static int LICENSEE_CITY = 2;
    private static int LICENSEE_ZIP = 3;
    private static int LICENSEE_STATE = 4;

    private static String INDEX_FIELD_LICENSEE_CALLSIGN = "callsign";
    private static String INDEX_FIELD_LICENSEE_FIRST_NAME = "first_name";
    private static String INDEX_FIELD_LICENSEE_CITY = "city";
    private static String INDEX_FIELD_LICENSEE_ZIP = "zip";
    private static String INDEX_FIELD_LICENSEE_STATE = "state";

    @Value("${api.data.license.path}")
    private String licenseDataPath;

    @Value("${api.index.license.path}")
    private String licenseIndexPath;

    private Directory directory;

    public void createIndex() {
        if (fileOrDirDoesNotExist(licenseIndexPath)) {
            LOG.info("Creating index: '{}'", licenseIndexPath);
            writeIndex();
            LOG.info("Finished creating index: '{}'", licenseIndexPath);
        } else {
            LOG.info("Index '{}' already exists. Skipping build.", licenseIndexPath);
        }
    }

    public List<Licensee> findByCall(final String callsign) {
        List<Licensee> licensees = new ArrayList<>();
        try {
            IndexReader reader = DirectoryReader.open(getDirectory());
            IndexSearcher searcher = new IndexSearcher(reader);

            QueryParser qp = new QueryParser(INDEX_FIELD_LICENSEE_CALLSIGN, ANALYZER);
            Query query = qp.parse(callsign);
            TopDocs foundDocs = searcher.search(query, 1);
            LOG.debug("Found '{}' docs for query '{}'", foundDocs.totalHits, query);

            for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                Licensee licensee = Licensee.newInstance()
                        .withCallsign(doc.get(INDEX_FIELD_LICENSEE_CALLSIGN))
                        .withFirstName(doc.get(INDEX_FIELD_LICENSEE_FIRST_NAME))
                        .withCity(doc.get(INDEX_FIELD_LICENSEE_CITY))
                        .withZip(doc.get(INDEX_FIELD_LICENSEE_ZIP))
                        .withState(doc.get(INDEX_FIELD_LICENSEE_STATE));
                LOG.info("Found: '{}'", licensee);
                licensees.add(licensee);
            }
            reader.close();
        } catch (IOException | ParseException e) {
            LOG.error("Error executing query: '{}'", callsign);
        }
        return licensees;
    }

    private Directory getDirectory() {
        try {
            if (null == directory) {
                directory = FSDirectory.open(Paths.get(licenseIndexPath));
            }
            return directory;
        } catch (IOException e) {
            LOG.error("Can't get index directory: '{}'", licenseIndexPath);
            throw new RuntimeException("Can't get index directory");
        }
    }

    private boolean fileOrDirDoesNotExist(final String fileOrDir) {
        return !fileOrDirExists(fileOrDir);
    }

    private boolean fileOrDirExists(final String fileOrDir) {
        return StringUtils.isNotBlank(fileOrDir)
                ? Files.exists(Paths.get(fileOrDir))
                : false;
    }

    private void writeIndex() {
        try {
            IndexWriterConfig iwc = new IndexWriterConfig(ANALYZER);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(getDirectory(), iwc);
            BufferedReader reader = new BufferedReader(new FileReader(licenseDataPath));
            String record = null;
            while ((record = reader.readLine()) != null) {
                Licensee licensee = parseRecord(record);
                if (licensee != null) {
                    indexDoc(writer, licensee);
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            LOG.error("Error writing index", e);
        }
    }

    private void indexDoc(IndexWriter writer, Licensee licensee) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(INDEX_FIELD_LICENSEE_CALLSIGN, licensee.getCallsign(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_LICENSEE_FIRST_NAME, licensee.getFirstName(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_LICENSEE_CITY, licensee.getCity(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_LICENSEE_ZIP, licensee.getZip(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_LICENSEE_STATE, licensee.getState(), Field.Store.YES));
        writer.addDocument(doc);
    }

    private Licensee parseRecord(final String record) {
        if (StringUtils.isNotBlank(record)) {
            String fields[] = record.split("\\|");
            if ( (fields != null) && (fields.length == NUM_CSV_FIELDS) ) {
                return Licensee.newInstance()
                        .withCallsign(fields[LICENSEE_CALLSIGN])
                        .withFirstName(fields[LICENSEE_FIRST_NAME])
                        .withCity(fields[LICENSEE_CITY])
                        .withZip(fields[LICENSEE_ZIP])
                        .withState(fields[LICENSEE_STATE]);
            }
        }
        return null;
    }
}