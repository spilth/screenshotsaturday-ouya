package org.spilth.screenshotsaturday;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.spilth.screenshotsaturday.loaders.ImageLoader;
import org.spilth.screenshotsaturday.models.Screenshot;
import org.spilth.screenshotsaturday.models.ScreenshotCollection;
import org.spilth.screenshotsaturday.models.ScreenshotFeeds;

import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScreenshotSaturdayActivity extends Activity {
  private SimpleDateFormat dateFormatter = (SimpleDateFormat) SimpleDateFormat
    .getDateInstance();

  private boolean musicEnabled = false;

  private LinearLayout screenshotLayout;
  private TextView screenshotNameText;
  private TextView screenshotUsernameText;
  private TextView screenshotDescriptionText;
  private TextView screenshotDateText;
  private ImageView screenshotImage;
  private ImageView screenshotAvatarImage;
  private ImageView screenshotFeedImage;

  private LinearLayout musicLayout;

  private ProgressBar imageProgressBar;

  private ScreenshotFeeds screenshotFeeds;

  private ScreenshotCollection screenshots = new ScreenshotCollection();

  private Screenshot currentScreenshot;

  private SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
  private int nextPreviousSound;
  private int finishedLoadingSound;

  private HashMap<String, Integer> feedIconHash = new HashMap<String, Integer>();

  int imageCacheSize = 32 * 1024 * 1024;
  LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(imageCacheSize) {
    protected int sizeOf(String key, Bitmap value) {
      return value.getByteCount();
    }
  };

  int avatarCacheSize = 4 * 1024 * 1024;
  LruCache<String, Bitmap> avatarCache = new LruCache<String, Bitmap>(avatarCacheSize) {
    protected int sizeOf(String key, Bitmap value) {
      return value.getByteCount();
    }
  };

  private MusicFragment musicFragment;

  private class FetchAvatar extends AsyncTask<String, Void, Bitmap> {
    String url;

    @Override
    protected Bitmap doInBackground(String... urls) {
      url = urls[0];
      return ImageLoader.getBitmapFromUrl(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      avatarCache.put(url, bitmap);
      showScreenshotAvatar(url);
    }
  }

  private class FetchImage extends AsyncTask<String, Void, Bitmap> {
    String url;

    @Override
    protected Bitmap doInBackground(String... urls) {
      url = urls[0];
      return ImageLoader.getBitmapFromUrl(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      imageCache.put(url, bitmap);
      showScreenshotImage(url);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_screenshot_saturday);

    OuyaController.init(this);

    imageProgressBar = (ProgressBar) findViewById(R.id.image_progress);

    screenshotLayout = (LinearLayout) findViewById(R.id.layout);
    screenshotNameText = (TextView) findViewById(R.id.name);
    screenshotUsernameText = (TextView) findViewById(R.id.username);
    screenshotAvatarImage = (ImageView) findViewById(R.id.avatar);
    screenshotImage = (ImageView) findViewById(R.id.image);
    screenshotDescriptionText = (TextView) findViewById(R.id.text);
    screenshotDateText = (TextView) findViewById(R.id.timestamp);
    screenshotFeedImage = (ImageView) findViewById(R.id.feed);

    nextPreviousSound = soundPool.load(this, R.raw.next, 1);
    finishedLoadingSound = soundPool.load(this, R.raw.loaded, 1);

    musicLayout = (LinearLayout) findViewById(R.id.music_layout);

    screenshotLayout.setVisibility(View.INVISIBLE);

    screenshotFeeds = new ScreenshotFeeds(this);

    feedIconHash.put("twitter", R.drawable.twitter);
    feedIconHash.put("tumblr", R.drawable.tumblr);
    feedIconHash.put("reddit", R.drawable.reddit);

    if (musicEnabled) {
      FragmentManager fm = getFragmentManager();
      musicFragment = (MusicFragment) fm.findFragmentById(R.id.music_layout);

      if (musicFragment == null) {
        musicFragment = new MusicFragment();
        fm.beginTransaction().add(R.id.music_layout, musicFragment).commit();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.screenshot_saturday, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        getFragmentManager().beginTransaction()
          .replace(android.R.id.content, new Settings())
          .commit();
        return true;

      case R.id.action_help:
        Intent i = new Intent(ScreenshotSaturdayActivity.this, HelpActivity.class);
        startActivity(i);
        return true;

      case R.id.action_quit:
        finish();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onKeyDown(final int keyCode, KeyEvent event) {
    boolean handled = false;

    if (screenshots.size() > 0) {
      switch (keyCode) {
        case OuyaController.BUTTON_DPAD_RIGHT:
          showNextScreenshot();
          handled = true;
          break;

        case OuyaController.BUTTON_DPAD_LEFT:
          showPreviousScreenshot();
          handled = true;
          break;

        case OuyaController.BUTTON_DPAD_UP:
          showNextScreenshotFeed();
          handled = true;
          break;

        case OuyaController.BUTTON_DPAD_DOWN:
          showPreviousScreenshotFeed();
          handled = true;
          break;

        case OuyaController.BUTTON_U:
        case KeyEvent.KEYCODE_ENTER:
          openScreenshot();
          handled = true;
          break;

        case OuyaController.BUTTON_O:
        case KeyEvent.KEYCODE_TAB:
          toggleDescriptions();
          handled = true;
          break;

        case KeyEvent.KEYCODE_H:
        case OuyaController.BUTTON_MENU:
          handled = false;
          break;				
      }
    }

    if (musicEnabled && handled == false) {
      switch (keyCode) {
        case KeyEvent.KEYCODE_RIGHT_BRACKET:
        case OuyaController.BUTTON_R1:
          musicFragment.playNextSong();
          handled = true;
          break;

        case KeyEvent.KEYCODE_LEFT_BRACKET:
        case OuyaController.BUTTON_L1:
          musicFragment.playPreviousSong();
          handled = true;
          break;

        case OuyaController.BUTTON_A:
          //playPauseSong();
          handled = true;
          break;

        case OuyaController.BUTTON_Y:
          musicFragment.openSong();
          handled = true;
          break;
      }
    }

    return handled;
  }

  private void showNextScreenshot() {
    if (!screenshots.hasNext()) {
      //loadMoreTwitterScreenshots();
    } else {
      currentScreenshot = screenshots.getNext();
      showCurrentScreenshot();
    }
  }

  private void showPreviousScreenshot() {
    if (screenshots.hasPrevious()) {
      currentScreenshot = screenshots.getPrevious();
      showCurrentScreenshot();
    }
  }

  private void showNextScreenshotFeed() {
    screenshots = screenshotFeeds.getNextCollection();
    currentScreenshot = screenshots.getCurrent();
    showCurrentScreenshot();
  }

  private void showPreviousScreenshotFeed() {
    screenshots = screenshotFeeds.getPreviousCollection();
    currentScreenshot = screenshots.getCurrent();
    showCurrentScreenshot();
  }

  private void openScreenshot() {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentScreenshot.getUrl()));
    startActivity(browserIntent);
  }

  private void showFirstScreenshot() {
    currentScreenshot = screenshots.getCurrent();
    screenshotLayout.setVisibility(View.VISIBLE);
    showCurrentScreenshot();
  }

  private void showCurrentScreenshot() {
    screenshotFeedImage.setImageResource(feedIconHash.get(currentScreenshot.getProvider()));

    screenshotNameText.setText(currentScreenshot.getName());
    if (currentScreenshot.getName() == null) {
      screenshotNameText.setVisibility(View.GONE);	
    } else {
      screenshotNameText.setVisibility(View.VISIBLE);
    }

    screenshotUsernameText.setText(currentScreenshot.getUsername());
    if (currentScreenshot.getUsername() == null) {
      screenshotUsernameText.setVisibility(View.GONE);	
    } else {
      screenshotUsernameText.setVisibility(View.VISIBLE);
    }

    screenshotDescriptionText.setText(currentScreenshot.getText());		
    if (currentScreenshot.getText().equals("")) {
      screenshotDescriptionText.setVisibility(View.GONE);	
    } else {
      screenshotDescriptionText.setVisibility(View.VISIBLE);
    }

    if (currentScreenshot.getDate() == null) {
      screenshotDateText.setText(null);
      screenshotDateText.setVisibility(View.GONE);
    } else {
      screenshotDateText.setText(dateFormatter.format(currentScreenshot.getDate()));
      screenshotDateText.setVisibility(View.VISIBLE);
    }

    if (currentScreenshot.getProvider().equals("reddit")) {
      screenshotAvatarImage.setVisibility(View.GONE);			
    } else {
      screenshotAvatarImage.setVisibility(View.VISIBLE);
      if (avatarCache.get(currentScreenshot.getAvatarUrl()) != null) {
        showScreenshotAvatar(currentScreenshot.getAvatarUrl());
      } else {
        screenshotAvatarImage.setImageBitmap(null);
        new FetchAvatar().execute(currentScreenshot.getAvatarUrl());
      }
    }

    if (imageCache.get(currentScreenshot.getImageUrl()) != null) {
      showScreenshotImage(currentScreenshot.getImageUrl());
    } else {
      soundPool.play(nextPreviousSound, 1.0f, 1.0f, 0, 0, 1.0f);
      imageProgressBar.setVisibility(View.VISIBLE);
      screenshotImage.setImageBitmap(null);
      new FetchImage().execute(currentScreenshot.getImageUrl());
    }
  }

  //	private void loadMoreTwitterScreenshots() {
  //		if (!loadingMoreScreenshot) {
  //			new FetchMoreTwitterScreenshots().execute();
  //		}
  //	}

  private void showScreenshotImage(String url) {
    if (url == currentScreenshot.getImageUrl()) {
      imageProgressBar.setVisibility(View.INVISIBLE);
      screenshotImage.setImageBitmap(imageCache.get(url));
      soundPool.play(finishedLoadingSound, 1.0f, 1.0f, 0, 0, 1.0f);
    }
  }

  private void showScreenshotAvatar(String url) {
    if (url == currentScreenshot.getAvatarUrl()) {
      screenshotAvatarImage.setImageBitmap(avatarCache.get(url));
    }
  }

  private void toggleDescriptions() {
    if (screenshotLayout.isShown()) {
      screenshotLayout.setVisibility(View.INVISIBLE);
      musicLayout.setVisibility(View.INVISIBLE);
    } else {
      screenshotLayout.setVisibility(View.VISIBLE);
      musicLayout.setVisibility(View.VISIBLE);
    }
  }

  public void showConnectionAlert() {
    AlertDialog.Builder alert = new AlertDialog.Builder(
        ScreenshotSaturdayActivity.this);
    alert.setMessage(R.string.no_network_error);
    alert.setCancelable(false);
    alert.setPositiveButton("Quit",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            System.exit(-1);
          }
        });
    alert.show();
  }

  public void collectionReady() {
    screenshots = screenshotFeeds.getCurrentCollection();
    showFirstScreenshot();
  }
}
