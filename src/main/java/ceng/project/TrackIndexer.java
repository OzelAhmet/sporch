package ceng.project;

import ceng.project.entity.Playlist;
import ceng.project.entity.Track;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TrackIndexer implements AutoCloseable {

    static String trackIndexDir = "src\\main\\resources\\index\\track";

    private final IndexWriter writer;

    public TrackIndexer() throws IOException {
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(Paths.get(trackIndexDir));

        var analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
        var indexWriterConfig = new IndexWriterConfig(analyzer);

//        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

//        if (create) {
//            // Create a new index in the directory, removing any
//            // previously indexed documents:
//            indexWriterConfig.setOpenMode(OpenMode.CREATE);
//        } else {
//            // Add new documents to an existing index:
//            indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
//        }

        //create the indexer
        writer = new IndexWriter(indexDirectory, indexWriterConfig);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    private Document getDocument(Playlist playlist, Track track) throws IOException, ParseException {
        String playlistPidList = playlist.pid().toString();

        String searchedList;
        try (TrackSearcher trackSearcher = new TrackSearcher()) {
            searchedList = trackSearcher.searchPidListByUri(track.track_uri());
        }
        if (searchedList != null) {
            playlistPidList = playlistPidList + "," + searchedList;
        }

        Document document = new Document();
        Field trackUriField = new StringField(Constants.TRACK_URI, track.track_uri(), Field.Store.YES);
        document.add(trackUriField);
        Field trackNameField = new StringField(Constants.TRACK_NAME, track.track_name(), Field.Store.YES);
        document.add(trackNameField);
        Field artistNameField = new StringField(Constants.ARTIST_NAME, track.artist_name(), Field.Store.YES);
        document.add(artistNameField);
        Field albumNameField = new StringField(Constants.ALBUM_NAME, track.artist_name(), Field.Store.YES);
        document.add(albumNameField);
        Field playlistPidListField = new StringField(Constants.PLAYLIST_PID_LIST, playlistPidList, Field.Store.YES);
        document.add(playlistPidListField);

        return document;
    }

    private void indexFile(File file) throws IOException, ParseException {
        System.out.println("Indexing Tracks" + file.getCanonicalPath());

        // parse json
        var json = new Gson().fromJson(new FileReader(file), JsonObject.class);
        Type listType = new TypeToken<ArrayList<Playlist>>(){}.getType();
        List<Playlist> playlistList = new Gson().fromJson(json.get("playlists"), listType);

        for (Playlist playlist : playlistList) {
            for (Track track : playlist.tracks()) {
                Document document = getDocument(playlist, track);
                writer.updateDocument(new Term(Constants.TRACK_URI, track.track_uri()), document);
            }
            // commit after each playlist because playlists do contain unique list of tracks. Even if they contain, we do not need to keep playlist id multiple times in the list
            writer.commit();
        }

    }

    public void createIndex(String dataDirPath) throws IOException, ParseException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if (!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
            ) {
                indexFile(file);
            }
        }

        System.out.println("maxDoc: " + writer.getDocStats().maxDoc);
        System.out.println("numDocs: " + writer.getDocStats().numDocs);
    }
}