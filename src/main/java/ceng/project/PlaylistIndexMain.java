package ceng.project;

import java.io.IOException;

public class PlaylistIndexMain {

    static String dataDir = "src\\main\\resources\\data";

    public static void main(String[] args) {
        try {
            createIndex();
        } catch (IOException e) {
            throw new RuntimeException("io", e);
        }
    }

    private static void createIndex() throws IOException {
        try (PlaylistIndexer indexFiles = new PlaylistIndexer()) {
            long startTime = System.currentTimeMillis();
            indexFiles.createIndex(dataDir);
            long endTime = System.currentTimeMillis();
            System.out.println("All files indexed, time taken: " + (endTime - startTime) + " ms");
        }
    }
}