package org.spilth.screenshotsaturday;

import tv.ouya.console.api.OuyaController;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;

public class HelpActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.help, menu);
    return false;
  }

  @Override
  public boolean onKeyDown(final int keyCode, KeyEvent event) {
    boolean handled = false;

    switch (keyCode) {
      case OuyaController.BUTTON_O:
      case OuyaController.BUTTON_A:
      case KeyEvent.KEYCODE_SPACE:
        Intent i = new Intent(HelpActivity.this, ScreenshotSaturdayActivity.class);
        startActivity(i);
        handled = true;
        break;
    }

    return handled;
  }

}
