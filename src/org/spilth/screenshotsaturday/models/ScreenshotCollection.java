package org.spilth.screenshotsaturday.models;

import java.util.ArrayList;
import java.util.List;


public class ScreenshotCollection {
  private List<Screenshot> screenshots = new ArrayList<Screenshot>();

  private int currentIndex = 0;

  public void addAll(List<Screenshot> screenshots) {
    this.screenshots.addAll(screenshots);
  }

  public int size() {
    return screenshots.size();
  }

  public boolean hasPrevious() {
    return currentIndex != 0;
  }

  public boolean hasNext() {
    return !(currentIndex == screenshots.size() - 1);
  }

  public Screenshot getCurrent() {
    return screenshots.get(currentIndex);
  }

  public Screenshot getPrevious() {
    currentIndex--;
    return getCurrent();
  }

  public Screenshot getNext() {
    currentIndex++;
    return getCurrent();
  }
}
