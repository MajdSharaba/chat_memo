package com.yawar.memo.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.yawar.memo.BuildConfig;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatMessage;

import java.io.File;
import java.util.List;
import java.util.Locale;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;


public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public final List<ChatMessage> chatMessages;
    private Activity context;
    private ChatAdapter.CallbackInterface mCallback;





    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param groupSelectorRespone - the text to pass back
         */
        void onHandleSelection(int position, ChatMessage groupSelectorRespone, boolean myMessage);

        void downloadFile(int position, ChatMessage chatMessage, boolean myMessage);
         void downloadVoice(int position, ChatMessage chatMessage, boolean myMessage);
        void downloadVideo(int position, ChatMessage chatMessage, boolean myMessage);

        void onLongClick(int position, ChatMessage chatMessage, boolean isChecked);
        void playVideo(Uri path);



    }


    public ChatAdapter(Activity context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        try {
            mCallback = (ChatAdapter.CallbackInterface) context;
        } catch (ClassCastException ex) {
            //.. should log the error or throw and exception
        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message, parent, false);
        ChatAdapter.ViewHolder holder = new ChatAdapter.ViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        boolean myMsg = chatMessage.getIsme();//Just a dummy check
        //to simulate whether it me or other sender
//        System.out.println(chatMessage.getState()+"mmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        setAlignment(holder, myMsg, chatMessage.getState());
        switch (chatMessage.getType()){
            case "text":

                System.out.println("chatMessage.getType()" + chatMessage.getType() + chatMessage.getMessage());
                holder.txtMessage.setText(chatMessage.getMessage());
                holder.txtMessage.setVisibility(View.VISIBLE);
                holder.contentVideo.setVisibility(View.GONE);
                holder.imageMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
                holder.contentRecord.setVisibility(View.GONE);

                break;
            case "image":
                Glide.with(holder.imageMessage.getContext()).load(chatMessage.getImage()).centerCrop()
                   .into(holder.imageMessage);
                holder.imageMessage.setVisibility(View.VISIBLE);
                holder.contentVideo.setVisibility(View.GONE);
                holder.txtMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
                holder.contentRecord.setVisibility(View.GONE);

                break;
            case "video":
                System.out.println(chatMessages.get(position).getMessage()+"hhhhhhhhhhhhhhhhhhhhhhhhhh");
                holder.imageMessage.setVisibility(View.GONE);
                holder.txtMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
                holder.contentRecord.setVisibility(View.GONE);
                holder.contentVideo.setVisibility(View.VISIBLE);
                File videoFile;
                if (myMsg) {

                    File d = Environment.getExternalStoragePublicDirectory("memo/send/video");  // -> filename = maven.pdf
                    videoFile = new File(d, chatMessage.getFileName());

                } else {
                    File d = Environment.getExternalStoragePublicDirectory("memo/recive/video");  // -> filename = maven.pdf
                    videoFile = new File(d, chatMessage.getMessage().toString());

                }
                if (!videoFile.exists()) {

                    Glide.with(holder.imageVideo.getContext()).load(R.drawable.backgrounblack).centerCrop()
                            .into(holder.imageVideo);

                   holder.videoImageButton.setVisibility(View.GONE);
                    holder.videoImageDownload.setVisibility(View.VISIBLE);
                    holder.videoImageDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("clicked");
                            if (mCallback != null) {
                                mCallback.downloadVideo(position, chatMessages.get(position), myMsg);
                            }
                        }
                    });
                }
                else {

                    holder.videoImageButton.setVisibility(View.VISIBLE);
                    holder.videoImageDownload.setVisibility(View.GONE);
                    Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", videoFile);

                    Glide.with(holder.imageVideo.getContext()).load(path).centerCrop()
                            .into(holder.imageVideo);

                    holder.videoImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mCallback != null) {
                                mCallback.playVideo(path);
                            }

