package ceng.project.entity;

public record Track(String track_uri, String track_name, String album_name, String artist_name) {

    @Override
    public String track_uri() {
        String[] uriParts = track_uri.split(":");
        return uriParts[uriParts.length - 1];
    }

    public String content() {
        return track_name + ' ' + album_name + ' ' + artist_name;
    }
}
