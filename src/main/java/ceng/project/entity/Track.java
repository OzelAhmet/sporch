package ceng.project.entity;

public record Track(String track_uri, String track_name, String album_name, String artist_name) {
    @Override
    public String toString() {
        return track_name + ' ' + album_name + ' ' + artist_name;
    }
}
