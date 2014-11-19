package edu.collablab.games;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.unity3d.player.UnityPlayerNativeActivity;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;


public class UnityTPadIOIO extends UnityPlayerNativeActivity implements IOIOLooperProvider {
	public static Context ctx;
	
	public static String gameState;

	public int uid = 0;
	public String plan = "";
	
	static File logFile;
	static FileWriter fw;
	long starttime;
	final static private String APP_KEY = "oyg9oxn75h2w0te";
	final static private String APP_SECRET = "k59ufleep6rfvu3";
	final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	// You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;
	
	private DropboxAPI<AndroidAuthSession> mDBApi;
	
	private boolean mLoggedIn;
	
	private final IOIOAndroidApplicationHelper helper_ = new IOIOAndroidApplicationHelper(
			this, this);
	
	TPadIOIOHelper tpadhelper = new TPadIOIOHelper();
	
	private float px, py;
	private VelocityTracker vTracker;
//	private float bx, by;
	Bitmap texturebmp;
	
	int bmpIter;
	String textureNames[] = {"black", "narrowstripes"}; // {"fullscreen_horizontal_3", "fullscreen_horizontal_2"};
	//int bmpIds[] = new int[textureNames.length];
	Bitmap textures[] = new Bitmap[textureNames.length];
	
	int visIter;
	String visualNotificationNames[] = {"clear", "glow"};
	Bitmap backgrounds[] = new Bitmap[visualNotificationNames.length];
	
	public float[] hsv = new float[3];
	
	/*private Canvas myBackgroundCanvas = null;

	private Bitmap myBackgroundBitmap;
	private int width = 1280;
	private int height = 736;*/
	
	FrameLayout mFrameLayout;
	ImageView mImageView; // = new ImageView(this);
	ImageButton mButton;
	int buttonVisState;
	
	public boolean buttonPressed;

//	public final int redHue;
//	public final int blueHue;
//	public final int yellowHue;
//	public final int greenHue;
//	public final int cyanHue;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		helper_.create();
		
		logFile = createFile();
		
//		AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
		//AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
		AndroidAuthSession session = buildSession();
//        mApi = new DropboxAPI<AndroidAuthSession>(session);
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
//		mDBApi.getSession().startOAuth2Authentication(UnityTPadIOIO.this);
		checkAppKeySetup();
		if(!mLoggedIn) {
			mDBApi.getSession().startOAuth2Authentication(UnityTPadIOIO.this);
		}
		mLoggedIn = session.isLinked();
		
		// token: v8iXf5j2SaIAAAAAAABLK5PB6Y5Wqt-joe9tp0ZoM9b8rZ0SJ_RPm6u2O97_JTIZ
//		AndroidAuthSession session = new AndroidAuthSession(
//			     myAppKeys, myAccessType, new AccessTokenPair(storedAccessKey, storedAccessSecret));
		
		starttime = System.currentTimeMillis();
		gameState = "";
		
		for(int i = 0; i < textureNames.length; i++) {
			Log.i("UnityTPadIOIO", "Setting up " + textureNames[i]);
			
			int bmpId = getResources().getIdentifier(textureNames[i], "drawable", getPackageName());
			textures[i] = BitmapFactory.decodeResource(getResources(), bmpId);
		}
		for(int i = 0; i < visualNotificationNames.length; i++) {
			Log.i("UnityTPadIOIO", "Setting up " + visualNotificationNames[i]);
			
			int bmpId = getResources().getIdentifier(visualNotificationNames[i], "drawable", getPackageName());
			backgrounds[i] = BitmapFactory.decodeResource(getResources(), bmpId);
		}
		bmpIter = 0;
//		texturebmp = BitmapFactory.decodeResource(getResources(), bmpIds[bmpIter]);
		setTexture("black");
		
		/*myBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		myBackgroundCanvas = new Canvas(myBackgroundBitmap);*/
		
