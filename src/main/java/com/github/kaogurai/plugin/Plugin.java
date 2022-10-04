package com.github.kaogurai.plugin;
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

@Service
public class Plugin implements AudioPlayerManagerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(Plugin.class);

    private final KaoguraiSourcesConfig sourcesConfig;
    private final DeezerPluginConfig deezerPluginConfig;

    public Plugin(KaoguraiSourcesConfig sourcesConfig, DeezerPluginConfig deezerPluginConfig) {
        this.sourcesConfig = sourcesConfig;
        this.deezerPluginConfig = deezerPluginConfig;
    }

    @Override
    public AudioPlayerManager configure(AudioPlayerManager manager) {
        if (sourcesConfig.isDeezer()) {
            log.info("Enabling Deezer plugin");
            manager.registerSourceManager(new DeezerAudioSourceManager(deezerPluginConfig.getProxyURL(), deezerPluginConfig.getAuthKey()));
        }
        return manager;
    }
    
}
