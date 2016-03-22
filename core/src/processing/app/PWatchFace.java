package processing.app;

import android.content.Intent;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import processing.core.PApplet;
import processing.core.PGraphics;

public class PWatchFace extends Gles2WatchFaceService implements PContainer {

  private DisplayMetrics metrics;
  private PApplet sketch;
  private PGraphics graphics;

  public void initDimensions() {
    WindowManager man = (WindowManager) getSystemService(WINDOW_SERVICE);
    man.getDefaultDisplay().getMetrics(metrics);
  }

  public int getWidth() {
    return metrics.widthPixels;
  }

  public int getHeight() {
    return metrics.heightPixels;
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

  @Override
  public Engine onCreateEngine() {
      return new Engine();
  }

  private class Engine extends Gles2WatchFaceService.Engine {
    @Override
    public void onCreate(SurfaceHolder surfaceHolder) {
      super.onCreate(surfaceHolder);
      if (sketch != null) {
        sketch.initSurface(PWatchFace.this, null);
        graphics = sketch.g;
      }
    }

    @Override
    public void onGlContextCreated() {
        super.onGlContextCreated();
    }

    @Override
    public void onGlSurfaceCreated(int width, int height) {
      super.onGlSurfaceCreated(width, height);
      graphics.setSize(width, height);
    }

    @Override
    public void onAmbientModeChanged(boolean inAmbientMode) {
      super.onAmbientModeChanged(inAmbientMode);
      invalidate();
      // call new event handlers in sketch (?)
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      super.onVisibilityChanged(visible);
      if (visible) {
        sketch.onResume();
      } else {
        sketch.onPause();
      }
    }

    @Override
    public void onTimeTick() {
      invalidate();
    }

    @Override
    public void onDraw() {
      super.onDraw();

      sketch.handleDraw();

      // Draw every frame as long as we're visible and in interactive mode.
      if (isVisible() && !isInAmbientMode()) {
          invalidate();
      }
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
      sketch.surfaceTouchEvent(event);
      super.onTouchEvent(event);
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      sketch.onDestroy();
    }
  }
}
