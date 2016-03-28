package processing.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import processing.core.PApplet;
import processing.core.PGraphics;
import android.graphics.Rect;

public class PWatchFace extends Gles2WatchFaceService implements PContainer {

  private DisplayMetrics metrics;
  private PApplet sketch;
  private PGraphics graphics;

  private static final long TICK_PERIOD_MILLIS = 100;
  private Handler timeTick;

  public void initDimensions() {
    metrics = new DisplayMetrics();
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
      setWatchFaceStyle(new WatchFaceStyle.Builder(PWatchFace.this)
              .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
              .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
              .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
              .setShowSystemUiTime(false)
              .build());
      timeTick = new Handler(Looper.myLooper());
      startTimerIfNecessary();
      if (sketch != null) {
        sketch.initSurface(PWatchFace.this, null);
        graphics = sketch.g;
      }
    }

    private void startTimerIfNecessary() {
      timeTick.removeCallbacks(timeRunnable);
      if (isVisible() && !isInAmbientMode()) {
        timeTick.post(timeRunnable);
      }
    }

    private final Runnable timeRunnable = new Runnable() {
      @Override
      public void run() {
        onSecondTick();

        if (isVisible() && !isInAmbientMode()) {
          timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
        }
      }
    };

    private void onSecondTick() {
      invalidateIfNecessary();
    }

    private void invalidateIfNecessary() {
      if (isVisible() && !isInAmbientMode()) {
        invalidate();
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
      sketch.surfaceChanged();
    }

    @Override
    public void onAmbientModeChanged(boolean inAmbientMode) {
      super.onAmbientModeChanged(inAmbientMode);
//      invalidate();
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
      startTimerIfNecessary();
    }

    @Override
    public void onPeekCardPositionUpdate(Rect rect) {

    }

    @Override
    public void onTimeTick() {
      invalidate();
    }

    int frame = 0;
    @Override
    public void onDraw() {
      super.onDraw();

//      GLES20.glClearColor(sketch.random(1), sketch.random(1), sketch.random(1), 1);
//      GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//      PApplet.println("FRAME: " + frame);
//      frame++;
      PApplet.println("Calling handleDraw: " + sketch.width + " " + sketch.height);
      sketch.handleDraw();

      // Draw every frame as long as we're visible and in interactive mode.
//      if (isVisible() && !isInAmbientMode()) {
//          invalidate();
//      }
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


  @Override
  public void onDestroy() {
    sketch.onDestroy();
    super.onDestroy();
  }
}
