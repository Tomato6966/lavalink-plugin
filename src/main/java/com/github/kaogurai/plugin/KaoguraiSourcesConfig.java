package com.github.kaogurai.plugin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "plugins.kaogurai.sources")
@Component
public class KaoguraiSourcesConfig{

	private boolean deezer = true;

	public boolean isDeezer(){
		return this.deezer;
	}

	public void setDeezer(boolean deezer){
		this.deezer = deezer;
	}

}