package com.github.kaogurai.plugin;

import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;
import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import java.net.URI;

public class DeezerAudioTrack extends DelegatedAudioTrack {

    private final AudioTrackInfo trackInfo;
    private final DeezerAudioSourceManager sourceManager;

    public DeezerAudioTrack(AudioTrackInfo trackInfo, DeezerAudioSourceManager manager) {
        super(trackInfo);
        this.trackInfo = trackInfo;
        this.sourceManager = manager;
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        try (HttpInterface httpInterface = this.sourceManager.getHttpInterface()) {
            try (PersistentHttpStream stream = new PersistentHttpStream(httpInterface, new URI(this.trackInfo.uri),
                    this.trackInfo.length)) {
                processDelegate(new Mp3AudioTrack(this.trackInfo, stream), executor);
            }
        }
    }

}
