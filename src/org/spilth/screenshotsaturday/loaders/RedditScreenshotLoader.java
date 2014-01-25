package org.spilth.screenshotsaturday.loaders;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.matshofman.saxrssreader.RssFeed;
import nl.matshofman.saxrssreader.RssItem;
import nl.matshofman.saxrssreader.RssReader;

import org.spilth.screenshotsaturday.models.Screenshot;
import org.xml.sax.SAXException;

public class RedditScreenshotLoader implements ScreenshotLoader {
  //private static String screenshotSaturdayFeed = "http://www.reddit.com/r/gamedev/search.rss?q=Screenshot+Saturday&sort=new&restrict_sr=on&t=all";

  public List<Screenshot> loadScreenshots() {
    List<Screenshot> screenshots = new ArrayList<Screenshot>();

    URL url;

    try {
      url = new URL(getLatestUrl());

      RssFeed feed = RssReader.read(url);

      ArrayList<RssItem> rssItems = feed.getRssItems();
      for (RssItem item : rssItems) {
        List<String> imageUrls = getImageUrls(item);

        if (imageUrls.size() > 0) {
          for (String imageUrl : imageUrls) {
            screenshots.add(new Screenshot(
                  "reddit",
                  item.getTitle(),
                  null,
                  "",
                  imageUrl,
                  null,
                  item.getLink().toString(),
                  null
                  ));
          }
          // System.out.println(item.getElementValue("http://purl.org/dc/elements/1.1/",
          // "date"));
        }
      }

    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return screenshots;
  }

  public List<Screenshot> loadMoreScreenshots() {
    List<Screenshot> screenshots = new ArrayList<Screenshot>();

    return screenshots;
  }

  private String getLatestUrl() {

    return "http://www.reddit.com/r/gamedev/comments/1rr3zt/screenshot_saturday_147_turkey_sandwich_saturday.rss?limit=500&sort=new";
    //		String latestUrl = null;
    //		
    //		URL url;
    //		try {
    //			url = new URL(screenshotSaturdayFeed);
    //
    //			RssFeed feed = RssReader.read(url);
    //
    //			ArrayList<RssItem> rssItems = feed.getRssItems();
    //			RssItem first = rssItems.get(0);
    //			latestUrl = first.getLink();
    //
    //		} catch (MalformedURLException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		} catch (SAXException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		} catch (IOException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		}
    //		
    //		return latestUrl;
  }

  private static List<String> getImageUrls(RssItem item) {
    List<String> imageUrls = new ArrayList<String>();

    String pattern = "(http(s?):/)(/[^/]+)+.(?:jpg|png)";
    Pattern patt = Pattern.compile(pattern);
    Matcher matcher = patt.matcher(item.getDescription());
    while (matcher.find()) {
      imageUrls.add(matcher.group(0));
    }
    return imageUrls;
  }

}
