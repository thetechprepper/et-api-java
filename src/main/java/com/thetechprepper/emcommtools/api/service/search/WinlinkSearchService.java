package com.thetechprepper.emcommtools.api.service.search;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LatLonDocValuesField;
import org.apache.lucene.document.LatLonPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thetechprepper.emcommtools.api.model.WinlinkRmsChannel;

@Service
public class WinlinkSearchService extends AbstractLuceneSearchService<WinlinkRmsChannel> {

    @Value("${api.data.winlink.path}")
    private String winlinkDataPath;

    @Value("${api.index.winlink.path}")
    private String winlinkIndexPath;

    public static final int NUM_CSV_FIELDS = 7;
    private static int BASE_CALLSIGN = 0;
    private static int CALLSIGN = 1;
    private static int LAT = 2;
    private static int LON = 3;
    private static int MODE = 4;
    private static int MODE_CODE = 5;
    private static int FREQ = 6;

    public static String INDEX_FIELD_BASE_CALLSIGN = "base_callsign";
    public static String INDEX_FIELD_CALLSIGN = "callsign";
    public static String INDEX_FIELD_LAT = "lat";
    public static String INDEX_FIELD_LON = "lon";
    public static String INDEX_FIELD_MODE = "mode";
    public static String INDEX_FIELD_MODE_CODE = "mode_code";
    public static String INDEX_FIELD_FREQ = "freq";

    public static String INDEX_FIELD_GEO = "geo";
    public static String INDEX_FIELD_GEO_SORT = "geosort";

    @Override
    protected String getIndexPath() {
        return winlinkIndexPath;
    }

    @Override
    protected String getDataPath() {
        return winlinkDataPath;
    }

    @Override
    protected WinlinkRmsChannel parseRecord(String record) {
        if (StringUtils.isNotBlank(record)) {
            String fields[] = record.split("\\|");
            if ( (fields != null) && (fields.length == NUM_CSV_FIELDS) ) {

                try {
                    return WinlinkRmsChannel.newInstance()
                            .withBaseCallsign(fields[BASE_CALLSIGN])
                            .withCallsign(fields[CALLSIGN])
                            .withLat(Double.valueOf(fields[LAT]))
                            .withLon(Double.valueOf(fields[LON]))
                            .withMode(fields[MODE])
                            .withModeCode(Integer.valueOf(fields[MODE_CODE]))
                            .withFreq(Double.valueOf(fields[FREQ]));

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
    protected void indexDoc(IndexWriter writer, WinlinkRmsChannel channel) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(INDEX_FIELD_BASE_CALLSIGN, channel.getBaseCallsign(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_CALLSIGN, channel.getCallsign(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_MODE, channel.getMode(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_MODE_CODE, String.valueOf(channel.getModeCode()), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_FREQ, String.valueOf(channel.getFreq()), Field.Store.YES));

        doc.add(new StoredField(INDEX_FIELD_LAT, channel.getLat()));
        doc.add(new StoredField(INDEX_FIELD_LON, channel.getLon()));
        doc.add(new LatLonPoint(INDEX_FIELD_GEO, channel.getLat(), channel.getLon()));
        doc.add(new LatLonDocValuesField(INDEX_FIELD_GEO_SORT, channel.getLat(), channel.getLon()));

        writer.addDocument(doc);
    }

    @Override
    protected WinlinkRmsChannel convertDocumentToEntity(Document doc) {
        return WinlinkRmsChannel.newInstance()
                   .withBaseCallsign(doc.get(INDEX_FIELD_BASE_CALLSIGN))
                   .withCallsign(doc.get(INDEX_FIELD_CALLSIGN))
                   .withLat(Double.valueOf(doc.get(INDEX_FIELD_LAT)))
                   .withLon(Double.valueOf(doc.get(INDEX_FIELD_LON)))
                   .withMode(doc.get(INDEX_FIELD_MODE))
                   .withModeCode(Integer.valueOf(doc.get(INDEX_FIELD_MODE_CODE)))
                   .withFreq(Double.valueOf(doc.get(INDEX_FIELD_FREQ)));
    }
}
