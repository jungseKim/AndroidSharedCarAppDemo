package com.example.resultmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Boolean check=true;

    int myId;

    private Button button,profile_btn;
    private ImageButton cahage_Button,menubutton,myLocation;
    private EditText text;

    private  static int permission_code=100;
    private  static String[] permissions={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,

    };
    private FusedLocationSource mLoction;
    private NaverMap mNaverMap;
    Marker marker=new Marker();

    private DrawerLayout drawerLayout;
    private  View menubar;

    TextView spinner;
    ArrayAdapter<String> arrayAdapter;

    LinearLayout mLlinearLayout;
    Message msg;
    OkHttpClient client=new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new myGetThead().start();

        Button speed=findViewById(R.id.speed);
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client=new OkHttpClient();
//        RequestBody Body=new FormBody.Builder().add("nickname",str).build();
                Request request=new Request.Builder().url("https://tazoapp.site/rooms")
                        .get().build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("연결실패");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("연결성공");
                        try {
                            JSONArray json=new JSONArray(response.body().string());
                            msg=handler2.obtainMessage();
                            msg.what=1;//구분
                            msg.obj= json;
                            handler2.sendMessage(msg);
                            System.out.println(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


        mLoction=new FusedLocationSource(this,permission_code);


        button=findViewById(R.id.button);
        text=findViewById(R.id.text);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=text.getText().toString();
                System.out.println(str);
                new searchlocation(str).start();
            }

        });
        myLocation=findViewById(R.id.myLocation);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng2 = new LatLng(mLoction.getLastLocation().getLatitude(),mLoction.getLastLocation().getLongitude());
                CameraUpdate cameraUpdate2=CameraUpdate.scrollTo(latLng2);
                cameraUpdate2.zoomTo(15.0);
                mNaverMap.moveCamera(cameraUpdate2);
            }
        });

        drawerLayout =(DrawerLayout)findViewById(R.id.drawer_layout);
        menubar=(View) findViewById(R.id.menu);
        drawerLayout.setDrawerListener(listener);
        Button close=findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        String[] array={" 영진대 후문","영진대 정문"};
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                array);

        spinner=findViewById(R.id.spinner);

        menubutton=findViewById(R.id.menubutton);
        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(menubar);
            }
        });


        mLlinearLayout = findViewById(R.id.mLlinearLayout);

        cahage_Button=findViewById(R.id.cahage_Button);
        cahage_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check){
                    mLlinearLayout.removeAllViews();
                    mLlinearLayout.addView(spinner);
                    mLlinearLayout.addView(text);
                    text.setText(null);
                    text.setHint("      도착지");
                    Toast.makeText(getApplicationContext(),"도착지를 선택해 주세요",
                            Toast.LENGTH_SHORT).show();
                    check=false;
                }
                else{
                    mLlinearLayout.removeAllViews();
                    mLlinearLayout.addView(text);
                    mLlinearLayout.addView(spinner);
                    text.setText(null);
                    text.setHint("      출발지");
                    Toast.makeText(getApplicationContext(),"출발지를 선택해 주세요",
                            Toast.LENGTH_SHORT).show();
                    check=true;
                }
            }
        });
        profile_btn=findViewById(R.id.profile_btn);
        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Profile.class);
                startActivity(intent);
            }
        });
    }

    DrawerLayout.DrawerListener listener =new DrawerLayout.DrawerListener(){

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

    protected void search(List<Address> addresses) {
        Address address = addresses.get(0);
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        System.out.println(latLng);
        CameraUpdate cameraUpdate=CameraUpdate.scrollTo(latLng);
        cameraUpdate.zoomTo(15.0);
        mNaverMap.moveCamera(cameraUpdate);
        marker.setPosition(latLng);
        marker.setMap(mNaverMap);
    }


    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        ActivityCompat.requestPermissions(this,permissions,permission_code);

        mNaverMap=naverMap;
        mNaverMap.setLocationSource(mLoction);

//        mNaverMap.setOnMapClickListener((point, coord) ->{
//                    marker.setPosition(new LatLng(coord.latitude,coord.longitude));
//                    marker.setMap(mNaverMap);
//                }
//                Toast.makeText(this, coord.latitude + ", " + coord.longitude, Toast.LENGTH_SHORT).show()


//        );


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        Toast.makeText(this,"request함수 호출됨",Toast.LENGTH_LONG).show();
        if(requestCode==permission_code){
            if(grantResults.length>0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
            }
        }

    }
    class searchlocation extends Thread{
        String str;
        int index=0;
        public searchlocation(String str){
            this.str=str;
        }
        @Override
        public void run() {
            super.run();
            List<Address> addresses ;
            String temp=str;

            try {
                Geocoder geocoder=new Geocoder(getBaseContext());
                System.out.println(str);
                addresses = geocoder.getFromLocationName(str, 10);
                Address address = addresses.get(0);
                System.out.println(address);
                System.out.println(address.getAdminArea());
                if (!address.getAdminArea().equals("대구광역시")&&!address.getLocality().equals(" 대구광역시")){
                    throw new Exception();
                }
                if (address != null && !address.equals(" ")) {
                    msg=handler.obtainMessage();
                    msg.what=1;//구분
                    msg.obj= address.getLatitude()+" "+address.getLongitude();
                    handler.sendMessage(msg);
                }
            } catch(Exception e) {
                if ((index==0))str="대구 복현동 "+str;
                if (index==1)str="대구 "+temp;
                if (index<2)run();
                index++;
            }
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try{
                String[] a=msg.obj.toString().split(" ");
                LatLng latLng = new LatLng(Double.parseDouble(a[0]), Double.parseDouble(a[1]));

                CameraUpdate cameraUpdate=CameraUpdate.scrollTo(latLng);
                cameraUpdate.zoomTo(15.0);
                mNaverMap.moveCamera(cameraUpdate);
                marker.setPosition(latLng);
                marker.setMap(mNaverMap);

            }catch (Exception e){
                e.getStackTrace();
                System.out.println(e.getMessage());
            }
        }
    };
    Handler handler2=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            JSONArray array= null;
            JSONObject object=null;
            JSONArray array2= null;
            try {
                array = new JSONArray(msg.obj.toString());
                object=(JSONObject)array.get(0);
                array2=object.getJSONArray("Members");
                for (int i=0;i<array2.length();i++){
                    JSONObject temp=(JSONObject) array2.get(i);
                    if(myId==temp.getInt("id"))
                        System.out.println(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    class myGetThead extends Thread{
        StringBuilder sb;
        StringBuilder sb2;
        public void run() {
            try {
                URL url = new URL("https://tazoapp.site/users/test");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                InputStream is = connection.getInputStream();
                sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String result;
                while((result = br.readLine())!=null){
                    sb.append(result+"\n");
                }
                try {
                    JSONObject temp=new JSONObject(sb.toString());
                    myId=temp.getInt("id");
                    System.out.println("dddddd"+temp.getInt("id"));
                }catch (Exception e){

                }
                URL url2 = new URL("https://tazoapp.site/rooms");

                HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
                connection2.setRequestMethod("GET");
                connection2.setDoInput(true);

                InputStream is2 = connection2.getInputStream();
                sb2 = new StringBuilder();
                BufferedReader br2 = new BufferedReader(new InputStreamReader(is2,"UTF-8"));
                String result2;
                while((result2 = br2.readLine())!=null){
                    sb2.append(result2+"\n");
                }
                System.out.println(sb2);
                try {
                    JSONArray array=new JSONArray(sb.toString());
                    JSONObject object=(JSONObject)array.get(0);
                    JSONArray array2=object.getJSONArray("Members");
                    for (int i=0;i<array2.length();i++){
                        JSONObject temp=(JSONObject) array2.get(i);
                        if(myId==temp.getInt("id"))
                            System.out.println(temp);
                    }

                }catch (Exception e){

                }


//                msg=myhand.obtainMessage();
//                msg.what=1;//구분
//                msg.obj= sb.toString();
//                myhand.sendMessage(msg);
//                System.out.println(sb);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}