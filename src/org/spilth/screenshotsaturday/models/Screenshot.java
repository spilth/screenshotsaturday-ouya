package org.spilth.screenshotsaturday.models;

import java.util.Date;

public class Screenshot {
  private String provider;
  private String name;
  private String username;
  private String text;
  private String imageUrl;
  private String avatarUrl;
  private String url;
  private Date date;

  public Screenshot(String provider, String name, String username, String text, String imageUrl, String avatarUrl, String url, Date date) {
    this.provider = provider;
    this.name = name;
    this.username = username;
    this.text = text;
    this.imageUrl = imageUrl;
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

  public String getText() {
    return text;
  }

  public String getImageUrl() {
    return imageUrl;
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
