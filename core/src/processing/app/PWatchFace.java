package processing.app;

import android.content.Intent;
//import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.Gles2WatchFaceService;
//import android.support.wearable.watchface.CanvasWatchFaceService
import processing.core.PApplet;

public class PWatchFace extends Gles2WatchFaceService implements PContainer {

  private PApplet sketch;

  public void initDimensions() {
  }

  public int getWidth() {
    return 0;
  }

  public int getHeight() {
    return 0;
  }

  public int getKind() {
    return WATCHFACE;
  }

  @Override
  public void startActivity(Intent intent) {
  }

  public void setSketch(PApplet sketch) {
    this.sketch = sketch;
  }



}
