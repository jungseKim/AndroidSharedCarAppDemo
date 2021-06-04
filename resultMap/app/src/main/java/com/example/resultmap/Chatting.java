package com.example.resultmap;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chatting extends AppCompatActivity {
    //  이미지 전송 버튼 **************************************
    private ImageButton imageButton;
    private String img_path;
//  *****************************************************

    //  예전 채팅 기록 ****************************************
    private String record ="";
    //  *****************************************************
//  옆에 네비게이션 바 ************************************
    private DrawerLayout drawerLayout;
    private View drawerView;
    //  *****************************************************
//  채팅을 위한 소켓 **************************************
    private TextView textView_test;
    private Button socketClose;
    private int socketCloseNumber=0;
    private int socketCloseNumber2=0;

    private ArrayList<ChattingData> arrayList = new ArrayList<>();
    private com.example.resultmap.ChattingAdapter chattingAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayout;


    //  *****************************************************
//  메세지 전송 시 ****************************************
    private Message msg;
    static int testNum=0;
    JSONObject jsonObject;
//  *****************************************************


    private Button send_button_test;
    private EditText message_edit_test;
    String sumStr="";
    String name="sexyboy";
    String nameCheck="";
    String imgURL="";
    int inOut=-1;

//    ReceiverThread thread1;

    ChattingData chatData;

    private Context context=this;
    private Intent socketServiceIntent;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);



//        Intent socketServiceIntent = new Intent(this, SocketService.class);
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
//        {
//            this.startForegroundService(socketServiceIntent);
//            Log.d("Boot2","BootReceier.onReceive.if");
//        }
//        else
//        {
//            this.startService(socketServiceIntent);
//            Log.d("Boot2","MyAutoRunApp.onReceive.else");
//        }

//        *********************************************************************
//      백그라운드 서비스 등록
            String className = SocketService.class.getName();
            if(!isServiceRunning(className))
            {
                socketServiceIntent = new Intent(this,SocketService.class);
                socketServiceIntent.setAction("startForground");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(socketServiceIntent);
                    Log.d("Boot2","startForegroundService start");
                } else {
                    startService(socketServiceIntent);
                }
            }




//      *************************************************************************

//       ************************************************************************
//        서비스에서 데이터 받아오는 준비
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("chattingReceiver"));





//      AsyncTask 활용해서 소켓 실행 ***************************************************
        HttpAsyncTask httpAsyncTask = new HttpAsyncTask(record);
        try {
            System.out.println("error");
            record =httpAsyncTask.execute("https://tazoapp.site/rooms/1/chat").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(record);
//      *******************************************************************************


//      예전 채팅 기록 *****************************************************************
        setRecord(record);
//      *******************************************************************************


//  키보드가 나올 시 레이아웃 변경 **********************************************************
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//  **************************************************************************************

//      네비게이션 바 ********************************************************************
        drawerLayout= (DrawerLayout) findViewById(R.id.ChattingActivity);
        drawerView = (View) findViewById(R.id.drawer);

        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        textView_test = (TextView) findViewById(R.id.textView_test);
        textView_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


//        ********************************************************************

//      ********************************************************************************

//      상단 바 없애기 *******************************************************************
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
//      ********************************************************************************


        send_button_test = findViewById(R.id.send_button_test);
        message_edit_test = (EditText) findViewById(R.id.message_edit_test);




        recyclerView = (RecyclerView) findViewById(R.id.chatting_test);
        linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);



        chattingAdapter = new com.example.resultmap.ChattingAdapter(arrayList);
        recyclerView.setAdapter(chattingAdapter);

        com.example.resultmap.RecyclerDecoration spaceDecoration = new com.example.resultmap.RecyclerDecoration(30);
        recyclerView.addItemDecoration(spaceDecoration);

//      메세지 전송 ******************************************************************

        send_button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sumStr=message_edit_test.getText().toString();
                String chattingMessage=message_edit_test.getText().toString();
                message_edit_test.setText("");

                jsonObject=new JSONObject();
                jsonObject.put("content",sumStr);



                testNum = 1;
                Log.d("sendChattingMessage","123");
                SenderThread2 senderThread2 = new SenderThread2(chattingMessage);
                senderThread2.start();



            }
        });
//  *********************************************************************************



//  뒤로 나가기 **************************************************************
        socketClose = (Button) findViewById(R.id.socketClose);
        socketClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("socketClose");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                stopService(socketServiceIntent);
                onBackPressed();
            }
        });
//   **********************************************************************



//  이미지전송 버튼 **********************************************************
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

