package ceng.project;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class TrackIndexMain {

    static String dataDir = "src\\main\\resources\\data";

    public static void main(String[] args) {
        try {
            createIndex();
        } catch (IOException e) {
            throw new RuntimeException("io", e);
        } catch (ParseException e) {
            throw new RuntimeException("parse", e);
        }
    }

    private static void createIndex() throws IOException, ParseException {
        try (TrackIndexer TrackIndexer = new TrackIndexer()) {
            long startTime = System.currentTimeMillis();
            TrackIndexer.createIndex(dataDir);
            long endTime = System.currentTimeMillis();
            System.out.println("All files indexed, time taken: " + (endTime - startTime) + " ms");
        }
    }
}