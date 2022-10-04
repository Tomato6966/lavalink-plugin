package com.github.kaogurai.plugin;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.URLEncoder;
import java.io.IOException;
import org.slf4j.Logger;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;

import java.util.ArrayList;
import java.util.List;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterfaceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.LoggerFactory;

public class DeezerAudioSourceManager implements AudioSourceManager {

  private static final Logger log = LoggerFactory.getLogger(Plugin.class);

  private String DEEZER_SEARCH_PREFIX = "dsearch:";
  private String DEEZER_SEARCH_ISRC_PREFIX = "isrc:";
  private String proxyURL;
  private String authKey;

  public final HttpInterfaceManager httpInterfaceManager = HttpClientTools.createDefaultThreadLocalManager();

  public DeezerAudioSourceManager(String proxyURL, String authKey) {
    this.proxyURL = proxyURL;
    this.authKey = authKey;
  }

  @Override
  public String getSourceName() {
    return "deezer";
  }

  @Override
  public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {

    try {

      if (reference.identifier.startsWith(DEEZER_SEARCH_PREFIX)) {
        if (reference.identifier.startsWith(DEEZER_SEARCH_PREFIX + DEEZER_SEARCH_ISRC_PREFIX)) {
          return loadItemByIsrc(manager,
              reference.identifier.substring(DEEZER_SEARCH_PREFIX.length() + DEEZER_SEARCH_ISRC_PREFIX.length()));
        } else {
          return loadItemBySearch(manager, reference.identifier.substring(DEEZER_SEARCH_PREFIX.length()));
        }
      }

      // Support for deezer.com URLs and deezer.page.link URLs will be added later

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  private AudioItem loadItemByIsrc(AudioPlayerManager manager, String isrc) throws IOException {

    HttpInterface httpInterface = httpInterfaceManager.getInterface();

    try {

      String url = proxyURL + "/v1/track/info/isrc:" + isrc;

      var r = httpInterface.execute(new HttpGet(url));


      if (r.getStatusLine().getStatusCode() != 200) {
        log.error("Deezer API returned status code " + r.getStatusLine().getStatusCode());
        return null;
      }

      JsonBrowser json = JsonBrowser.parse(r.getEntity().getContent());

      String title = json.get("name").text();
      String artist = json.get("artist").get("name").text();
      long length = json.get("duration").as(Long.class) * 1000;
      int id = json.get("id").as(Integer.class);

      return new DeezerAudioTrack(
          new AudioTrackInfo(title, artist, length, isrc, false, proxyURL + "/v1/track/download/" + id),
          this);

    } finally {
      httpInterface.close();
    }

  }

  private AudioItem loadItemBySearch(AudioPlayerManager manager, String query) throws IOException {
    HttpInterface httpInterface = httpInterfaceManager.getInterface();

    try {

      String url = proxyURL + "/v1/search?query=" + URLEncoder.encode(query, "UTF-8");

      var r = httpInterface.execute(new HttpGet(url));

      if (r.getStatusLine().getStatusCode() != 200) {
        return null;
      }

      JsonBrowser json = JsonBrowser.parse(r.getEntity().getContent());

      var tracks = json.get("tracks").values();

      if (tracks.size() == 0) {
        return null;
      }

      List<AudioTrack> playlistTracks = new ArrayList<>();

      for (var track : tracks) {
        String title = track.get("name").text();
        String artist = track.get("artist").get("name").text();
        long length = track.get("duration").as(Long.class) * 1000;
        String id = track.get("id").text();

        playlistTracks.add(new DeezerAudioTrack(
            new AudioTrackInfo(title, artist, length, id, false, proxyURL + "/v1/track/download/" + id),
            this));
      }

      return new BasicAudioPlaylist("Deezer Search: " + query, playlistTracks, null, false);

    } finally {
      httpInterface.close();
    }

  }

  public HttpInterface getHttpInterface() {
    return httpInterfaceManager.getInterface();
  }

  @Override
  public void shutdown() {
    try {
      this.httpInterfaceManager.close();
    } catch (IOException e) {
      log.error("Failed to close HTTP interface manager", e);
    }
  }

  @Override
  public boolean isTrackEncodable(AudioTrack track) {
    return false;
  }

  @Override
  public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {

  }

  @Override
  public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
    return new DeezerAudioTrack(trackInfo, this);
  }

}
