package com.thetechprepper.emcommtools.api.service.search;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thetechprepper.emcommtools.api.model.Aircraft;
import com.thetechprepper.emcommtools.api.util.FaaUtils;

@Service
public class FaaSearchService extends AbstractLuceneSearchService<Aircraft> {

    @Value("${api.data.faa.path}")
    private String faaDataPath;

    @Value("${api.index.faa.path}")
    private String faaIndexPath;

    public static final int NUM_CSV_FIELDS = 9;
    private static int TAIL_NUMBER = 0;
    private static int MAKE = 1;
    private static int MODEL = 2;
    private static int YEAR = 3;
    private static int OWNER_NAME = 4;
    private static int CITY = 5;
    private static int STATE = 6;
    private static int ICAO24 = 7;
    private static int REGISTRANT_TYPE = 8;

    public static String INDEX_FIELD_TAIL_NUMBER = "tail_number";
    public static String INDEX_FIELD_MAKE = "make";
    public static String INDEX_FIELD_MODEL = "model";
    public static String INDEX_FIELD_YEAR = "year";
    public static String INDEX_FIELD_OWNER_NAME = "owner_name";
    public static String INDEX_FIELD_CITY = "city";
    public static String INDEX_FIELD_STATE = "state";
    public static String INDEX_FIELD_ICAO24 = "icao24";
    public static String INDEX_FIELD_REGISTRANT_TYPE = "registrant_type";

    @Override
    protected String getIndexPath() {
        return faaIndexPath;
    }

    @Override
    protected String getDataPath() {
        return faaDataPath;
    }

    @Override
    protected Aircraft parseRecord(String record) {
        if (StringUtils.isNotBlank(record)) {
            String fields[] = record.split("\\|");
            if ( (fields != null) && (fields.length == NUM_CSV_FIELDS) ) {

                if (FaaUtils.isNotValidIcao24(fields[ICAO24])) {
                    LOG.warn("Invalid ICAO24 value: '{}'", fields[ICAO24]);
                    return null;
                }

                try {
                    return Aircraft.newInstance()
                            .withTailNumber(fields[TAIL_NUMBER])
                            .withMake(fields[MAKE])
                            .withModel(fields[MODEL])
                            .withYear(Integer.valueOf(fields[YEAR]))
                            .withOwnerName(fields[OWNER_NAME])
                            .withCity(fields[CITY])
                            .withState(fields[STATE])
                            .withIcao24(fields[ICAO24])
                            .withRegistrantType(fields[REGISTRANT_TYPE]);

                } catch (Exception e) {
                    LOG.warn("Error parsing record: '{}'", record, e);
                    return null;
                }
            } else {
                LOG.warn("Incomplete number of fields for record: '{}'", record);
            }
        } else {
            LOG.warn("Skipping empty record");
        }
        return null;
    }

    @Override
    protected void indexDoc(IndexWriter writer, Aircraft aircraft) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(INDEX_FIELD_TAIL_NUMBER, aircraft.getTailNumber(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_MAKE, aircraft.getMake(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_MODEL, aircraft.getModel(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_YEAR, String.valueOf(aircraft.getYear()), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_OWNER_NAME, aircraft.getOwnerName(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_CITY, aircraft.getCity(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_STATE, aircraft.getState(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_ICAO24, aircraft.getIcao24(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_REGISTRANT_TYPE, aircraft.getRegistrantType(), Field.Store.YES));
        writer.addDocument(doc);
    }

    @Override
    protected Aircraft convertDocumentToEntity(Document doc) {
        return Aircraft.newInstance()
                   .withTailNumber(doc.get(INDEX_FIELD_TAIL_NUMBER))
                   .withMake(doc.get(INDEX_FIELD_MAKE))
                   .withModel(doc.get(INDEX_FIELD_MODEL))
                   .withYear(Integer.valueOf(doc.get(INDEX_FIELD_YEAR)))
                   .withOwnerName(doc.get(INDEX_FIELD_OWNER_NAME))
                   .withCity(doc.get(INDEX_FIELD_CITY))
                   .withState(doc.get(INDEX_FIELD_STATE))
                   .withIcao24(doc.get(INDEX_FIELD_ICAO24))
                   .withRegistrantType(doc.get(INDEX_FIELD_REGISTRANT_TYPE));
    }
}