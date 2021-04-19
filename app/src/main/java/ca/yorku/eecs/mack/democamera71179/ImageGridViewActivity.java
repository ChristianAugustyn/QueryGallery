package ca.yorku.eecs.mack.democamera71179;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* This activity receives a bundle containing the name of a directory.  All the images
 * in the directory are retrieved and displayed in a grid view.
 *
 */
public class ImageGridViewActivity extends Activity implements AdapterView.OnItemClickListener {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    GridView gridView;
    TextView textView;
    ImageAdapter imageAdapter;
    File directory;
    File[] files;
    String[] filenames;
    TagDB db;
    String directoryString, testingFilePath;
    ArrayList<String> testScores;
    ArrayList<String> usedFiles;
    long time1, time2;
    boolean searchAttempted;

    int columnWidth, imageCount, amountOfImageFiles, errors;
    final static String TESTMODE_KEY = "testmode"; //tells if the program is in test mode
    final static String ALLOW_SEARCH_KEY = "allowsearch"; //allows the user to use the search function
    final static String DIRECTORY_KEY = "directory";
    final static String IMAGE_INDEX_KEY = "image_index";
    final static String IMAGE_FILENAMES_KEY = "image_filenames";
    boolean testMode, allowSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagegrid);

        // data passed from the setup activity in startActivity
        Bundle b = getIntent().getExtras();
        directoryString = b.getString("directory");
        testMode = b.getBoolean("testmode");
        allowSearch = b.getBoolean("allowsearch");
        testingFilePath = b.getString("file");
        imageCount = b.getInt("image_index");
        testScores = b.getStringArrayList("testscores");
        usedFiles = b.getStringArrayList("usedfiles");
        amountOfImageFiles = b.getInt("fileAmount");

        // get the directory containing some images
        directory = new File(directoryString);
        if (!directory.exists()) {
            Log.i(MYDEBUG, "No directory: " + directory.toString());
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }

        // Get a list of files in the directory, sorted by filename. See...
        files = directory.listFiles(new MyFilenameFilter(".jpg"));
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });


        getInitalFileNames();
        // make a String array of the filenames


        // get references to the GridView and TextView
        gridView = (GridView) findViewById(R.id.gridview);
        textView = (TextView) findViewById(R.id.textview);

        if(!testMode){
            allowSearch = true;
        }
        // display the name of the directory in the text view (minus the full path)
        String[] s = directory.toString().split(File.separator);
        textView.setText(s[s.length - 1]);

        // create an ImageAdapter and give it the array of filenames and the director
