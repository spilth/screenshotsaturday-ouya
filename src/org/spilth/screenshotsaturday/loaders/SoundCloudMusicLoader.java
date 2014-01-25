package org.spilth.screenshotsaturday.loaders;

import java.util.ArrayList;
import java.util.List;

import org.spilth.screenshotsaturday.models.Song;

//import com.soundcloud.api.ApiWrapper;

public class SoundCloudMusicLoader {
  //	private ApiWrapper wrapper;

  public SoundCloudMusicLoader() {
    //		wrapper = new ApiWrapper("FOO", "BAR", null, null);
  }

  public List<Song> loadSongs() {
    List<Song> songs = new ArrayList<Song>();

    //		try {
    //			Request request = Request.to("/tracks").with("track[user]", 79678272);
    //			HttpResponse resp = wrapper.get(request);
    //			//System.out.println("\n" + Http.formatJSON(Http.getString(resp)));
    //		} catch (IOException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		}

    songs.add(new Song(
          "soundcloud",
          "Dunderpate Music ",
          "Dunderpate Music ",
          "The Mystics of Voss",
          "http://api.soundcloud.com/tracks/88038196/stream?consumer_key=FOO",
          "https://i2.sndcdn.com/artworks-000045600268-5qncbn-t120x120.jpg?cc07a88",
          "https://soundcloud.com/dunderpate-music/the-mystics-of-voss",
          null
          ));

    songs.add(new Song(
          "soundcloud",
          "spilth",
          "spilth",
          "Boards of Valhalla",
          "http://api.soundcloud.com/tracks/5207429/stream?consumer_key=FOO",
          "https://i3.sndcdn.com/avatars-000001709164-f7jyen-t120x120.jpg?cc07a88",
          "https://soundcloud.com/spilth/borders-of-valhalla",
          null
          ));

    songs.add(new Song(
          "soundcloud",
          "spilth",
          "spilth",
          "First Flight 2",
          "https://api.soundcloud.com/tracks/77746830/stream?consumer_key=FOO",
          "https://i3.sndcdn.com/avatars-000001709164-f7jyen-t120x120.jpg?cc07a88",
          "https://soundcloud.com/spilth/first-flight-2",
          null
          ));

    songs.add(new Song(
          "soundcloud",
          "spilth",
          "spilth",
          "Electric Space Piano",
          "https://api.soundcloud.com/tracks/79678272/stream?consumer_key=FOO",
          "https://i3.sndcdn.com/avatars-000001709164-f7jyen-t120x120.jpg?cc07a88",
          "https://soundcloud.com/spilth/electric-space-piano",
          null
          ));

    return songs;
  }

}
