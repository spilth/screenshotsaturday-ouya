package org.spilth.screenshotsaturday.loaders;

import java.util.ArrayList;
import java.util.List;

import org.spilth.screenshotsaturday.models.Screenshot;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;

public class TumblrScreenshotLoader implements ScreenshotLoader {
  JumblrClient client;

  public TumblrScreenshotLoader() {
    client = new JumblrClient("FOO", "BAR");
  }

  @Override
  public List<Screenshot> loadScreenshots() {
    List<Screenshot> screenshots = new ArrayList<Screenshot>();

    for (Post post : client.tagged("screenshotsaturday")) {
      if (post instanceof PhotoPost) {
        post = (PhotoPost) post;
        Blog blog = client.blogInfo(post.getBlogName());

        for (Photo photo : ((PhotoPost) post).getPhotos()) {
          screenshots.add(new Screenshot(
                "tumblr",
                blog.getTitle(),
                blog.getName(),
                photo.getCaption(),
                photo.getSizes().get(0).getUrl(),
                blog.avatar(64),
                post.getPostUrl(),
                null
                ));
        }
      }
    }
    return screenshots;
  }

  @Override
  public List<Screenshot> loadMoreScreenshots() {
    return null;
  }

}
