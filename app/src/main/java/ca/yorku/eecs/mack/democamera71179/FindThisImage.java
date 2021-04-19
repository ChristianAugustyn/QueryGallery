package ca.yorku.eecs.mack.democamera71179;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;



public class FindThisImage extends Activity implements View.OnClickListener {

    ImageView imageToFind;
    String directoryString, fileString;
    File[] files;
    File file;
    File directory;
    Button next;
    boolean testMode, allowSearch;
    String[] imageFilenames;
    TextView header;
    Bundle b;
    int imageCount;
    private static final int IMAGE_VIEWER_MODE = 300;
    final static String IMAGE_FILENAMES_KEY = "image_filenames";
    final static String TESTMODE_KEY = "testmode";
    final static String ALLOW_SEARCH_KEY = "allowsearch";
    final static String FILE_KEY ="file";
    final static String DIRECTORY_KEY = "directory";
    final static String IMAGE_INDEX_KEY = "image_index";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findthisimage);


        // data passed from the setup activity in startActivity
        b = getIntent().getExtras();
        directoryString = b.getString("directory");
        testMode = b.getBoolean("testmode");
        allowSearch = b.getBoolean("allowsearch");
        imageCount = b.getInt("image_index");
        // get the directory containing some images

        System.out.println("DIRECTROY STRING " + directoryString);
        directory = new File(directoryString);

        if (!directory.exists())
        {
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }

        // Get a list of files in the directory, sorted by filename. See...
        files = directory.listFiles(new FindThisImage.MyFilenameFilter(".jpg"));
        Arrays.sort(files, new Comparator<File>()
        {
            public int compare(File f1, File f2)
            {
                return f1.getName().compareTo(f2.getName());
            }
        });

        imageToFind = (ImageView)findViewById(R.id.imageToFind);
        next = (Button)findViewById(R.id.next);
        header = (TextView)findViewById(R.id.header);


        next.setOnClickListener(this);

        //getting a random image from the files
        Random random = new Random();
        int randomNum = random.nextInt(((files.length-1)-0)+1) + 0;
        file = files[randomNum];
        fileString = file.toString();
        Uri uri = Uri.parse(file.toString());
        imageToFind.setImageURI(uri);

        if(imageCount % 2 == 0){
            allowSearch = false;
        }else{
            allowSearch = true;
        }

        System.out.println("FindThisImage COUNT: " + imageCount);
        if(allowSearch){
            header.setText("Search For This Image Using The Search Bar");
        }else{
            header.setText("Search For This Image Without Using The Search Bar");
        }


    }

    //does not allow user to press back
    @Override
    public void onBackPressed () {

    }

    @Override
    public void onClick(View v){

        if(v == next){
            b.putStringArray(IMAGE_FILENAMES_KEY, imageFilenames);
            b.putString(DIRECTORY_KEY, directoryString);
            b.putString(FILE_KEY, fileString);
            b.putBoolean(TESTMODE_KEY,testMode);
            b.putBoolean(ALLOW_SEARCH_KEY, allowSearch);
            b.putInt(IMAGE_INDEX_KEY, imageCount);

            // start image viewer activity
            Intent i = new Intent(getApplicationContext(), ImageGridViewActivity.class);
            i.putExtras(b);
            startActivityForResult(i, IMAGE_VIEWER_MODE);

        }
    }


    class MyFilenameFilter implements FilenameFilter
    {
        String extension;

        MyFilenameFilter(String extensionArg)
        {
            this.extension = extensionArg;
        }

        @SuppressLint("DefaultLocale")
        public boolean accept(File f, String name)
        {
            // add toLowerCase to accept ".jpg" or ".JPG"
            return name.toLowerCase().endsWith(extension);
        }
    }
}
