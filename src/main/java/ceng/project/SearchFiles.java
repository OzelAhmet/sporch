package ceng.project;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class SearchFiles implements AutoCloseable{

    private final DirectoryReader indexReader;
    private final IndexSearcher indexSearcher;
    public QueryParser queryParser;

    public SearchFiles(String indexDirectoryPath) throws IOException {
        indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectoryPath)));
        indexSearcher = new IndexSearcher(indexReader);
    }

    public TopDocs search(String field, String searchQuery) throws IOException, ParseException {
        queryParser = new QueryParser(field, new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet()));
        Query query = queryParser.parse(searchQuery);
        return indexSearcher.search(query, Constants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        indexReader.close();
    }

}