		int layoutID = getResources().getIdentifier("layout", "layout", getPackageName());
		FrameLayout layout = (FrameLayout) LayoutInflater.from(this).inflate(layoutID, null);
	    int imageViewId = getResources().getIdentifier("imageView", "id", getPackageName());
	    mImageView = (ImageView) layout.findViewById(imageViewId);
	    addContentView(layout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

	    int buttonId = getResources().getIdentifier("keyButton", "id", getPackageName());
	    mButton = (ImageButton) findViewById(buttonId);

		
		setBackgroundImage("clear");
		
		
		
		
		
		ctx = this;
		
	}
	
	public void changeTexture() {
		/*texturebmp.recycle();
		bmpIter = (bmpIter + 1) % bmpIds.length;
		texturebmp = BitmapFactory.decodeResource(getResources(), bmpIds[bmpIter]);
		Log.i("UnityTPadIOIO", "Texture changed: " + textureNames[bmpIter]);
		writeToLog("texturechange," + textureNames[bmpIter]);*/
		
		bmpIter = (bmpIter + 1) % textureNames.length;
		texturebmp = textures[bmpIter];
//		writeToLog("texturechange," + textureNames[bmpIter]);
	}
	
	public void setTexture(String textureName) {
		texturebmp = textures[Arrays.binarySearch(textureNames, textureName)];
//		writeToLog("texturechange," + textureName);
		
		//if(texturebmp != null) texturebmp.recycle();
		/*int bmpId;
		bmpId = getResources().getIdentifier(textureName, "drawable", getPackageName());
		Bitmap newbmp = BitmapFactory.decodeResource(getResources(), bmpId);
		if(newbmp != texturebmp) {
			texturebmp.recycle();
			texturebmp = newbmp;

			writeToLog("texturechange," + textureName);
		}
		newbmp.recycle();*/
//		texturebmp = BitmapFactory.decodeResource(getResources(), bmpId);
	}
	
	public void changeVisual() {
		/*visIter = (visIter + 1) % visualNotificationNames.length;
		setBackgroundImage(visualNotificationNames[visIter]);
		writeToLog("vischange," + visualNotificationNames[visIter]);*/
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
	}
	
	public void buttonPress(View view) {
		writeToLog("buttonpress");
		buttonPressed = true;
	}

	Runnable buttonVisRunner = new Runnable() {
		@Override
		public void run() {
			mButton.setVisibility(buttonVisState);
		}
	};

	public void hideButton() {
		buttonVisState = View.GONE;
		runOnUiThread(buttonVisRunner);
		// mButton.setVisibility(View.GONE);
	}

	public void showButton() {
		buttonVisState = View.VISIBLE;
		runOnUiThread(buttonVisRunner);
	}
	
	public void setButtonPressed(boolean b) {
		buttonPressed = b;
	}
	
	public boolean getButtonPressed() {
		return buttonPressed;
	}
	
	public void newLogFile() {
		// saveToDB();
		logFile = createFile();
	}
	
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
			
			writeToLog("TouchDown", px + "," + py);

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
			
			
			
			writeToLog("TouchMove", px + "," + py);

			touchTimer = System.nanoTime();

			break;

		case MotionEvent.ACTION_UP:

			isTouching = false;
			touchTimer = System.nanoTime();
			tpadhelper.sendTPad(0f);
			
			writeToLog("TouchUp", event.getX() + "," + event.getY());
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

