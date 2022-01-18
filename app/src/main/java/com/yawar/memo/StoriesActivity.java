package com.yawar.memo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.yawar.memo.adapter.ViewPagerAdapter;
import com.yawar.memo.fragment.StoriesFragment;
import com.yawar.memo.model.UserStatus;
import com.yawar.memo.utils.BaseApp;
import com.yawar.memo.views.BottomSheetDialog;
import com.yawar.memo.views.VideoActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import jp.shts.android.storiesprogressview.StoriesProgressView;


public class StoriesActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener, Observer {

    // on below line we are creating a int array
    // in which we are storing all our image ids.
//    private final int[] resources = new int[]{
//            R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background,
//            R.drawable.ic_launcher_background,
//
//    };
    private  final ArrayList<String> resources = new ArrayList<String>();
    BaseApp myBase;
    int id;
    TextView textView;


    // on below line we are creating variable for
    // our press time and time limit to display a story.
    long pressTime = 0L;
    long limit = 300L;

    // on below line we are creating variables for
    // our progress bar view and image view .
    private StoriesProgressView storiesProgressView;
    //    private TextView textView;
    VideoView videoView;
    ImageView imageView;

    // on below line we are creating a counter
    // for keeping count of our stories.
    private int counter = 0;
    ArrayList<UserStatus> userStatusList = new ArrayList<>();
    UserStatus myStatus;
    UserStatus statuses ;




    // on below line we are creating a new method for adding touch listener
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // inside on touch method we are
            // getting action on below line.
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // on action down when we press our screen
                    // the story will pause for specific time.
                    pressTime = System.currentTimeMillis();

                    // on below line we are pausing our indicator.
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:

                    // in action up case when user do not touches
                    // screen this method will skip to next image.
                    long now = System.currentTimeMillis();

                    // on below line we are resuming our progress bar for status.
                    storiesProgressView.resume();

                    // on below line we are returning if the limit < now - presstime
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inside in create method below line is use to make a full screen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stories);
        myBase = (BaseApp) getApplication();
        myBase.getObserver().addObserver(this);
        userStatusList = myBase.getStoriesObserve().getUserStatusList();
        System.out.println(userStatusList.size());
        Bundle bundle = getIntent().getExtras();
        textView = findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });
        id = bundle.getInt("id",1);

        if(id>=0){
            statuses = userStatusList.get(id);
            System.out.println(id+"id>0");
        }
        else {
            statuses = myBase.getStoriesObserve().getMyStatus();
            System.out.println(id+"id<0");

        }
        for(int i=0;i<statuses.getStatusList().size();i++){
            resources.add(statuses.getStatusList().get(i).getUrl());
        }


//        resources.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
//        resources.add("http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
//        resources.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");



        // on below line we are initializing our variables.
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);

        // on below line we are setting the total count for our stories.
        storiesProgressView.setStoriesCount(resources.size());

        // on below line we are setting story duration for each story.
        storiesProgressView.setStoryDuration(10000L);

        // on below line we are calling a method for set
        // on story listener and passing context to it.
        storiesProgressView.setStoriesListener(this);

        // below line is use to start stories progress bar.
        storiesProgressView.startStories(counter);
        imageView = findViewById(R.id.image);
        videoView = findViewById(R.id.video);



        // initializing our image view.
//        textView =  findViewById(R.id.text);
        if(statuses.getStatusList().get(counter).getType().equals("video")){
            videoView.setVisibility(View.VISIBLE);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        }
                    });
                }
            });
        videoView.setVideoPath(resources.get(counter));


        videoView.start();}
        else {
            imageView.setVisibility(View.VISIBLE);
        Glide.with(imageView.getContext()).load(resources.get(counter)).centerCrop()
                .into(imageView);}

        // on below line we are setting image to our image view.
//        image.setImageResource(resources[counter]);


        // below is the view for going to the previous story.
        // initializing our previous view.
        View reverse = findViewById(R.id.reverse);

        // adding on click listener for our reverse view.
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inside on click we are
                // reversing our progress view.
                storiesProgressView.reverse();
            }
        });

        // on below line we are calling a set on touch
        // listener method to move towards previous image.
        reverse.setOnTouchListener(onTouchListener);

        // on below line we are initializing
        // view to skip a specific story.
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inside on click we are
                // skipping the story progress view.
                storiesProgressView.skip();
            }
        });
        // on below line we are calling a set on touch
        // listener method to move to next story.
        skip.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onBackPressed() {
        System.out.println(" onBackPressed()");
        if(id>=0){
            if (counter>=statuses.getStatusList().size()-1){
                myBase.getStoriesObserve().setAllSeen(id);
                System.out.println(counter+";;;"+statuses.getStatusList().size());
            }}
        this.finish();
//        finish();
        super.onBackPressed();
    }

    @Override
    public void onNext() {
        // this method is called when we move
        // to next progress view of story.
//        textView.setText(resources.get(++counter));
//        videoView.setVideoPath(resources.get(++counter));
        ++counter;
        if(statuses.getStatusList().get(counter).getType().equals("video")){
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoPath(resources.get(counter));

            videoView.start();}
        else {
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(imageView.getContext()).load(resources.get(counter)).centerCrop()
                    .into(imageView);}
//        Glide.with(imageView.getContext()).load(resources.get(
//                ++counter)).centerCrop()
//                .into(imageView);
        if(id>0){
            myBase.getStoriesObserve().setUserStatusSeen(id,counter);}
//      System.out.println(  userStatusList.get(id).getStatusList().get(counter).isSeen());
//         videoView.start();
    }

    @Override
    public void onPrev() {

        // this method id called when we move to previous story.
        // on below line we are decreasing our counter
        if ((counter - 1) < 0) return;

        // on below line we are setting image to image view
//        textView.setText(resources.get(--counter));
//        videoView.setVideoPath(resources.get(--counter));
//        videoView.start();
        --counter;
        if(statuses.getStatusList().get(counter).getType().equals("video")){
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoPath(resources.get(counter));

            videoView.start();}
        else {
            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(imageView.getContext()).load(resources.get(counter)).centerCrop()
                    .into(imageView);}
//        Glide.with(imageView.getContext()).load(resources.get(--counter)).centerCrop()
//                .into(imageView);
    }

    @Override
    public void onComplete() {
        // when the stories are completed this method is called.
        // in this method we are moving back to initial main activity.
        if (id>=0){
            myBase.getStoriesObserve().setAllSeen(id);}
//        myBase.getObserver().setUserStatusList(userStatusList);
        this.finish();

//        finish();
    }

    @Override
    protected void onDestroy() {
        // in on destroy method we are destroying
        // our stories progress view.
//        if (counter>=statuses.getStatusList().size()-1){
//            myBase.getObserver().setAllSeen(id);
//            System.out.println(counter+";;;"+statuses.getStatusList().size());
//
//
//
//
//        }
        storiesProgressView.destroy();
//        Intent i = new Intent(StoriesActivity.this, MainActivity2.class);
//        startActivity(i);
//        finish();
        System.out.println("destroyyyyyyyyyyyyyyyy");
        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}