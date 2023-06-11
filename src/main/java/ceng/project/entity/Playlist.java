package ceng.project.entity;

import java.util.List;
import java.util.stream.Collectors;

public record Playlist(Integer pid, String name, List<Track> tracks) {

    public String content() {
        return tracks.stream().map(Track::content).collect(Collectors.joining("\n"));
    }

    public String trackUriList() {
        return tracks.stream().map(Track::track_uri).collect(Collectors.joining(","));
    }
}
