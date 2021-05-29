package com.example.resultmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class Profile extends AppCompatActivity {

    RequestQueue requestQueue;
    boolean name_check=true;
    private Toast toast;
    TextView id_view,email_view,gender_view,shadow;
    EditText nickname_view;
    ImageView photo;
    Button photoChang_btn,nameChang_btn;
    int id;
    String email,nickname,imageURL,gender;
    JSONObject myJson;
    Message msg;

    DrawerLayout drawerLayout2;

    View nickname_change_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sub_profile);

        nickname_view=findViewById(R.id.nickname_view);
        photo=findViewById(R.id.photo);

        gender_view=findViewById(R.id.gender_view);

        email_view=findViewById(R.id.email_view);


        photoChang_btn=findViewById(R.id.photoChang_btn);
        photoChang_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        nameChang_btn=findViewById(R.id.nameChang_btn);
        nameChang_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name_check){
                    nickname_view.setEnabled(true);
                    nameChang_btn.setText("완료");
                    nickname_view.requestFocus();
                    name_check=false;
                }else{
                    String str=nickname_view.getText().toString();
                    new myPostThred(str).start();
                    nickname_view.setEnabled(false);
                    nameChang_btn.setText("바꾸기");
                    name_check=true;
                    new myGetThead().start();
                }

            }
        });

        new myGetThead().start();
    }

    class myGetThead extends Thread{
        StringBuilder sb;
        public void run() {
            try {
                URL url = new URL("https://tazoapp.site/users/test");
//                URL url = new URL(user);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET"); //전송방식
//                    connection.setDoOutput(true);       //데이터를 쓸 지 설정
                connection.setDoInput(true);        //데이터를 읽어올지 설정

                InputStream is = connection.getInputStream();
                sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String result;
                while((result = br.readLine())!=null){
                    sb.append(result+"\n");
                }
                try {
                    JSONObject temp=new JSONObject(sb.toString());
                }
                catch (Exception e){

                }

                msg=myhand.obtainMessage();
                msg.what=1;//구분
                msg.obj= sb.toString();
                myhand.sendMessage(msg);
                System.out.println(sb);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class myPostThred extends Thread{
        HttpURLConnection conn = null;
        JSONObject responseJson = null;
        String cd;
        public myPostThred(String str){
            this.cd=str;
        }

        public void run(){
            try {
                URL url = new URL("https://tazoapp.site/user/test/nickname");
//                URL url = new URL(user);
                conn = (HttpURLConnection) url.openConnection();
                int a=conn.getResponseCode();
                System.out.println(a);
                conn.setRequestMethod("PATCH");
                conn.setDoOutput(true);       //데이터를 쓸 지 설정
                conn.setDoInput(true);        //데이터를 읽어올지 설정
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Transfer-Encoding", "chunked");
                conn.setRequestProperty("Connection", "keep-alive");

                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                // JSON 형식의 데이터 셋팅
                JsonObject commands = new JsonObject();
                commands.addProperty("nickname","김예찬");
// 데이터를 STRING으로 변경
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonOutput = gson.toJson(commands);

                bw.write(commands.toString());
                bw.flush();
                bw.close();

            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
         }
    }



    Handler myhand=new Handler(){
      @Override
      public void handleMessage(Message msg) {
         try {
             JSONObject json2=new JSONObject((msg.obj.toString()));

//             id=json2.getInt("id");
//             id_view.setText(nickname);

             email=json2.getString("email");
             email_view.setText(email);

             nickname=json2.getString("nickname");
             nickname_view.setText(nickname);

             gender=json2.getString("gender");
             gender_view.setText(gender);

             imageURL=json2.getString("image");
             Glide.with(Profile.this).load(imageURL).into(photo);

         }
         catch (Exception e){
                System.out.println(e.getMessage());
         }
      }
    };


    @Override
    public void onBackPressed() {
        if (name_check) {
            finish();
        }
        else {
          toast = Toast.makeText(this,"설정을 완료해 주세요",Toast.LENGTH_SHORT);
          toast.show();
        }
    }




}