	    AndroidAuthSession session = mDBApi.getSession();
	    if (session.authenticationSuccessful()) {
	        try {
	            // Required to complete auth, sets the access token on the session
	            session.finishAuthentication();
	            
	            storeAuth(session);
	            mLoggedIn = true;

	            //String accessToken = mDBApi.getSession().getOAuth2AccessToken();
	            //Log.i("Dropbox", accessToken);
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	        }
	    }
	}
	

	@Override
	protected void onDestroy() {
		MediaScannerConnection.scanFile(this, new String[] { logFile.getAbsolutePath() }, null, null);
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
		MediaScannerConnection.scanFile(this, new String[] { logFile.getAbsolutePath() }, null, null);
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
	
	
	
	
	
	
	
	private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            Log.i("Dropbox","You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            Log.i("Dropbox","URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }
	
	private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }
	
	/**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
        mLoggedIn = session.isLinked();
    }
    
    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.commit();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.commit();
            return;
        }
    }
    
    private String readFile(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int)file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while(scanner.hasNextLine()) {        
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }
	
	public int getPlan() {
		String str = "";
		File file = null;
		FileOutputStream outputStream = null;
		Log.i("Dropbox", "uid: " + uid);
		try {
			String file_path = Environment.getExternalStorageDirectory().getPath() + "/plans";
		    file = new File(file_path); // + uid + ".csv");
			if(!file.exists()) {
				file.mkdirs();
			}
			file = new File(file_path, uid + ".csv");
		    Boolean created = file.createNewFile();
		    // Log.i("Dropbox", created.toString());
		    outputStream = new FileOutputStream(file);
		    DropboxFileInfo info = mDBApi.getFile("/TPad Logs/plans/" + uid + ".csv", null, outputStream, null);
		} catch (Exception e) {
		    System.out.println("Something went wrong: " + e);
		    return 1;
		} finally {
		    if (outputStream != null) {
		    	// Log.i("Dropbox", "gets here");
		        try {
		            outputStream.close();
//		            FileReader fr = new FileReader(file);
//		            char[] buffer = {};
//		            fr.read(buffer);
//		            fr.close();
//		            // file.delete();
//		            Log.i("Dropbox", buffer.toString());
//		            str = new String(buffer);
		            
		            str = readFile(file.getPath());
		            file.delete();
		            Log.i("Dropbox", str);
//		    		return str;
		        } catch (Exception e) {
		        	System.out.println("Gah! " + e);
		        	return 1;
		        }
		    }
		}
		Log.i("Dropbox", "File len: " + str.length());
		plan = str;

		if(str.length() > 247) return 1;
		else return 0;

		
		
		
//		File file = new File("/plans/" + uid + ".csv");
//		FileOutputStream outputStream = new FileOutputStream(file);
//		DropboxFileInfo info = mDBApi.getFile("/plans/" + uid + ".csv", null, outputStream, null);
//		FileReader fr = new FileReader(file);
//		String str = fr.toString();
//		try {
//			fr.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Log.i("Dropbox", str);
//		return str;
	}
	
	public File createFile() {

		String file_path = Environment.getExternalStorageDirectory().getPath() + "/logFiles";

		File saveFile = new File(file_path);

		if (!saveFile.exists()) {
			saveFile.mkdirs();
		}

		 Date date = new Date();
		 SimpleDateFormat sdf = new SimpleDateFormat("yy.MM.dd. h:mm:ss:SSS a");
		 String formattedDate = sdf.format(date);

		int stringId = this.getApplicationInfo().labelRes;
		String appName = this.getString(stringId);

		saveFile = new File(file_path, formattedDate + " " + appName + ".txt");
	
		try {
			fw = new FileWriter(saveFile, true);
			// fw.write("File Start\r\n");
			fw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		
		return saveFile;
	}
	
	public void saveToDB() {
		FileInputStream inputStream;
		if(mLoggedIn) {
			try {
				inputStream = new FileInputStream(logFile);
				try {
						// logFile.getName()
						Entry response = mDBApi.putFile("/TPad Logs/logs/" + uid + ".csv", inputStream,
						                                logFile.length(), null, null);
					} catch (DropboxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	public void writeToLog(String msg) {
		
		try {
			Log.i("Logging", timestamp() + "," + msg + "," + gameState + ",,");
			fw = new FileWriter(logFile, true);
			fw.write(timestamp() + "," + msg + "," + gameState + ",," +"\r\n");
			fw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public void writeToLog(String msg, String xy) {
		
		try {
			Log.i("Logging", timestamp() + "," + msg + "," + gameState + "," + xy);
			fw = new FileWriter(logFile, true);
			fw.write(timestamp() + "," + msg + "," + gameState + "," + xy + "\r\n");
			fw.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	public String timestamp() {
		long now = System.currentTimeMillis();
		return Long.toString(now - starttime);
	}
	
	public void zeroTime() {
		starttime = System.currentTimeMillis();
	}
	
}

