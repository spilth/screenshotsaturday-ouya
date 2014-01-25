package org.spilth.screenshotsaturday.loaders;

import java.util.ArrayList;
import java.util.List;

import org.spilth.screenshotsaturday.models.Screenshot;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterScreenshotLoader implements ScreenshotLoader {
  private static final int QUERY_COUNT = 100;

  private Query query = new Query("#screenshotsaturday");
  private Twitter twitter;
  private QueryResult result;

  public TwitterScreenshotLoader() {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setUseSSL(true);
    cb.setApplicationOnlyAuthEnabled(true);

    TwitterFactory tf = new TwitterFactory(cb.build());
    twitter = tf.getInstance();
    twitter.setOAuthConsumer("FOO",
        "BAR");

    query.setCount(QUERY_COUNT);
    query.setResultType(Query.RECENT);
  }

  public List<Screenshot> loadScreenshots() {
    List<Status> tweets = new ArrayList<Status>();

    try {
      twitter.getOAuth2Token();
      result = twitter.search(query);
      tweets = result.getTweets();

    } catch (TwitterException te) {
      te.printStackTrace();
    }

    List<Screenshot> imageTweets = filterTweets(tweets);

    return imageTweets;
  }

  public List<Screenshot> loadMoreScreenshots() {
    List<Status> tweets = new ArrayList<Status>();

    try {
      query = result.nextQuery();
      query.setCount(QUERY_COUNT);
      query.setResultType(Query.RECENT);

      result = twitter.search(query);
      tweets = result.getTweets();

    } catch (TwitterException te) {
      te.printStackTrace();
      System.exit(-1);
    }

    return filterTweets(tweets);
  }

  private List<Screenshot> filterTweets(List<Status> tweets) {
    List<Screenshot> screenshots = new ArrayList<Screenshot>();

    for (Status status : tweets) {
      if (status.getMediaEntities().length > 0
          && !status.getText().startsWith("RT")
          && !status.isPossiblySensitive()) {
        screenshots.add(new Screenshot("twitter", status.getUser()
              .getName(), "@" + status.getUser().getScreenName(),
              status.getText(), status.getMediaEntities()[0]
              .getMediaURL(), status.getUser()
              .getBiggerProfileImageURL(), "http://twitter.com/"
              + status.getUser().getScreenName() + "/status/"
              + status.getId(), status.getCreatedAt()));
          }
    }

    return screenshots;
  }
}
