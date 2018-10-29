package a.aman.com.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String INTENT_NAME_VIDEO_PATH = "INTENT_NAME_VIDEO_PATH";
    private VideoView mVvPlayback;
    String path="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    File mVideo;
    FloatingActionButton playButton;
    private  int checked=0;
    ConnectivityManager connectivityManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initViewa();
        initObject();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionAndGo();
        }


        //--------- Here we are checking if video is already downloaded or not.-----------------
        mVideo =new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+ File.separator +"OfflineVideos"), "bigbunny.mp4");
        if(mVideo.exists()){
            //File Exists
           // Toast.makeText(this, "Offline  "+mVideo.getAbsolutePath(), Toast.LENGTH_SHORT).show();

            //---------------- If downloaded it will play with downloded file path.-----------------------
            playVideo(mVideo.getAbsolutePath());

        } else {

            // ------------------If not downloaded it will play with server path and  downlod the video.--------------------
            playVideo(path);
            //Toast.makeText(this, "Online", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This method is to  initialize views
     */
     public void initViewa(){
         playButton= findViewById(R.id.fab_add);
         mVvPlayback =findViewById(R.id.vv_playback);
         connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
     }

    /**
     * This method is to  initialize objects
     */
    private void initObject(){
        playButton.setOnClickListener(this);
    }

    /**
     * This method is to  initialize video plaer
     */
    private  void  playVideo(String url){
        mVvPlayback.setVideoPath(url);
        mVvPlayback.setKeepScreenOn(true);
        //  mVvPlayback.setZOrderOnTop(true);
        mVvPlayback.setMediaController(new MediaController(this));
     //   mVvPlayback.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_add:
                    if(mVideo.exists()){
                        checkButtonStatus();
                    }else{
                        //-------- Check if connected to internet or not.-----------
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()|| connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()) {
                            checkButtonStatus();
                            DownloadData(); 
                        }else {
                            Toast.makeText(this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                        }
                       
                    }
                break;

                  }
    }

    /**
     * This method is to  give pause and resume functionality to video;
     */
    private void checkButtonStatus(){
        if (checked==0){
            checked = 1;

            mVvPlayback.start();
            playButton.setImageResource(R.drawable.ic_baseline_pause_24px);
        }else {
            checked = 0;
            playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24px);
            mVvPlayback.pause();
        }
    }


    /**
     * This method is to download video
     */
    private void DownloadData () {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));
        request.setDescription("Downloading bigbunny.mp4 for offline purpose.");
        request.setTitle("bigbunny.mp4");
// -----------in order for this if to run, you must use the android 3.2 to compile your app
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS+ File.separator +"OfflineVideos", "bigbunny.mp4");

//----------------- get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * This method will  enable storage permissions  if not granted
     */
    private void checkPermissionAndGo() {
        if (permissionsEnabled()) {
            //  btnNext.setEnabled(false);

        } else {
            enablePermission();
        }
    }

    /**
     * This method is to  check storage permissions  required
     */
    private boolean permissionsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED  )
                return false;
        }
        return true;
    }

    /**
     * This method is to  enable storage permissions  required
     */
    private void enablePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(permissions, 0);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        playButton.setImageResource(R.drawable.ic_baseline_play_arrow_24px);
        mVvPlayback.pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == R.id.action_gallery) {
          openFolder();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method is to  check your downloaded video
     */
    public void openFolder()
    {   if (mVideo.exists()) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(Uri.parse(mVideo.getAbsolutePath()), "video/mp4");
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }
    }
}
