package com.thetechprepper.emcommtools.api.service.search;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thetechprepper.emcommtools.api.model.NWSZoneCounty;

@Service
public class WeatherForecastZoneService extends AbstractLuceneSearchService<NWSZoneCounty> {

    @Value("${api.data.weatherforecastzone.path}")
    private String weatherForecastZoneDataPath;

    @Value("${api.index.weatherforecastzone.path}")
    private String weatherForecastZoneIndexPath;

    private static final int NUM_CSV_FIELDS = 11;
    private static int STATE = 0;
    private static int ZONE = 1;
    private static int NAME = 3;
    private static int COUNTY = 5;
    private static int LAT = 9;
    private static int LON = 10;

    private static String INDEX_FIELD_STATE = "state";
    private static String INDEX_FIELD_ZONE = "zone";
    private static String INDEX_FIELD_NAME = "name";
    private static String INDEX_FIELD_LAT = "lat";
    private static String INDEX_FIELD_LON = "lon";
    private static String INDEX_FIELD_COUNTY = "county";

    private static String INDEX_FIELD_GEO = "geo";
    private static String INDEX_FIELD_GEO_SORT = "geosort";

    private static long DEFAULT_SEARCH_RADIUS_IN_METERS = 482000; // 482,000m = 482km = 300mi

    @Override
    protected String getIndexPath() {
        return weatherForecastZoneIndexPath;
    }

    @Override
    protected String getDataPath() {
        return weatherForecastZoneDataPath;
    }

    @Override
    protected NWSZoneCounty parseRecord(String record) {
        if (StringUtils.isNotBlank(record)) {
            String fields[] = record.split("\\|");
            if ( (fields != null) && (fields.length == NUM_CSV_FIELDS) ) {

                try {
                    return NWSZoneCounty.newInstance()
                            .withState(fields[STATE])
                            .withZone(fields[ZONE])
                            .withName(fields[NAME])
                            .withLat(Double.valueOf(fields[LAT]))
                            .withLon(Double.valueOf(fields[LON]))
                            .withCounty(fields[COUNTY]);

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
    protected void indexDoc(IndexWriter writer, NWSZoneCounty zone) throws IOException {
        Document doc = new Document();
        doc.add(new TextField(INDEX_FIELD_STATE, zone.getState(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_ZONE, zone.getZone(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_NAME, zone.getName(), Field.Store.YES));
        doc.add(new TextField(INDEX_FIELD_COUNTY, zone.getCounty(), Field.Store.YES));

        doc.add(new StoredField(INDEX_FIELD_LAT, zone.getLat()));
        doc.add(new StoredField(INDEX_FIELD_LON, zone.getLon()));
        doc.add(new LatLonPoint(INDEX_FIELD_GEO, zone.getLat(), zone.getLon()));
        doc.add(new LatLonDocValuesField(INDEX_FIELD_GEO_SORT, zone.getLat(), zone.getLon()));

        writer.addDocument(doc);
    }

    @Override
    protected NWSZoneCounty convertDocumentToEntity(Document doc) {
        return NWSZoneCounty.newInstance()
                   .withState(doc.get(INDEX_FIELD_STATE))
                   .withZone(doc.get(INDEX_FIELD_ZONE))
                   .withName(doc.get(INDEX_FIELD_NAME))
                   .withLat(Double.valueOf(doc.get(INDEX_FIELD_LAT)))
                   .withLon(Double.valueOf(doc.get(INDEX_FIELD_LON)))
                   .withCounty(doc.get(INDEX_FIELD_COUNTY));
    }

    public List<NWSZoneCounty> findNear(final Double lat, final Double lon) {
        List<NWSZoneCounty> zones = new ArrayList<>();

        try {
            IndexReader reader = DirectoryReader.open(getDirectory());
            IndexSearcher searcher = new IndexSearcher(reader);

            Query distanceQuery = LatLonPoint.newDistanceQuery(INDEX_FIELD_GEO, lat, lon, DEFAULT_SEARCH_RADIUS_IN_METERS);

            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            builder.add(distanceQuery, BooleanClause.Occur.MUST);

            Query finalQuery = builder.build();

            SortField sortByDistance = LatLonDocValuesField.newDistanceSort(INDEX_FIELD_GEO_SORT, lat, lon);
            Sort sort = new Sort(new SortField[] {sortByDistance});
            TopDocs foundDocs = searcher.search(finalQuery, 20, sort);
            LOG.debug("Found '{}' NWS zones for query '{}'", foundDocs.totalHits, finalQuery);

            for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                NWSZoneCounty zone = convertDocumentToEntity(doc);
                LOG.debug("Found NWS zones: '{}'", zone);
                zones.add(zone);
            }
            reader.close();
        } catch (IOException e) {
            LOG.error("Error executing geospatial query for lat='{}', lon='{}'", lat, lon, e);
        }
        return zones;
    }
}
