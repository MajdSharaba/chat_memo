package com.yawar.memo.constant;

import com.google.firebase.auth.PhoneAuthProvider;

public interface AllConstants {
    String VERIFICATION_CODE = "code";
    String IMAGE_PATH = "Media/Profile_Image/profile";
    int STORAGE_REQUEST_CODE = 4000;
    int USERNAME_CODE = 100;
    int CONTACTS_REQUEST_CODE = 2000;
    int RECORDING_REQUEST_CODE = 3000;
    int Read_Write_Storage_CODE = 5000;
    public String base_url = "http://192.168.1.10:8080/yawar_chat/";
    public String upload_Voice_URL = "http://192.168.1.12:3000/uploadAudio";
    public String upload_file_URL = "http://192.168.1.12:3000/uploadFile";
    public String upload_video_URL = "http://192.168.1.12:3000/uploadVedio";
    public String upload = "http://192.168.1.10:3000/createStore";

    public String download_url = "http://192.168.1.12:8080/src/yawar_chat/uploads/";


    public String load_chat_message =  "http://192.168.1.12:8000/messagesbyusers";
   public  String socket_url = "http://192.168.1.12:3000";
   public  String delete_message ="http://192.168.1.12:8000/deletmessage";
   public    String add_to_archived_url = "http://192.168.1.12:8000/archivechat";


    String CHANNEL_ID = "1000";

}