//        imageAdapter.setFilenames(filenames, directory);


        /*
         * Determine the display width and height. The column width is calculated so we have three
         * columns when the screen is in portrait mode. We'll keep the same column width in
         * landscape mode, but use as many columns as will fit. Including "-12" in the calculation
         * accommodates 3 pixels of space on the left and right and between each column.
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        columnWidth = dm.widthPixels < dm.heightPixels ? dm.widthPixels / 3 - 12
                : dm.heightPixels / 3 - 12;
        imageAdapter = new ImageAdapter(filenames, directoryString, columnWidth);
        gridView.setColumnWidth(columnWidth);

        // give the ImageAdapter to the GridView (and load the images)
        gridView.setAdapter(imageAdapter);

        // attach a click listener to the GridView (to respond to finger taps)
        gridView.setOnItemClickListener(this);

        db = QueryGallery.db;

        time1 = System.currentTimeMillis();
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
    }


    //Search Bar Functionality
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (allowSearch) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options_menu, menu);

            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

            //Listener for when the user changes the text in the text bar or submits their query
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String tags) {
                    search(tags);
                    return false;
                }

                //When there is no text in the search bar, the gridview is reverts back to showing all images
                @Override
                public boolean onQueryTextChange(String s) {
                    searchAttempted = true;
                    if (s.length() == 0) {
                        getInitalFileNames();
                        imageAdapter = new ImageAdapter(filenames, directoryString, columnWidth);
                        gridView.setAdapter(null);
                        gridView.setAdapter(imageAdapter);
                    }
                    return false;
                }
            });
        }
        return true;
    }

    //takes the image tag and gets all ImageBeans that match the tag
    //the ImageBean's id variable is a path to a particular image file
    //so the string needs to be parsed so there is just the image file name
    //filenames is now global so we add in only the files that the imageDAO brings back
    //a new instance of imageAdapter is made and is reassigned to the gridview.
    public void search(String tag) {

        Boolean tagsMatched = false;
        String[] tagArray = tag.split("\\s+");
        ImageDAO imageDAO = db.imageDAO();
        ArrayList<String> resizeableFilenamesArray = new ArrayList<String>();

        //iterate through each tag,
        for (int j = 0; j < tagArray.length; j++) {
            String currentTag = tagArray[j] + '_';
            List<ImageBean> imageBeans = imageDAO.getImagesByTag(currentTag);

            //if there are imageBeans then the tag matched an image and the gridview is changed
            if (imageBeans.size() > 0) {
                tagsMatched = true;
                //iterate through the image beans, get the path(id),split it, get the tags, check if tags match, extract the filename
                for (int i = 0; i < imageBeans.size(); i++) {
                    String tempImagePath = imageBeans.get(i).id;


                    String[] imagePathArray = tempImagePath.split("/");

                    String filename = imagePathArray[imagePathArray.length - 1];

                    //if the file already hasn't been added to the resizeableFilenamesArray add it and now we

                    if (!inFilenames(resizeableFilenamesArray, filename)) {
                        resizeableFilenamesArray.add(filename);
                    }

                }
            } else { // let the user know that a tag didnt match
                Toast.makeText(ImageGridViewActivity.this, "No Images Matching: " + tagArray[j], Toast.LENGTH_SHORT).show();
                errors++;
            }
        }

        //tags matched, time to change the gridview
        if (tagsMatched) {
            filenames = toArray(resizeableFilenamesArray);
            imageAdapter = new ImageAdapter(filenames, directoryString, columnWidth);
            gridView.setAdapter(null);
            gridView.setAdapter(imageAdapter);
        } else {
            gridView.setAdapter(null);
        }
    }

    //this is used so that all the images in the directory can be passed to the imageAdapter
    public void getInitalFileNames() {
        filenames = new String[files.length];
        for (int i = 0; i < files.length; ++i)
            filenames[i] = files[i].getName();
    }

    //checks to see if the filename is already in the filenames array
    public boolean inFilenames(ArrayList<String> filenames, String filename) {
        boolean inFilesnames = false;
        for (int i = 0; i < filenames.size(); i++) {
            if (filenames.get(i) == filename) {
                inFilesnames = true;
            }
        }
        return inFilesnames;
    }

    //converts the arraylist to an array
    public String[] toArray(ArrayList<String> list) {
        String[] theArray = new String[list.size()];
        for (int i = 0; i < theArray.length; i++) {
            theArray[i] = list.get(i);
        }

        return theArray;

    }


    /*
     * If the user taps on an image in the GridView, create an Intent to launch a new activity to
     * view the image in an ImageView. The image will respond to touch events (e.g., flings), so
     * we'll bundle up the filenames array and the directory and pass the bundle to the activity.
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        //Checks if the user is in test mode
        if (!testMode) {
            final Bundle b = new Bundle();
            b.putStringArray("imageFilenames", filenames);
            b.putString("directory", directory.toString());
            b.putInt("position", position);

            // start image viewer activity
            Intent i = new Intent(getApplicationContext(), ImageViewerActivity.class);
            i.putExtras(b);
            startActivityForResult(i, RESULT_OK);
        } else {
            //creates the path of the click
            if (validSelection()) {
                String path = directory + File.separator + filenames[position];
                //compares the path with the current pictures path
                if (testingFilePath.equalsIgnoreCase(path)) {
                    Toast.makeText(this, "They Match ", Toast.LENGTH_SHORT).show();
                    time2 = System.currentTimeMillis();

                    long score = TimeUnit.MILLISECONDS.toSeconds(time2 - time1);

                    imageCount++;

                    System.out.println("IMAGE COUNT: " + imageCount);
                    System.out.println("FILENAMESLENGTH" + filenames.length * 2);

                    String result;
                    if (allowSearch) {
                        int testnum = imageCount / 2;
                        if (testnum == 0) {
                            testnum = 1;
                        }
                        result = "Test: " + testnum + " with search - " + score + " seconds" + "\n" + "Errors: " + errors;
                    } else {
                        int testnum = imageCount / 2;
                        result = "Test: " + testnum + " without search - " + score + " seconds" + "\n" + "Errors: " + errors;
                    }
                    testScores.add(result);

                    //image count is used to ensure that there are not more tests than there are pictures

                    //filenames.length*2 to make a direct comparison of search manually and via text bar
                    if (imageCount <= amountOfImageFiles * 2) {
                        final Bundle b = new Bundle();
                        b.putStringArray(IMAGE_FILENAMES_KEY, filenames);
                        b.putString(DIRECTORY_KEY, directory.toString());
                        b.putBoolean(TESTMODE_KEY, testMode);
                        b.putBoolean(ALLOW_SEARCH_KEY, allowSearch);
                        b.putInt(IMAGE_INDEX_KEY, imageCount);
                        b.putStringArrayList("testscores", testScores);
                        b.putString("file", testingFilePath);
                        System.out.println("IMAGE GRID COUNT: " + imageCount);
                        b.putStringArrayList("usedfiles", usedFiles);
                        Intent i = new Intent(getApplicationContext(), FindThisImage.class);
                        i.putExtras(b);
                        startActivity(i);
                    } else {
                        //Get results

                        final Bundle b = new Bundle();
                        b.putStringArrayList("testscores", testScores);

                        Intent i = new Intent(getApplicationContext(), ResultsPage.class);
                        i.putExtras(b);
                        startActivity(i);
                    }


                } else {
                    Toast.makeText(this, "They Dont Match ", Toast.LENGTH_SHORT).show();
                    errors++;

                }
            } else {
                Toast.makeText(this, "You Must Use The Toolbar To Search", Toast.LENGTH_SHORT).show();
                errors++;
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (testMode) {

        } else {
            super.onBackPressed();
        }
    }

    public boolean validSelection() {
        if (allowSearch) {
            if (searchAttempted) {
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }
    }

    // A filter used with the list method (see above) to return only files with a specified
    // extension (e.g., ".jpg")
    class MyFilenameFilter implements FilenameFilter {
        String extension;

        MyFilenameFilter(String extensionArg) {
            this.extension = extensionArg;
        }

        @SuppressLint("DefaultLocale")
        public boolean accept(File f, String name) {
            // add toLowerCase to accept ".jpg" or ".JPG"
            return name.toLowerCase().endsWith(extension);
        }
    }
}
