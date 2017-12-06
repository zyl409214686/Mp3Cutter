/**
 * Copyright 2011, Felix Palmer
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.zyl.mp3cutter.common.ui.view.visualizer;

// Data class to explicitly indicate that these bytes are raw audio data
public class AudioData
{
  public AudioData(byte[] bytes)
  {
    this.bytes = bytes;
  }

  public AudioData() {
  }

  public byte[] bytes;

  public AudioData setBytes(byte[] bytes) {
    this.bytes = bytes;
    return this;
  }
}
