package org.spilth.screenshotsaturday.models;

import java.util.Date;

public class Song {
  private String provider;
  private String name;
  private String username;
  private String title;
  private String audioUrl;
  private String avatarUrl;
  private String url;
  private Date date;

  public Song(String provider, String name, String username, String title, String audioUrl, String avatarUrl, String url, Date date) {
    this.provider = provider;
    this.name = name;
    this.username = username;
    this.title = title;
    this.audioUrl = audioUrl;
    this.avatarUrl = avatarUrl;
    this.url = url;
    this.date = date;
  }

  public String getProvider() {
    return provider;
  }

  public String getName() {
    return name;
  }

  public String getUsername() {
    return username;
  }

  public String getTitle() {
    return title;
  }

  public String getAudioUrl() {
    return audioUrl;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public String getUrl() {
    return url;
  }

  public Date getDate() {
    return date;
  }

}
