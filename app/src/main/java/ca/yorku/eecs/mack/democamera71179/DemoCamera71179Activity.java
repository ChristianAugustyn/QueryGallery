package ca.yorku.eecs.mack.democamera71179;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.room.Room;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;



public class DemoCamera71179Activity extends Activity implements OnClickListener, OnTouchListener
{
    public static final int MEDIA_TYPE_IMAGE = 1;

    public static final String WORKING_DIRECTORY = "CameraStuff";

    final static String IMAGE_INDEX_KEY = "image_index";
    final static String DIRECTORY_KEY = "directory";

    final static String IMAGE_FILENAMES_KEY = "image_filenames";
    private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private static final int IMAGE_CAMERA_MODE = 100;
    private static final int VIDEO_CAMERA_MODE = 200;
    private static final int IMAGE_VIEWER_MODE = 300;
    private static final int VIDEO_VIEWER_MODE = 400;
    Uri fileUri;
    static TagDB db;
    ImageButton imageCameraButton, videoCameraButton, imageCameraButton2, imageGelleryButton;
    ImageButton imagePrevButton, imageNextButton, videoPrevButton, videoNextButton;
    ImageView imageView;

    File mediaStorageDirectory;
    String[] imageFilenames;
    int imageIdx;
    TextView statusTextView;
    TextView imageCountView;

    // create a file Uri for saving an image or video
    private static Uri getOutputMediaFileUri(File directory, int type)
    {
        return Uri.fromFile(getOutputMediaFile(directory, type));
    }

    // create a File for saving an image or video
    private static File getOutputMediaFile(File directory, int type)
    {
        // create a media file name, encoded with the current date and time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(System.currentTimeMillis());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else
        {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }

    private void initialize()
    {
        // hide the action bar (gives more display space on small screens)
        if (getActionBar() != null)
            getActionBar().hide();
        //setup db
        db = Room.databaseBuilder(getApplicationContext(),
                TagDB.class, "tagDB").allowMainThreadQueries().build();

        // get references to UI widgets
//        imageCameraButton = (ImageButton) findViewById(R.id.button1);
        imageCameraButton2 = (ImageButton) findViewById(R.id.cam);
        imageGelleryButton=(ImageButton)findViewById(R.id.gallery);

        imageView = (ImageView)findViewById(R.id.imageView1);

        imagePrevButton = (ImageButton)findViewById(R.id.button1a);
        imageNextButton = (ImageButton)findViewById(R.id.button1b);
        imageCountView = (TextView)findViewById(R.id.imageCount);



        // attach listeners to UI widgets
        imageView.setOnTouchListener(this);
//        imageCameraButton.setOnClickListener(this);
        imageCameraButton2.setOnClickListener(this);
        imageGelleryButton.setOnClickListener(this);

        imagePrevButton.setOnClickListener(this);
        imageNextButton.setOnClickListener(this);

        // make a working directory (if necessary) to store the images and videos
        mediaStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                WORKING_DIRECTORY);
        if (!mediaStorageDirectory.exists() && !mediaStorageDirectory.mkdirs())
        {
            Log.i(MYDEBUG, "Failed to create directory: " + WORKING_DIRECTORY);
            this.finish(); // terminate
        }
        Log.i(MYDEBUG, "Media directory: " + mediaStorageDirectory.toString());

        // fill arrays for image/video filenames currently in the working directory
        imageFilenames = mediaStorageDirectory.list(new MyFilenameFilter(".jpg"));


		/*
         * Sort the arrays into chronological order. Note: This only works because the date and time
		 * of creation are embedded in the filenames.
		 */
        Arrays.sort(imageFilenames);


        Log.i(MYDEBUG, "Number of image files: " + imageFilenames.length);


        // index of last (most recent) image (or -1 if none)
        imageIdx = imageFilenames == null ? -1 : imageFilenames.length - 1;



        if (imageIdx >= 0) // there is at least one image in the directory (show it!)
            displayImage();

    }

    // touch callback for picture and video views
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        // we're only interested in ACTION_UP events
        if (me.getAction() != MotionEvent.ACTION_UP)
            return true;

