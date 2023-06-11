package ceng.project;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

public class PlaylistSearchMain {

    public static void main(String[] args) {
        try {
            search(Constants.PLAYLIST_NAME, "HALLOWEEN");
            search(Constants.PLAYLIST_CONTENTS, "Nickelback");
        } catch (IOException e) {
            throw new RuntimeException("io", e);
        } catch (ParseException e) {
            throw new RuntimeException("parse", e);
        }
    }

    private static void search(String field, String searchQuery) throws IOException, ParseException {
        try (PlaylistSearcher playlistSearcher = new PlaylistSearcher()) {
            long startTime = System.currentTimeMillis();
            TopDocs hits = playlistSearcher.search(field, searchQuery);
            long endTime = System.currentTimeMillis();

            System.out.println(hits.totalHits + " documents found. Time :" + (endTime - startTime));

            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = playlistSearcher.getDocument(scoreDoc);
                System.out.println("Playlist: " + doc.get(Constants.PLAYLIST_NAME));
            }
        }
    }
}