//                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//
//                            View mView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_video_player, null);
//                            VideoView videoView;
//                            videoView = mView.findViewById(R.id.simpleVideoView);
//                            MediaController mediaControls;
//                            mediaControls = new MediaController(view.getContext());
//                            videoView.setMediaController(mediaControls);
//                            mediaControls.setAnchorView(videoView);
//                            videoView.setVideoURI(path);
////                // start a video
//                            videoView.start();
//                            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    // Toast.makeText(getApplicationContext(), "Thank You...!!!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
//                                }
//                            });
//                            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                                @Override
//                                public boolean onError(MediaPlayer mp, int what, int extra) {
//                                  //  Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
//                                    return false;
//                                }
//                            });
//                            mBuilder.setView(mView);
//                            AlertDialog mDialog = mBuilder.create();
//                            mDialog.show();
                        }
                   });
                }


                break;

           ////voice begin
            case "voice":
                holder.txtMessage.setVisibility(View.GONE);
                holder.imageMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
                holder.contentVideo.setVisibility(View.GONE);

                holder.contentRecord.setVisibility(View.VISIBLE);

//              holder.voicePlayerView.setAudio("none");
                holder.mediaPlayer = new MediaPlayer();
                holder. playerSeekBar.setMax(100);

                File voiceFile;

                if (myMsg) {
                    File d = Environment.getExternalStoragePublicDirectory("memo/send/voiceRecord");  // -> filename = maven.pdf
                    voiceFile = new File(d, chatMessage.getFileName());

                } else {
                    File d = Environment.getExternalStoragePublicDirectory("memo/recive/voiceRecord");  // -> filename = maven.pdf
                    voiceFile = new File(d, chatMessage.getMessage().toString());

                }
                if (!voiceFile.exists()) {
                    holder.imagePlayerPause.setVisibility(View.GONE);
                    holder.downloadRecordIB.setVisibility(View.VISIBLE);
                    holder.playerSeekBar.setProgress(0);
                    holder.imagePlayerPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                    holder.textCurrentTime.setText("0.00");
                    holder.textTotalDouration.setText("0.00");

                    holder.downloadRecordIB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mCallback != null) {
                                System.out.println(chatMessages.get(position).getFileName());
                                mCallback.downloadVoice(position, chatMessages.get(position), myMsg);
                            }
                        }
                    });



                    System.out.println(chatMessage.getFileName()+"name");
                }
                else
                    {
                        holder.downloadRecordIB.setVisibility(View.VISIBLE);
                        if(!holder.mediaPlayer.isPlaying()){
                        holder.playerSeekBar.setProgress(0);
                        holder.textCurrentTime.setText("0.00");
                        holder.textTotalDouration.setText("0.00");}
                        else {
                            System.out.println("is playing"+chatMessage.getMessage());
                        }
                        holder.imagePlayerPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                        holder.imagePlayerPause.setVisibility(View.VISIBLE);
                        holder.downloadRecordIB.setVisibility(View.GONE);
                        try {
                            holder.mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                            holder.mediaPlayer.prepare();
                            holder.textTotalDouration.setText(milliSecondsToTimer((long) holder.mediaPlayer.getDuration()));
                        }
                        catch (Exception exceptione){
                            Toast.makeText(context,exceptione.getMessage(),Toast.LENGTH_SHORT).show();
                        }






                    }
                ////////////////media player tools
                 holder.playerSeekBar.setOnTouchListener(new View.OnTouchListener() {
              @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar) view;
                int payPosition = (holder.mediaPlayer.getDuration()/100)*seekBar.getProgress();
                holder.mediaPlayer.seekTo(payPosition);
                holder.textCurrentTime.setText(milliSecondsToTimer((long) holder.mediaPlayer.getCurrentPosition()));
                return false;
            }
        });
       holder. mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                holder.playerSeekBar.setSecondaryProgress(i);
            }
        });
      holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
          @Override
          public void onCompletion(MediaPlayer mediaPlayer) {
              holder.playerSeekBar.setProgress(0);
              holder.imagePlayerPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);
              holder.textCurrentTime.setText("0.00");
              mediaPlayer.reset();
              try {
                  holder.mediaPlayer.setDataSource(voiceFile.getAbsolutePath());
                  holder.mediaPlayer.prepare();
                  holder.textTotalDouration.setText(milliSecondsToTimer((long) holder.mediaPlayer.getDuration()));
              }
              catch (Exception exceptione){
                  Toast.makeText(context,exceptione.getMessage(),Toast.LENGTH_SHORT).show();
              }
              //prepeareMediaPlayer();
          }
      });

                  holder.updater= new Runnable() {
                    @Override
                    public void run() {
                        if(holder.mediaPlayer.isPlaying()){
                            holder.playerSeekBar.setProgress((int)(((float)holder.mediaPlayer.getCurrentPosition()/holder.mediaPlayer.getDuration()*100)));
                            holder.handler.postDelayed(holder.updater,1000);
                        }
                        long currentDuration = holder.mediaPlayer.getCurrentPosition();
                        holder.textCurrentTime.setText(milliSecondsToTimer(currentDuration));
                    }
                };
                holder.imagePlayerPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(holder.mediaPlayer.isPlaying()){
                            holder.handler.removeCallbacks(holder.updater);
                            holder.mediaPlayer.pause();
                            holder.imagePlayerPause.setImageResource(R.drawable.ic_play_arrow_white_24dp);

                        }
                        else {
                            holder.mediaPlayer.start();
                            holder.imagePlayerPause.setImageResource(R.drawable.ic_pause_white_24dp);
                            if(holder.mediaPlayer.isPlaying()){
                                holder.playerSeekBar.setProgress((int)(((float)holder.mediaPlayer.getCurrentPosition()/holder.mediaPlayer.getDuration()*100)));
                                holder.handler.postDelayed(holder.updater,1000);
                            }
                        }
                    }
                });

             break;
           //// voice end
            case "file":
        File pdfFile;
        if (myMsg) {
            File d = Environment.getExternalStoragePublicDirectory("memo/send");  // -> filename = maven.pdf
            pdfFile = new File(d, chatMessage.getFileName());

        } else {
            File d = Environment.getExternalStoragePublicDirectory("memo/recive");  // -> filename = maven.pdf
            pdfFile = new File(d, chatMessage.getMessage().toString());

        }

        System.out.println(pdfFile.exists() + "adaptee" + chatMessage.getFileName());
        System.out.println(chatMessage.getMessage());
        holder.txtFile.setText(chatMessage.getFileName());
        holder.txtMessage.setVisibility(View.GONE);
        holder.imageMessage.setVisibility(View.GONE);
                holder.contentVideo.setVisibility(View.GONE);

                holder.contentRecord.setVisibility(View.GONE);

                holder.contentFile.setVisibility(View.VISIBLE);
        holder.contentFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onHandleSelection(position, chatMessages.get(position), myMsg);
                }
            }
        });
        if (!pdfFile.exists()) {
            System.out.println("pdf not found");
            holder.pdfImage.setVisibility(View.GONE);
            holder.fileImageButton.setVisibility(View.VISIBLE);
            holder.fileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null) {
                        mCallback.downloadFile(position, chatMessages.get(position), myMsg);
                    }
                }
            });
        } else {
            holder.fileImageButton.setVisibility(View.GONE);
            Uri path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);
            Bitmap bmp = getImageFromPdf(path,context);
            System.out.println(bmp);
            holder.pdfImage.setImageBitmap(bmp);
            if(bmp != null){
                holder.pdfImage.setVisibility(View.VISIBLE);

            }
            else {
                System.out.println(bmp+"is nulllllll");

                holder.pdfImage.setVisibility(View.GONE);


            }
            break;





        }



            }

        holder.txtInfo.setText(chatMessage.getDate());
         if(chatMessages.get(position).isChecked){
             holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.background_onLong_click));

         }
         else{
             holder.itemView.setBackground(null);
         }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                System.out.println(chatMessages.get(position).getMessage()+"mjjjjjjjjjjj");

                if(!chatMessages.get(position).isChecked){
                    if (mCallback != null) {

                        mCallback.onLongClick(position, chatMessages.get(position), true);
                        System.out.println(chatMessages.get(position).getMessage()+"mjjjjjjjjjjjllklkl");

                    }


                    chatMessages.get(position).setChecked(true);
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.background_onLong_click));


                }
                else{
                    if (mCallback != null) {

                    mCallback.onLongClick(position, chatMessages.get(position), false);
                        System.out.println(chatMessages.get(position).getMessage()+"mjjjjjjjjjjj");

                    }
                    chatMessages.get(position).setChecked(false);
                    holder.itemView.setBackground(null);


                }