        if (v == imageView && imageFilenames.length > 0)
        {
            final Bundle b = new Bundle();
            b.putStringArray(IMAGE_FILENAMES_KEY, imageFilenames);
            b.putString(DIRECTORY_KEY, mediaStorageDirectory.toString());

            // start image viewer activity
            Intent i = new Intent(getApplicationContext(), ImageGridViewActivity.class);
            i.putExtras(b);
            startActivityForResult(i, IMAGE_VIEWER_MODE);

        }
        System.out.println("method: on touch");
        return true;
    }

    // callback for the UI buttons used to cycle through images/videos or to launch the camera intent
    @Override
    public void onClick(View v)
    {
        if (v == imageCameraButton2) // launch camera intent (image mode)
        {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // specify a file URI where the image will be saved
            fileUri = getOutputMediaFileUri(mediaStorageDirectory, MEDIA_TYPE_IMAGE);

            // use putExtra to give the file URI to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the Intent (when the intent finishes, onActivityResult will execute)
            // Note: the 2nd argument is the Request Code (will be returned to onActivityResult)
            startActivityForResult(intent, IMAGE_CAMERA_MODE);

        }
        else if (v == imagePrevButton && imageFilenames.length != 0)
            previousImage();

        else if (v == imageNextButton && imageFilenames.length != 0)
            nextImage();
        else if (v == imageGelleryButton){
//            displayImage();
            System.out.println("method: start of gallery button");
            if ( imageFilenames.length > 0)
            {
                final Bundle b = new Bundle();
                b.putStringArray(IMAGE_FILENAMES_KEY, imageFilenames);
                b.putString(DIRECTORY_KEY, mediaStorageDirectory.toString());

                // start image viewer activity
                Intent i = new Intent(getApplicationContext(), ImageGridViewActivity.class);
                i.putExtras(b);
                startActivityForResult(i, IMAGE_VIEWER_MODE);
                System.out.println("method: end if in gallery button");
            }
            System.out.println("method: end of gallery button");
        }


        System.out.println("method: on click");

    }

    private void previousImage()
    {
        --imageIdx;
        if (imageIdx < 0)
            imageIdx = imageFilenames.length - 1;
        displayImage();
    }

    private void nextImage()
    {
        ++imageIdx;
        if (imageIdx >= imageFilenames.length)
            imageIdx = 0;
        displayImage();
    }

    private void displayImage()
    {
        if (imageFilenames != null && imageFilenames.length > 0)
        {
            String path = mediaStorageDirectory.toString() + File.separator + imageFilenames[imageIdx];
            Bitmap bmp = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bmp);
            imageCountView.setText(String.format(Locale.CANADA, "%d of %d", (imageIdx + 1), imageFilenames.length));

        } else
            imageCountView.setText(String.format("%s", "(no pictures)"));

        System.out.println("method: on display image");
    }







    /*
     * The onActivityResult method is called upon exiting from either the camera application, the
     * image viewer, or the video viewer. The image viewer and the video viewer are the full-screen
     * activities defined herein to improve the viewing of images or videos. A series of if/else
     * statements are used to select the correct code to execute, which will depend on the
     * requestCode originally used to launch the activity and the resultCode returned from the
     * activity. Data can be returned from the activity via the Intent object passed to
     * onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAMERA_MODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                /*
                 * The following line of commented-out code is from the Android API Guide for
				 * "Camera". However, this line of code causes the app to crash with a null pointer
				 * exception. The problem is that the Intent passed to onActivityResult is null.
				 * After some digging, I came across the following explanation in StackOverflow:
				 *
				 * "The default Android camera application returns a non-null intent only when
				 * passing back a thumbnail in the returned Intent. If you pass EXTRA_OUTPUT with a
				 * URL to write to, it will return a null intent and the picture is in the URL that
				 * you passed in."  (Note: The author meant "URI", not "URL".) See...
				 *
				 * http://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
				 */
                // Toast.makeText(this, "Image saved to:\n" + data.getData(),
                // Toast.LENGTH_LONG).show();

                // the fix...
                Toast.makeText(this, "Image saved to:\n" + fileUri.toString(), Toast.LENGTH_LONG).show();

                // update file list and index
                imageFilenames = mediaStorageDirectory.list(new MyFilenameFilter(".jpg"));
                Arrays.sort(imageFilenames);

                imageIdx = imageFilenames.length - 1;
                displayImage();

            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                // user cancelled the image capture
                Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_LONG).show();

            } else
            {
                // image capture failed, advise user
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_LONG).show();
            }
        }
		// we're returning via the Back button in the Navigation Bar. Therefore, the return code is CANCEL
        else if (requestCode == IMAGE_VIEWER_MODE)
        {
            if (resultCode == Activity.RESULT_OK)
                displayImage();
            else
                Log.i(MYDEBUG, "UNKNOWN RESULT CODE (IMAGE)!");

        }
    }

    /*
     * We are handling the configuration change ourself, which is to say, the system will not shut
     * down and restart the activity when the configuration changes (such as occurs if the screen is
     * rotated).
     *
     * If the screen is rotated, the UI will still change (e.g., from portrait to landscape). This
     * occurs through the call to the super method. The new orientation is stored in the newConfig
     * object passed as an argument. The layout will remain the same, however (because onCreate does
     * not execute and no new resource is loaded).
     *
     * Note that this method is only called if the manifest contains
     * android:configChanges="orientation" as an attribute for the Activity element. See...
     *
     * http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange
     *
     * Note: Screen orientation changes for this demo occur much faster using this technique
     * compared to the usual approach of implementing onSaveInstanceState and
     * onRestoreInstanceState. This technique is faster because the activity is *not* being shut
     * down and restarted. Just look at the code executed from onCreate and you'll see why.
     *
     * One final note: We are implementing onConfigurationChanged simply to demonstrate this
     * alternative way to handle a configuration change. We aren't actually doing anything except
     * printing a message to the LogCat window. If this method is deleted or commented-out, the
     * app's behaviour is the same. For an example where there is actually some work to do in
     * onConfigurationChanged, see Demo_WebView.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.i(MYDEBUG, "onConfigurationChanged! newConfig=" + newConfig);
        super.onConfigurationChanged(newConfig);
    }

    // A filter used with listFiles (see above) to return only files with a specified extension
    // (e.g., ".jpg" or ".mp4")
    class MyFilenameFilter implements FilenameFilter
    {
        String extension;

        MyFilenameFilter(String extensionArg)
        {
            this.extension = extensionArg;
        }

        public boolean accept(File f, String name)
        {
            return name.endsWith(extension);
        }
    }
}