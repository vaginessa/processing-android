package processing.app;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import processing.core.PApplet;
import processing.opengl.PGLES;
import processing.opengl.PGraphicsOpenGL;

// http://pastebin.com/6wPgFYhq
public class PCardboard extends CardboardActivity implements PContainer {

  private DisplayMetrics metrics;
  private PApplet sketch;
  private GLCardboardSurfaceView view;
//  private CardboardView cardboardView;

  public PCardboard() {

  }

  public PCardboard(PApplet sketch) {
    System.err.println("-----> PCardboard CONSTRUCTOR: " + sketch);
    this.sketch = sketch;
  }

  public void initDimensions() {
    metrics = new DisplayMetrics();
    getResources().getDisplayMetrics();

  }

  public int getWidth() {
    return metrics.widthPixels;
  }

  public int getHeight() {
    return metrics.heightPixels;
  }

  public int getKind() {
    return CARDBOARD;
  }

  public void setSketch(PApplet sketch) {
    this.sketch = sketch;
  }

  public class GLCardboardSurfaceView extends CardboardView {
    PGraphicsOpenGL g3;
    SurfaceHolder surfaceHolder;

    public GLCardboardSurfaceView(Context context) {
      super(context);

      // Check if the system supports OpenGL ES 2.0.
      final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
      final boolean supportsGLES2 = configurationInfo.reqGlEsVersion >= 0x20000;

      if (!supportsGLES2) {
        throw new RuntimeException("OpenGL ES 2.0 is not supported by this device.");
      }

      surfaceHolder = getHolder();
      // are these two needed?
      surfaceHolder.addCallback(this);
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);

      // Tells the default EGLContextFactory and EGLConfigChooser to create an GLES2 context.
      setEGLContextClientVersion(2);
      setPreserveEGLContextOnPause(true);

      setFocusable(true);
      setFocusableInTouchMode(true);
      requestFocus();
    }

    public void initRenderer() {
      g3 = (PGraphicsOpenGL)(sketch.g);
      int quality = sketch.sketchQuality();
      if (1 < quality) {
        setEGLConfigChooser(((PGLES)g3.pgl).getConfigChooser(quality));
      }
      // The renderer can be set only once.
      setRenderer(((PGLES)g3.pgl).getRenderer());
//      setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

      // Cardboard needs to run with its own loop.
      setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
      super.surfaceChanged(holder, format, w, h);

      if (PApplet.DEBUG) {
        System.out.println("SketchSurfaceView3D.surfaceChanged() " + w + " " + h);
      }
      sketch.surfaceChanged();
//      width = w;
//      height = h;
//      g.setSize(w, h);

      // No need to call g.setSize(width, height) b/c super.surfaceChanged()
      // will trigger onSurfaceChanged in the renderer, which calls setSize().
      // -- apparently not true? (100110)
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      return sketch.surfaceTouchEvent(event);
    }


    @Override
    public boolean onKeyDown(int code, android.view.KeyEvent event) {
      sketch.surfaceKeyDown(code, event);
      return super.onKeyDown(code, event);
    }


    @Override
    public boolean onKeyUp(int code, android.view.KeyEvent event) {
      sketch.surfaceKeyUp(code, event);
      return super.onKeyUp(code, event);
    }
  }

  /*
   * Called with the activity is first created.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     if (sketch != null) {
       view = new GLCardboardSurfaceView(PCardboard.this);
       sketch.initSurface(PCardboard.this, view);
       view.initRenderer();

       // Don't start Papplet's animation thread bc cardboard will drive rendering
       // continuously
//       sketch.start();
     }
  }

   @Override
   public void startActivity(Intent intent) {
     // TODO Auto-generated method stub

   }

   @Override
   public void onResume() {
     super.onResume();
     view.onResume();
//     sketch.onResume();
   }


   @Override
   public void onPause() {
     super.onPause();
     view.onPause();
//     sketch.onPause();
   }


   @Override
   public void onDestroy() {
     sketch.onDestroy();
     super.onDestroy();
   }


   @Override
   public void onStart() {
     super.onStart();
     sketch.onStart();
   }


   @Override
   public void onStop() {
     sketch.onStop();
     super.onStop();
   }

}