//                if (mCallback != null) {
//                    System.out.println(chatMessages.get(position).getFileName());
//
//                    mCallback.onLongClick(position, chatMessages.get(position), true);
//                }

                return false;
            }
        });


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

//    @Override
//    public void onViewRecycled(@NonNull ViewHolder holder) {
//        super.onViewRecycled(holder);
//      // holder.voicePlayerView.onPause();
////       holder.mediaPlayer.pause();
//
//    }



    public void add(ChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<ChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe, String state) {
        if (isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtMessage.setLayoutParams(layoutParams);

            holder.txtMessage.setTextColor(context.getResources().getColor(R.color.background_bottom_navigation));
            holder.txtDate.setTextColor(context.getResources().getColor(R.color.background_bottom_navigation));




            if (state.equals("3")) {
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done_green));
            } else if (state.equals("2")) {
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_recive_done));
            } else if (state.equals("1")) {
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_send_done));
            } else {
                holder.imageSeen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_send));

            }
            holder.imageSeen.setVisibility(View.VISIBLE);


            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.imageSeen.setVisibility(View.GONE);

            holder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams =
                    (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);
            holder.txtMessage.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.txtDate.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.imagePlayerPause.setColorFilter(context.getResources().getColor(R.color.blue));
            holder.downloadRecordIB.setColorFilter(context.getResources().getColor(R.color.blue));

            holder.textTotalDouration.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.textCurrentTime.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.timeSeparator.setTextColor(context.getResources().getColor(R.color.textColor));


            RelativeLayout.LayoutParams lp =
                    (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }

    public void filter(String charText) {
        for (ChatMessage wp : chatMessages) {
            if (wp.getMessage() != null) {
                System.out.println(wp.getMessage().toString());
                if (wp.getMessage().toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    int index = wp.getMessage().toString().toLowerCase(Locale.getDefault()).indexOf(charText);

                    Spannable WordtoSpan = new SpannableString(wp.getMessage().toString());
                    WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), index, index + charText.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //wp.setMessage(WordtoSpan);
                }

                notifyDataSetChanged();
            }
        }
    }
    String milliSecondsToTimer(Long milliSeconds) {
        String timerString = "";
        String secondString;
        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minute = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) (milliSeconds % (1000 * 60 * 60) % (1000 * 60) / 1000);

        if (hours > 0) {
            timerString = hours + ":";

        }
        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;

        }
        timerString = timerString + minute + ":" + secondString;
        return timerString;

    }

