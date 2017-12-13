package com.zyl.mp3cutter.mp3cut.bean;

public class Mp3Info
{
  private String title;
  private String artist;
  private String album;
  private int trackLength;
  private String biteRate;

  public String getTitle()
  {
    return this.title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getArtist() {
    return this.artist;
  }
  public void setArtist(String artist) {
    this.artist = artist;
  }
  public String getAlbum() {
    return this.album;
  }
  public void setAlbum(String album) {
    this.album = album;
  }
  public int getTrackLength() {
    return this.trackLength;
  }
  public void setTrackLength(int trackLength) {
    this.trackLength = trackLength;
  }
  public String getTrackLengthAsString() {
    return this.trackLength / 60 + ":" + this.trackLength % 60;
  }
  public String getBiteRate() {
    return this.biteRate;
  }
  public void setBiteRate(String biteRate) {
    this.biteRate = biteRate;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("title:" + this.title + ",");
    sb.append("artist:" + this.artist + ",");
    sb.append("album:" + this.album + ",");
    sb.append("trackLength:" + this.trackLength);
    sb.append("biteRate:" + this.biteRate);
    return sb.toString();
  }
}