//  ************************************************************************

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
//            System.out.println("일단 handler는 실행 됨");
            String content,userName, profileURL;
            int imgCheckReceiver;
            if(msg.what==1)
            {
                JSONObject jsonStr = (JSONObject) msg.obj;
//              여기서 제이슨으로 파싱해서 쪼갬 *************************************************
                try {

                    content = (String) jsonStr.get("content");
                    JSONObject jsonUser = (JSONObject) jsonStr.get("User");
                    userName = (String) jsonUser.get("nickname");
                    profileURL = (String) jsonUser.get("image");

                    if(profileURL == null)
                        profileURL="https://tazoapp.site/placeholder-profile.png";
                    if(content.contains("https://storage.googleapis.com/tazo-bucket/uploads"))
                        imgCheckReceiver=1;
                    else
                        imgCheckReceiver=0;
                    chatData = new ChattingData(profileURL, userName, content, name, inOut,imgCheckReceiver);
                    arrayList.add(chatData);
                    chattingAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(arrayList.size()-1);
//                    System.out.println("name : " + userName + ", profile : " + profileURL);

                } catch (Exception e)
                {
                    e.printStackTrace();
                }





            }

        }
    };






    //    -----------------------------------------------------------
    // 내비 바의 상태
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    private static class HttpAsyncTask extends AsyncTask<String, Void, String>{

        OkHttpClient client = new OkHttpClient();
        String result;

        public HttpAsyncTask(String result)
        {
            this.result=result;
        }

        @Override
        protected String doInBackground(String... params) {

            String strUrl = params[0];

            try {
                Request request = new Request.Builder().url(strUrl).build();
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }



    }

    public void setRecord(String recordMessage)
    {
//        int imgCheck=0;

        try {
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(recordMessage);
            JSONArray jsonArray = (JSONArray) obj;

            for(int i=0; i<jsonArray.size(); i++)
            {

                msg = handler.obtainMessage();
                msg.what=1;
                msg.obj=jsonArray.get(i);
                handler.sendMessage(msg);


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    //  이미지 경로 확인 *******************************************************************
    @Nullable
    public static String getPath(@NonNull Context context, @NonNull Uri uri)
    {
        final ContentResolver contentResolver = context.getContentResolver();

        if(contentResolver == null)
        {
            return null;
        }

        String filePath = context.getApplicationInfo().dataDir + File.separator + System.currentTimeMillis();

        File file = new File(filePath);

        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if(inputStream == null)
                return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len = inputStream.read(buf)) > 0)
            {
                outputStream.write(buf,0,len);
            }

            outputStream.close();
            inputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return file.getAbsolutePath();

    }
//  ***********************************************************************************

    //  뒤로가기 / 이미지 선택 후 ************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = null;
                if(data != null)
                {
                    uri = data.getData();
                }
                if(uri != null)
                {

//                    imageView.setImageURI(uri);   // 올리자마자 채팅방으로 가는게 아니라, 서버에 갔다가 옴
                    img_path = getPath(this,uri);

//                    img_path = Environment.getExternalStorageDirectory().getAbsolutePath() + img_path;
                    System.out.println("-------------------------------------------------");
                    System.out.println("image path : " + img_path);
                    goSend(img_path);
                }
            }

        }
    }
    //  *************************************************************************************
//  사진 보내기 **************************************************************************
    private void goSend(String path)
    {
        System.out.println("보냈음 : "+path);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image","image.jpg", RequestBody.create(MultipartBody.FORM, new File(path)))
                .build();

        Request request = new Request.Builder()
                .url("https://tazoapp.site/rooms/1/test/image")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                System.out.println("연결 실패");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                System.out.println("연결 성공");
                System.out.println(response);
            }
        });
    }


    public class SenderThread2 extends Thread
    {
        private String chattingMessage;

        public SenderThread2 (String chattingMessage)
        {
            this.chattingMessage = chattingMessage;
        }

        @Override
        public void run()
        {
            Log.d("senderThread","senderThread start");

            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("content",chattingMessage).build();
            Request request = new Request.Builder()
                    .url("https://tazoapp.site/rooms/1/test/chat")
                    .post(formBody)
                    .build();

//                Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    System.out.println("연결 실패");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    System.out.println("연결 성공");
                    System.out.println(response);
                }
            });




        }

    }

//   *********************************************************************************
//    로컬 브로드 캐스트 (서비스랑 액티비티 사이를 연결
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String str = intent.getStringExtra("jsonStr");
                JSONParser jsonParser=new JSONParser();
                Object obj = jsonParser.parse(str);
                JSONObject jsonStr=(JSONObject) obj;

                msg = handler.obtainMessage();
                msg.what=1;
                msg.obj=jsonStr;
                handler.sendMessage(msg);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

//   ****************************************************************************************
//    서비스 실행 여부 확인
    public Boolean isServiceRunning(String class_name)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(class_name.equals(service.service.getClassName()))
             return true;
        }

        return false;
    }

}