//    private ViewHolder createViewHolder(View v) {
//
//        ViewHolder holder = new ViewHolder();
//        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
//        holder.imageMessage= v.findViewById(R.id.imgMessage);
//        holder.content = (LinearLayout) v.findViewById(R.id.content);
//        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
//        holder.contentFile = (LinearLayout) v.findViewById(R.id.liner_file);
//        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
//        holder.txtDate = v.findViewById(R.id.tv_date);
//        holder.imageSeen = v.findViewById(R.id.iv_state);
//        holder.txtFile = v.findViewById(R.id.text_file);
//        holder.imageFile = v.findViewById(R.id.image_file);
//        holder.fileImageButton = v.findViewById(R.id.image_button_file);
//        return holder;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMessage;
        public ImageView imageMessage;
        private ImageView imageSeen;
        public TextView txtInfo;
        public TextView txtDate;
        public TextView txtFile;
        public ImageView imageFile;
        ImageButton fileImageButton;
        ImageButton downloadRecordIB;
        ImageView pdfImage;

        public LinearLayout content;
        public LinearLayout contentFile;
        public LinearLayout contentWithBG;
        public LinearLayout contentRecord;
        public LinearLayout contentDownload;
//        public  LinearLayout contentVideo;
        public ImageView imagePlayerPause;
        TextView textCurrentTime,textTotalDouration,timeSeparator;
        SeekBar playerSeekBar;
        MediaPlayer mediaPlayer;
        RelativeLayout relativeLayout;
        Handler handler = new Handler();
        Runnable updater;
