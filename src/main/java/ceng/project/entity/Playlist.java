package ceng.project.entity;

import java.util.List;
import java.util.stream.Collectors;

public record Playlist(Integer pid, String name, List<Track> tracks) {

    public String content() {
        return tracks.stream().map(Track::toString).collect(Collectors.joining("\n"));
    }
}
