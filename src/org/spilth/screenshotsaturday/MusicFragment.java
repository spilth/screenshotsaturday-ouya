package org.spilth.screenshotsaturday;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spilth.screenshotsaturday.loaders.ImageLoader;
import org.spilth.screenshotsaturday.loaders.SoundCloudMusicLoader;
import org.spilth.screenshotsaturday.models.Song;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicFragment extends Fragment implements OnCompletionListener {
  private TextView musicNameText;
  private TextView musicTitleText;
  private ImageView musicAvatarImage;
  private SoundCloudMusicLoader soundCloudMusicLoader;
  private List<Song> songs = new ArrayList<Song>();

  private Song currentSong;
  private int currentSongIndex;

  private MediaPlayer songPlayer = new MediaPlayer();

  int avatarCacheSize = 4 * 1024 * 1024;
  LruCache<String, Bitmap> avatarCache = new LruCache<String, Bitmap>(avatarCacheSize) {
    protected int sizeOf(String key, Bitmap value) {
      return value.getByteCount();
    }
  };

  private class FetchSoundCloudSongs extends
    AsyncTask<Void, Void, List<Song>> {
      @Override
      protected List<Song> doInBackground(Void... arg0) {
        return soundCloudMusicLoader.loadSongs();
      }

      @Override
      protected void onPostExecute(List<Song> newSongs) {
        songs.addAll(newSongs);
        playFirstSong();
      }
    }

  private class StreamSong extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... urls) {
      String url = urls[0];
      try {
        songPlayer.reset();
        songPlayer.setDataSource(url);
        songPlayer.prepare(); // might take long! (for buffering, etc)
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalStateException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      songPlayer.start();

      return null;
    }
  }

  private class FetchMusicAvatar extends AsyncTask<String, Void, Bitmap> {
    String url;

    @Override
    protected Bitmap doInBackground(String... urls) {
      url = urls[0];
      return ImageLoader.getBitmapFromUrl(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
      avatarCache.put(url, bitmap);
      showMusicAvatar(url);
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    soundCloudMusicLoader = new SoundCloudMusicLoader();

    new FetchSoundCloudSongs().execute();

    songPlayer.setOnCompletionListener(this);
    songPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate( R.layout.fragment_music, container, false);

    musicNameText = (TextView) v.findViewById(R.id.music_name);
    musicTitleText = (TextView) v.findViewById(R.id.music_title);
    musicAvatarImage = (ImageView) v.findViewById(R.id.music_avatar);

    return v;
  }

  private void showMusicAvatar(String url) {
    if (url == currentSong.getAvatarUrl()) {
      musicAvatarImage.setImageBitmap(avatarCache.get(url));
    }
  }

  private void playFirstSong() {
    currentSong = songs.get(0);
    playSong();
  }

  private void playSong() {
    musicNameText.setText(currentSong.getName());
    musicTitleText.setText(currentSong.getTitle());

    if (avatarCache.get(currentSong.getAvatarUrl()) != null) {
      showMusicAvatar(currentSong.getAvatarUrl());
    } else {
      musicAvatarImage.setImageBitmap(null);
      new FetchMusicAvatar().execute(currentSong.getAvatarUrl());
    }

    new StreamSong().execute(currentSong.getAudioUrl());
  }

  void playNextSong() {
    currentSongIndex++;
    if (currentSongIndex > songs.size() - 1) currentSongIndex = songs.size() - 1;
    currentSong = songs.get(currentSongIndex);
    playSong();
  }

  void playPreviousSong() {
    currentSongIndex--;
    if (currentSongIndex < 0) currentSongIndex = 0;
    currentSong = songs.get(currentSongIndex);
    playSong();
  }

  void openSong() {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentSong.getUrl()));
    startActivity(browserIntent);		
  }

  @Override
  public void onCompletion(MediaPlayer mediaPlayer) {
    playNextSong();
  }

  @Override
  public void onPause() {
    super.onPause();

    songPlayer.release();
  }
}
