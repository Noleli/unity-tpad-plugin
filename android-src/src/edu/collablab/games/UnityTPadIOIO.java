package edu.collablab.games;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Scanner;

//import net.noleli.texturechange.R;
import nxr.tpadioio.lib.TPadTexture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.unity3d.player.UnityPlayerNativeActivity;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;


public class UnityTPadIOIO extends UnityPlayerNativeActivity implements IOIOLooperProvider {
	public static Context ctx;
	
	private final IOIOAndroidApplicationHelper helper_ = new IOIOAndroidApplicationHelper(
			this, this);
	
	TPadIOIOHelper tpadhelper = new TPadIOIOHelper();
	
	private float px, py;
	private VelocityTracker vTracker;
//	private float bx, by;
	Bitmap texturebmp;
	
//	int bmpIter;
//	String textureNames[] = {"black", "narrowstripes"}; // {"fullscreen_horizontal_3", "fullscreen_horizontal_2"};
	//int bmpIds[] = new int[textureNames.length];
//	Bitmap textures[] = new Bitmap[textureNames.length];
	
	public float[] hsv = new float[3];
	
	/*private Canvas myBackgroundCanvas = null;

	private Bitmap myBackgroundBitmap;
	private int width = 1280;
	private int height = 736;*/
	
	FrameLayout mFrameLayout;

//	public final int redHue;
//	public final int blueHue;
//	public final int yellowHue;
//	public final int greenHue;
//	public final int cyanHue;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		helper_.create();
		
		
		/*for(int i = 0; i < textureNames.length; i++) {
			Log.i("UnityTPadIOIO", "Setting up " + textureNames[i]);
			
			int bmpId = getResources().getIdentifier(textureNames[i], "drawable", getPackageName());
			textures[i] = BitmapFactory.decodeResource(getResources(), bmpId);
		}
		bmpIter = 0;*/
//		texturebmp = BitmapFactory.decodeResource(getResources(), bmpIds[bmpIter]);
		
		
		/*myBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		myBackgroundCanvas = new Canvas(myBackgroundBitmap);*/
		
		// not sure this is necessary, but.
		int layoutID = getResources().getIdentifier("layout", "layout", getPackageName());
		FrameLayout layout = (FrameLayout) LayoutInflater.from(this).inflate(layoutID, null);
	    addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

	    
		
		
		
		
		
		
		
		ctx = this;
		
	}
	
	/*public void changeTexture() {
		/*texturebmp.recycle();
		bmpIter = (bmpIter + 1) % bmpIds.length;
		texturebmp = BitmapFactory.decodeResource(getResources(), bmpIds[bmpIter]);
		Log.i("UnityTPadIOIO", "Texture changed: " + textureNames[bmpIter]);
		writeToLog("texturechange," + textureNames[bmpIter]);* /
		
		bmpIter = (bmpIter + 1) % textureNames.length;
		texturebmp = textures[bmpIter];
//		writeToLog("texturechange," + textureNames[bmpIter]);
	}*/
	
	public void setTexture(String textureName) {
//		texturebmp = textures[Arrays.binarySearch(textureNames, textureName)];
		Log.i("Logging", "texturechange," + textureName);
//		writeToLog("texturechange," + textureName);
		
//		if(texturebmp != null) texturebmp.recycle();
		int bmpId;
		bmpId = getResources().getIdentifier(textureName, "drawable", getPackageName());
		Log.i("Bitmap", String.valueOf(bmpId));
//		Bitmap newbmp = BitmapFactory.decodeResource(getResources(), bmpId);
		texturebmp = BitmapFactory.decodeResource(getResources(), bmpId);
		/*if(newbmp != texturebmp) {
//			texturebmp.recycle();
			texturebmp = newbmp;

//			writeToLog("texturechange," + textureName);
		}
		newbmp.recycle();*/
//		texturebmp = BitmapFactory.decodeResource(getResources(), bmpId);
	}
	
	/*public void changeVisual() {
//		visIter = (visIter + 1) % visualNotificationNames.length;
//		setBackgroundImage(visualNotificationNames[visIter]);
//		writeToLog("vischange," + visualNotificationNames[visIter]);
		visIter = (visIter + 1) % visualNotificationNames.length;
		runOnUiThread(changeVisualRunner);
	}
	
	Runnable changeVisualRunner = new Runnable() {
		@Override
		public void run() {
			
//			setBackgroundImage(visualNotificationNames[visIter]);
			mImageView.setImageBitmap(backgrounds[visIter]);
//			writeToLog("vischange," + visualNotificationNames[visIter]);
		}
	};
	
	public void setBackgroundImage(String imageName) {
		//int bmpId = getResources().getIdentifier(imageName, "drawable", getPackageName());
//		Drawable d = getResources().getDrawable(bmpId);
//		texturebmp = ;
		visIter = Arrays.binarySearch(visualNotificationNames, imageName);
		runOnUiThread(changeVisualRunner);
//		mImageView.setImageBitmap(backgrounds[Arrays.binarySearch(visualNotificationNames, imageName)]); // Resource(bmpId);
	}*/
	
	// Handling touch events
	//@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.i("UnityTPadIOIO", "motion event");
		
		float px_old, py_old;
		
		
		double vy, vx;
		
		boolean isTouching;
		long touchTimer;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			px = event.getX();
			py = event.getY();

			vx = 0;
			vy = 0;

			// Start a new velocity tracker
			if (vTracker == null) {
				vTracker = VelocityTracker.obtain();
			} else {
				vTracker.clear();
			}
			vTracker.addMovement(event);

			//bitmapTasks();

			// Call the timeout timer
			touchTimer = System.nanoTime();
			
			Log.i("Logging", "TouchDown," + px + "," + py);

			// Set touching to true
			isTouching = true;

			break;

		case MotionEvent.ACTION_MOVE:
			// Update old positions

			// Update cursor positions
			px_old = px;
			py_old = py;

			px = event.getX();
			py = event.getY();

			vTracker.addMovement(event);

			// Compute velocity in pixels per 1 ms
			vTracker.computeCurrentVelocity(1);

			// get current velocities
			vx = vTracker.getXVelocity();
			vy = vTracker.getYVelocity();

			// Log.i("Velocities: ", String.valueOf(vx) + " " + String.valueOf(vy));

			//bitmapTasks();
			
			pixelToTexture(texturebmp.getPixel((int) px, (int) py));

