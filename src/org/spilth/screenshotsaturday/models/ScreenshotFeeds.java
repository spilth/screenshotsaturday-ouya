package org.spilth.screenshotsaturday.models;

import java.util.ArrayList;
import java.util.List;

import org.spilth.screenshotsaturday.ScreenshotSaturdayActivity;
import org.spilth.screenshotsaturday.loaders.RedditScreenshotLoader;
import org.spilth.screenshotsaturday.loaders.ScreenshotLoader;
import org.spilth.screenshotsaturday.loaders.TumblrScreenshotLoader;
import org.spilth.screenshotsaturday.loaders.TwitterScreenshotLoader;

import android.os.AsyncTask;

public class ScreenshotFeeds {
  //	private boolean loadingMoreScreenshot = false;

  private ScreenshotLoader twitterScreenshotLoader;
  private ScreenshotLoader redditScreenshotLoader;
  private ScreenshotLoader tumblrScreenshotLoader;

  private ScreenshotCollection twitterScreenshots = new ScreenshotCollection();
  private ScreenshotCollection redditScreenshots = new ScreenshotCollection();
  private ScreenshotCollection tumblrScreenshots = new ScreenshotCollection();
  private ScreenshotCollection screenshots = new ScreenshotCollection();

  private List<ScreenshotCollection> collections = new ArrayList<ScreenshotCollection>();
  private int screenshotCollectionIndex = 0;
  private ScreenshotSaturdayActivity activity;

  public ScreenshotFeeds(ScreenshotSaturdayActivity screenshotSaturdayActivity) {
    this.activity = screenshotSaturdayActivity;

    twitterScreenshotLoader = new TwitterScreenshotLoader();
    redditScreenshotLoader = new RedditScreenshotLoader();
    tumblrScreenshotLoader = new TumblrScreenshotLoader();

    collections.add(twitterScreenshots);
    collections.add(tumblrScreenshots);
    collections.add(redditScreenshots);

    new FetchTwitterScreenshots().execute();
    new FetchRedditScreenshots().execute();
    new FetchTumblrScreenshots().execute();
  }

  public ScreenshotCollection getNextCollection() {
    if (screenshotCollectionIndex == collections.size() - 1) {
      screenshotCollectionIndex = 0;
    } else {
      screenshotCollectionIndex++;
    }
    screenshots = collections.get(screenshotCollectionIndex);
    return screenshots;
  }

  public ScreenshotCollection getPreviousCollection() {
    if (screenshotCollectionIndex == 0) {
      screenshotCollectionIndex = collections.size() - 1;
    } else {
      screenshotCollectionIndex--;
    }
    screenshots = collections.get(screenshotCollectionIndex);
    return screenshots;
  }

  private class FetchTwitterScreenshots extends
    AsyncTask<Void, Void, List<Screenshot>> {
      @Override
      protected List<Screenshot> doInBackground(Void... arg0) {
        return twitterScreenshotLoader.loadScreenshots();
      }

      @Override
      protected void onPostExecute(List<Screenshot> newScreenshots) {
        if (newScreenshots.size() == 0) {
          // showConnectionAlert();
        } else {
          twitterScreenshots.addAll(newScreenshots);
          screenshots = twitterScreenshots;
          // showFirstScreenshot();
          activity.collectionReady();
        }
      }
    }

  //	private class FetchMoreTwitterScreenshots extends
  //			AsyncTask<Void, Void, List<Screenshot>> {
  //		@Override
  //		protected List<Screenshot> doInBackground(Void... arg0) {
  //			loadingMoreScreenshot = true;
  //			return twitterScreenshotLoader.loadMoreScreenshots();
  //		}
  //
  //		@Override
  //		protected void onPostExecute(List<Screenshot> newScreenshots) {
  //			twitterScreenshots.addAll(newScreenshots);
  //			screenshots = twitterScreenshots;
  //			// showNextScreenshot();
  //			loadingMoreScreenshot = false;
  //		}
  //	}

  private class FetchRedditScreenshots extends
    AsyncTask<Void, Void, List<Screenshot>> {
      @Override
      protected List<Screenshot> doInBackground(Void... arg0) {
        return redditScreenshotLoader.loadScreenshots();
      }

      @Override
      protected void onPostExecute(List<Screenshot> newScreenshots) {
        System.out.println(newScreenshots.size());
        if (newScreenshots.size() == 0) {
          // showConnectionAlert();
        } else {
          redditScreenshots.addAll(newScreenshots);
          // screenshots = redditScreenshots;
          // showFirstScreenshot();
        }
      }
    }

  private class FetchTumblrScreenshots extends
    AsyncTask<Void, Void, List<Screenshot>> {
      @Override
      protected List<Screenshot> doInBackground(Void... arg0) {
        return tumblrScreenshotLoader.loadScreenshots();
      }

      @Override
      protected void onPostExecute(List<Screenshot> newScreenshots) {
        System.out.println(newScreenshots.size());
        if (newScreenshots.size() == 0) {
          // showConnectionAlert();
        } else {
          tumblrScreenshots.addAll(newScreenshots);
          // screenshots = tumblrScreenshots;
          // showFirstScreenshot();
        }
      }

    }

  public ScreenshotCollection getCurrentCollection() {
    return collections.get(screenshotCollectionIndex);
  }
}
