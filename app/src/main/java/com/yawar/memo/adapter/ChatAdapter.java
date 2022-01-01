package com.yawar.memo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.yawar.memo.BuildConfig;
import com.yawar.memo.R;
import com.yawar.memo.model.ChatMessage;
import com.yawar.memo.model.GroupSelectorRespone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

import static org.webrtc.ContextUtils.getApplicationContext;

public class ChatAdapter  extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public final List<ChatMessage> chatMessages;
    private Activity context;
    private ChatAdapter.CallbackInterface mCallback;
//    MediaPlayer mediaPlayer;
//    Handler handler = new Handler();



    public interface CallbackInterface {

        /**
         * Callback invoked when clicked
         *
         * @param position             - the position
         * @param groupSelectorRespone - the text to pass back
         */
        void onHandleSelection(int position, ChatMessage groupSelectorRespone, boolean myMessage);

        void onDownload(int position, ChatMessage groupSelectorRespone, boolean myMessage);
       void Download(int position, ChatMessage groupSelectorRespone, boolean myMessage);

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

//    @Override
//    public int getCount() {
//        if (chatMessages != null) {
//            return chatMessages.size();
//        } else {
//            return 0;
//        }
//    }

//    @Override
//    public ChatMessage getItem(int position) {
//        if (chatMessages != null) {
//            return chatMessages.get(position);
//        } else {
//            return null;
//        }
//    }

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
                holder.imageMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
                holder.contentRecord.setVisibility(View.GONE);

                break;
            case "image":
                Glide.with(holder.imageMessage.getContext()).load(chatMessage.getImage()).into(holder.imageMessage);
                holder.imageMessage.setVisibility(View.VISIBLE);
                holder.txtMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
                holder.contentRecord.setVisibility(View.GONE);

                break;
            case "voice":
                holder.txtMessage.setVisibility(View.GONE);
                holder.imageMessage.setVisibility(View.GONE);
                holder.contentFile.setVisibility(View.GONE);
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
//                                     holder.downloadRecordIB.setVisibility(View.VISIBLE);


//                    holder.voicePlayerView.setVisibility(View.GONE);
//                    holder.downloadRecordIB.setVisibility(View.VISIBLE);

//                    holder.set

                    holder.downloadRecordIB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mCallback != null) {
                                System.out.println(chatMessages.get(position).getFileName());
                                mCallback.Download(position, chatMessages.get(position), myMsg);
                            }
                        }
                    });
//                holder.voicePlayerView.setVisibility(View.GONE);
//                    holder.pausebtn.setVisibility(View.GONE);
//                    holder.voicePlayerView.setVisibility(View.GONE);
//                    holder.contentDownload.setVisibility(View.VISIBLE);
//                    holder.voicePlayerView.setShowTiming(false);
//                    holder.voicePlayerView.setClickable(false);


                    System.out.println(chatMessage.getFileName()+"name");
                }
                else
                    {
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


//                        holder.contentDownload.setVisibility(View.GONE);
//                        holder.voicePlayerView.setVisibility(View.VISIBLE);
//
//
//
//
//                        holder.voicePlayerView.refreshPlayer(voiceFile.getAbsolutePath());




                    }
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
            holder.fileImageButton.setVisibility(View.VISIBLE);
            holder.pdfImage.setVisibility(View.INVISIBLE);
            holder.pdfImage.setVisibility(View.GONE);


            holder.fileImageButton.setVisibility(View.VISIBLE);
            holder.fileImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null) {
                        mCallback.onDownload(position, chatMessages.get(position), myMsg);
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

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
      // holder.voicePlayerView.onPause();
//       holder.mediaPlayer.pause();

    }



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
                    wp.setMessage(WordtoSpan);
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
        public ImageView imagePlayerPause;
        TextView textCurrentTime,textTotalDouration,timeSeparator;
        SeekBar playerSeekBar;
        MediaPlayer mediaPlayer;
        Handler handler = new Handler();
        Runnable updater;




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


