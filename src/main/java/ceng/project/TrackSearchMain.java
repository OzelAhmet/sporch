package ceng.project;

import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;

public class TrackSearchMain {

    static String dataDir = "src\\main\\resources\\data";
    static String trackIndexDir = "src\\main\\resources\\index\\track";

    public static void main(String[] args) {
        try {
            search("6I9VzXrHxO9rA9A5euc8Ak"); // Toxic, britney spears
        } catch (IOException e) {
            throw new RuntimeException("io", e);
        } catch (ParseException e) {
            throw new RuntimeException("parse", e);
        }
    }

    private static void search(String searchQuery) throws IOException, ParseException {
        try (TrackSearcher searchFiles = new TrackSearcher()) {
            long startTime = System.currentTimeMillis();
            String playlists = searchFiles.searchPidListByUri(searchQuery);
            long endTime = System.currentTimeMillis();

                System.out.println("Playlist List: " + playlists);
        }
    }
}