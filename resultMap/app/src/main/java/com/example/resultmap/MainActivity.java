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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Boolean check=true;

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
    Marker marker;

    JsonParser pa;
    JSONObject object;
    JSONObject object2;

    private DrawerLayout drawerLayout;
    private  View menubar;

    Spinner spinner;
    ArrayAdapter<String> arrayAdapter;

    LinearLayout mLlinearLayout;

    Profile profile;

//    *********************************************************
//    안드로이드 채팅 연결
    private Button chatting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                List<Address> addresses ;

                try {
                    Geocoder geocoder=new Geocoder(getBaseContext());
                    addresses = geocoder.getFromLocationName(str, 3);
                    if (addresses != null && !addresses.equals(" ")) {
//                        search(addresses);
//                        System.out.println(addresses.size());
//                        for(int i=0;i<addresses.size();i++){
//                            System.out.println(addresses.get(i));
//                        }

                        System.out.println(addresses.get(0));
                        search(addresses);
                    }
                } catch(Exception e) {
                    System.out.println("연결실패");
                    System.out.println(e.getMessage());
                }


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
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),array[i]+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        menubutton=findViewById(R.id.menubutton);
        menubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(menubar);
            }
        });


//        출처: https://bottlecok.tistory.com/63 [잡캐의 IT 꿀팁]
//        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),array[position]+"가 선택되었습니다.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

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


        chatting = (Button) findViewById(R.id.Chatting);
        chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Chatting.class);
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
        marker = new Marker();
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


}