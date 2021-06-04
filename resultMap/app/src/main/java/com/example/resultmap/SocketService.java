package com.example.resultmap;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class SocketService extends Service {
    private Message msg;
    private Context context;

    private SocketThread socketThread;

    private boolean stopThread=true;

    public SocketService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(this).registerReceiver(sBroadcastReceiver,
                new IntentFilter("socketClose"));
        Log.d("Boot1", "SocketService.onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Boot1", "SocketService.onStartCommand()");
        if (intent == null) return Service.START_STICKY;
        else Log.d("Boot1", "SocketService.onStartCommand().else");

        if ("startForground".equals(intent.getAction())) {
            Log.d("Boot2", "start getAction");
            startForgroundService();
        }

        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Boot1", "SocketService.onDestory()");
    }

    public class SocketThread extends Thread {
        @Override
        public void run() {


            try {
                Log.d("Boot2", "Socket");
                Socket socket = new Socket("10.0.2.2", 7777);
                Log.d("Boot2", "Socket start");

                BufferedReader readerIn = new BufferedReader(new InputStreamReader(System.in, "utf-8"));

                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));

                // 제일 먼저 서버로 대화명을 송신합니다.
                // 맨 먼저 간 값이 닉네임이 되기 때문.

                JSONObject jsonName = new JSONObject();
                jsonName.put("nickname", "sexyboy");
                JSONObject jsonName2 = new JSONObject();
                jsonName2.put("User", jsonName);


                writer.println(jsonName2);
                writer.flush();


                Log.d("Boot2", "BufferReader");
                BufferedReader readerOut = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

                while (stopThread) {


                    String str = readerOut.readLine();


                    JSONParser jsonParser = new JSONParser();
                    Object obj = jsonParser.parse(str);
                    JSONObject jsonStr = (JSONObject) obj;



                    msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = jsonStr;
                    handler.sendMessage(msg);


                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void startForgroundService() {
        context=this;
        Log.d("Boot2", "startForegroundService starting point");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("채팅 방 활성화...");
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);


        Intent notificationIntent = new Intent(this, com.example.resultmap.Chatting.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "socket", NotificationManager.IMPORTANCE_HIGH));
        }




        startForeground(1, builder.build());

        socketThread = new SocketThread();
        socketThread.start();


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String content, userName, profileURL;
            if (msg.what == 1) {
                JSONObject jsonStr = (JSONObject) msg.obj;
//              여기서 제이슨으로 파싱해서 쪼갬 *************************************************
                try {

                    content = (String) jsonStr.get("content");
                    JSONObject jsonUser = (JSONObject) jsonStr.get("User");
                    userName = (String) jsonUser.get("nickname");
                    profileURL = (String) jsonUser.get("image");
                    if(profileURL == null)
                        profileURL="https://tazoapp.site/placeholder-profile.png";
                    if(content!=null && content.contains("https://storage.googleapis.com/tazo-bucket/uploads"))
                        content = "[사진]";

                    Log.d("Boot2", userName);
                    if (content != null)
                        Log.d("Boot2", content);

                    if(! checkScreen().equals("Chatting"))
                    {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
                        Glide.with(getApplicationContext()).asBitmap().load(profileURL).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                builder.setLargeIcon(resource);

                            }


                        });
                        builder.setContentTitle(userName);
                        builder.setContentText(content);
                        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
                        System.out.println("여기서 우선순위");
//                    builder.setPriority(Notification.PRIORITY_MAX);
                        builder.setPriority(NotificationCompat.PRIORITY_MAX);
                        builder.setDefaults(Notification.DEFAULT_VIBRATE);

                        Intent notificationIntent = new Intent(context, com.example.resultmap.Chatting.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                        builder.setContentIntent(pendingIntent);



                        startForeground(1, builder.build());
                    } else {
                        System.out.println("여긴 채팅 방 안이야");
                        Intent intent = new Intent("chattingReceiver");
                        String str=jsonStr.toString();
                        intent.putExtra("jsonStr", str);


                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }









                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        }


    };

    public String checkScreen()
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        ComponentName componentName = info.get(0).topActivity;
        String ActivityName = componentName.getShortClassName().substring(1);
        return ActivityName;
    }

    private BroadcastReceiver sBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("**************************");
            System.out.println("종료할꺼야");
            stopThread=false;

        }
    };

}
