package org.spilth.screenshotsaturday.loaders;

import java.util.List;

import org.spilth.screenshotsaturday.models.Screenshot;

public interface ScreenshotLoader {	
  public List<Screenshot> loadScreenshots();
  public List<Screenshot> loadMoreScreenshots();
}
