package ceng.project;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class Main {

    static String dataDir = "src\\main\\resources\\data";
    static String indexDir = "src\\main\\resources\\index";

    public static void main(String[] args) {
        try {
            createIndex();
            search(Constants.PLAYLIST_NAME, "HALLOWEEN");
            search(Constants.PLAYLIST_CONTENTS, "Nickelback");
        } catch (IOException e) {
            throw new RuntimeException("io", e);
        } catch (ParseException e) {
            throw new RuntimeException("parse", e);
        }
    }

    private static void createIndex() throws IOException {
        try (IndexFiles indexFiles = new IndexFiles(indexDir)) {
            long startTime = System.currentTimeMillis();
            indexFiles.createIndex(dataDir, new TextFileFilter());
            long endTime = System.currentTimeMillis();
            System.out.println("All files indexed, time taken: " + (endTime - startTime) + " ms");
        }
    }

    private static void search(String field, String searchQuery) throws IOException, ParseException {
        try (SearchFiles searchFiles = new SearchFiles(indexDir)) {
            long startTime = System.currentTimeMillis();
            TopDocs hits = searchFiles.search(field, searchQuery);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));

            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searchFiles.getDocument(scoreDoc);
                System.out.println("Playlist: " + doc.get(Constants.PLAYLIST_NAME));
            }
        }
    }
}