//        VideoView simpleVideoView;
//        MediaController mediaControls;
        FrameLayout contentVideo;
        ImageButton videoImageButton;
        ImageButton videoImageDownload;
        ImageView imageVideo;




//        ImageButton forwardbtn, backwardbtn, pausebtn, playbtn;
//        MediaPlayer mPlayer;
//        TextView songName, startTime, songTime;
//        SeekBar songPrgs;
//         double startTim = 0;
//        double finalTime = 0;
//
//        int oTime =0;
//        int sTime =  0 ;
//        int eTime =0;
//        int fTime = 5000;
//        int bTime = 5000;
//        Handler hdlr = new Handler();
//        MediaPlayer mediaPlayer;

        VoicePlayerView voicePlayerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             relativeLayout = itemView.findViewById(R.id.item_relative);
            txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            imageMessage = itemView.findViewById(R.id.imgMessage);
            content = (LinearLayout) itemView.findViewById(R.id.content);
            contentWithBG = (LinearLayout) itemView.findViewById(R.id.contentWithBackground);
            contentFile = (LinearLayout) itemView.findViewById(R.id.liner_file);
            txtInfo = (TextView) itemView.findViewById(R.id.txtInfo);
            txtDate = itemView.findViewById(R.id.tv_date);
            imageSeen = itemView.findViewById(R.id.iv_state);
            txtFile = itemView.findViewById(R.id.text_file);
            imageFile = itemView.findViewById(R.id.image_file);
            fileImageButton = itemView.findViewById(R.id.image_button_file);
            pdfImage = itemView.findViewById(R.id.image_pdf);
            contentRecord = itemView.findViewById(R.id.Liner_record);
            imagePlayerPause = itemView.findViewById(R.id.image_play_pause);
            textCurrentTime = itemView.findViewById(R.id.text_current_time);
            textTotalDouration = itemView.findViewById(R.id.text_total_duration);
            timeSeparator = itemView.findViewById(R.id.time_separator);
            playerSeekBar = itemView.findViewById(R.id.player_seek_bar);
//            contentDownload = itemView.findViewById(R.id.Liner_download);
           downloadRecordIB =  itemView.findViewById(R.id.image_download_audio);
//            simpleVideoView = (VideoView) itemView.findViewById(R.id.simpleVideoView);
            videoImageButton = itemView.findViewById(R.id.video_image_button);
            contentVideo = itemView.findViewById(R.id.frame_video);
            imageVideo = itemView.findViewById(R.id.img_video);
            videoImageDownload = itemView.findViewById(R.id.video_image_download);




//            voicePlayerView = itemView.findViewById(R.id.voicePlayerView);


//            playbtn = itemView.findViewById(R.id.btnPlay);
//            pausebtn = itemView.findViewById(R.id.btnPause);
//            startTime = itemView.findViewById(R.id.txtStartTime);
//            songTime = itemView.findViewById(R.id.txtSongTime);
//            songPrgs = itemView.findViewById(R.id.sBar);

        }
    }
    public Bitmap getImageFromPdf(Uri pdfUri, Context context) {
        int pageNumber = 0;

        PdfiumCore pdfiumCore = new PdfiumCore(context);

        try {
            ParcelFileDescriptor fd = context.getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = (int) (pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber)*1.8);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);


            pdfiumCore.closeDocument(pdfDocument); // important!
            return bmp;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }


}


