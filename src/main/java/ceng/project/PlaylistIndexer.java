package ceng.project;

import ceng.project.entity.Playlist;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlaylistIndexer implements AutoCloseable {

    private final String playlistIndexDir = "src\\main\\resources\\index";

    private final IndexWriter writer;

    public PlaylistIndexer() throws IOException {
        //this directory will contain the indexes
        Directory indexDirectory = FSDirectory.open(Paths.get(playlistIndexDir));

        var analyzer = new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet());
        var indexWriterConfig = new IndexWriterConfig(analyzer);


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

    private Document getDocument(Playlist playlist) throws IOException {
        Document document = new Document();


        //index playlist name
        Field pidField = new StringField(Constants.PLAYLIST_PID, playlist.pid().toString(), Field.Store.YES);
        document.add(pidField);

        //index playlist name
        Field nameField = new TextField(Constants.PLAYLIST_NAME, playlist.name(), Field.Store.YES);
        document.add(nameField);

        //index file contents
        Field contentField = new TextField(Constants.PLAYLIST_CONTENTS, playlist.content(), Field.Store.YES);
        document.add(contentField);

        //index track uri list
        Field trackUriListField = new StringField(Constants.PLAYLIST_TRACK_URI_LIST, playlist.trackUriList(), Field.Store.YES);
        document.add(trackUriListField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());

        // parse json
        var json = new Gson().fromJson(new FileReader(file), JsonObject.class);
        Type listType = new TypeToken<ArrayList<Playlist>>(){}.getType();
        List<Playlist> playlistList = new Gson().fromJson(json.get("playlists"), listType);

        for (Playlist playlist : playlistList) {
            Document document = getDocument(playlist);
            writer.addDocument(document);
        }


    }

    public void createIndex(String dataDirPath) throws IOException {
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