//			if (!isDrawing) {
//
//				pixelToTexture(myDrawBitmap.getPixel((int) px, (int) py));
//
//			}
//
//			if (isDrawing) {
//
//				synchronized (myDrawCanvas) {
//
//					if (eraserOn) {
//						myDrawCanvas.drawLine(px_old, py_old, px, py, eraser);
//					} else
//						myDrawCanvas.drawLine(px_old, py_old, px, py, brush);
//				}
//			}
			
			
			
			Log.i("Logging","TouchMove," + px + "," + py);

			touchTimer = System.nanoTime();

			break;

		case MotionEvent.ACTION_UP:

			isTouching = false;
			touchTimer = System.nanoTime();
			tpadhelper.sendTPad(0f);
			
			Log.i("Logging","TouchUp," + event.getX() + "," + event.getY());
			break;

		case MotionEvent.ACTION_CANCEL:
			vTracker.recycle();
			break;
		}

		return false;
	}
	
	public void pixelToTexture(int pix) {

		float freq = 0;
		float amp = 0;

		TPadTexture waveType = TPadTexture.SINUSOID;

		Color.colorToHSV(pix, hsv);

		amp = hsv[2];

		/*if ((Color.green(pix)==Color.blue(pix))&& (Color.blue(pix)==Color.red(pix))&& Color.red(pix)==Color.green(pix)){
			// this is a grayscale image, skip
			
		}
		else if
		(hsv[0] == redHue) {
			freq = 100f;
			waveType = TPadTexture.SQUARE;
		} else if (hsv[0] == yellowHue) {
			freq = 70f;
			waveType = TPadTexture.SINUSOID;
		} else if (hsv[0] == greenHue) {
			freq = 30f;
			waveType = TPadTexture.SAWTOOTH;
		} else if (hsv[0] == blueHue) {
			freq = 20f;
			waveType = TPadTexture.SINUSOID;
		} else if (hsv[0] == cyanHue) {

		}

		if (freq > 0) { // we have a texture, sendthe texture command
			tpadhelper.sendTPadTexture(waveType, freq, amp);

		} else { // send normal tpad amp command
			tpadhelper.sendTPad(amp);

		}*/

		// these are all going to be grayscale
		tpadhelper.sendTPad(amp);
	}
	
	/*public void setBackgroundBitmap(String bgName) {
		int bmpId = getResources().getIdentifier(bgName, "drawable", getPackageName());
		myBackgroundBitmap = BitmapFactory.decodeResource(getResources(), bmpId);
		
		myBackgroundCanvas.drawBitmap(myBackgroundBitmap, 0, 0, new Paint());
	}*/
	
	
	
	protected void onResume() {
	    super.onResume();

	}
	

	@Override
	protected void onDestroy() {
//		MediaScannerConnection.scanFile(this, new String[] { logFile.getAbsolutePath() }, null, null);
		//saveToDB();

		helper_.destroy();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		helper_.start();
	}

	@Override
	protected void onStop() {
//		MediaScannerConnection.scanFile(this, new String[] { logFile.getAbsolutePath() }, null, null);
		//saveToDB();
		
		helper_.stop();
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0) {
			helper_.restart();
		}
	}

	/**
	 * Subclasses must either implement this method or its other overload by
	 * returning an implementation of {@link IOIOLooper}. A dedicated thread
	 * will be created for each available IOIO, from which the
	 * {@link IOIOLooper}'s methods will be invoked. <code>null</code> may be
	 * returned if the client is not interested to create a thread for this
	 * IOIO. In multi-IOIO scenarios, where you want to identify which IOIO the
	 * thread is for, consider overriding
	 * {@link #createIOIOLooper(String, Object)} instead.
	 * 
	 * @return An implementation of {@link IOIOLooper}, or <code>null</code> to
	 *         skip.
	 */
	protected IOIOLooper createIOIOLooper() {
		return tpadhelper.createIOIOLooper();
	}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return createIOIOLooper();
	}
	
	
	
	
	
	
	
	
    
    
	
}

