package app.gotogether.com.mapactivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity {
    private static final String TAG = "MapsActivity";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =1;
    AlertDialog.Builder builder;
    SupportMapFragment mapFragment;
    GoogleMap map;
    String value,value2;
    Dialog dig;
    double latitude,longitude;
    private LocationManager locationManager;
    private LocationListener listener;

    MarkerOptions myLocationMarker;
    MarkerOptions friendMarker1;
    MarkerOptions friendMarker2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "GoogleMap is ready.");

                map = googleMap;

            }
        });

        try {
            MapsInitializer.initialize(this);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }
    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_dial:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("전화 걸 친구 추가");       // 제목 설정
                builder.setMessage("전화번호를 입력하세요");   // 내용 설정

// EditText 삽입하기
                final EditText et = new EditText(MapsActivity.this);
                builder.setView(et);

// 확인 버튼 설정
                builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Text 값 받아서 로그 남기기
                        value = et.getText().toString();
                        Log.v(TAG, value);

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
            case R.id.add_sos:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("sos 문자 보낼 친구 추가");       // 제목 설정
                builder.setMessage("전화번호를 입력하세요");   // 내용 설정

// EditText 삽입하기
                final EditText et2 = new EditText(MapsActivity.this);
                builder.setView(et2);

// 확인 버튼 설정
                builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Text 값 받아서 로그 남기기
                        value2 = et2.getText().toString();
                        Log.v(TAG, value2);

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
            case R.id.button_sos:
                String message = "sos! 위도: "+latitude + "경도: "+longitude;
                String tel = value2;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                else{
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(tel,null,message,null,null);

                }
                break;
            case R.id.button_dial:
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+value));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(i);
                break;
            case R.id.button_start:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS승인");
                builder.setMessage("GPS를 키시겠습니까?");
                builder.setIcon(R.mipmap.ic_start);
                builder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        configure_button();
                        requestMyLocation();
                    }
                });
                builder.setNegativeButton("취소", null);

                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
            case R.id.button_end:
                builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS거부");
                builder.setMessage("GPS를 끄시겠습니까?");
                builder.setIcon(R.mipmap.ic_end);
                builder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent);
                    }

                });

                builder.setNegativeButton("취소", null);

                dig = builder.create();
                dig.setCanceledOnTouchOutside(false);
                dig.show();
                break;
        }
    }

    private void requestMyLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            long minTime = 10000;
            float minDistance = 0;
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                            addPictures(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }

                    }
            );

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                showCurrentLocation(lastLocation);
            }

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            showCurrentLocation(location);
                            addPictures(location);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );


        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        showMyLocationMarker(location);
        addPictures(location);
    }

    private void showMyLocationMarker(Location location) {
        if (myLocationMarker == null) {
            myLocationMarker = new MarkerOptions();
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            myLocationMarker.title("내위치");
            myLocationMarker.snippet("내위치");
            myLocationMarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.dial));
            map.addMarker(myLocationMarker);
        } else {
            myLocationMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    private void addPictures(Location location) {
        int pictureResId = R.mipmap.ic_start;

        if (friendMarker1 == null) {
            friendMarker1 = new MarkerOptions();
            friendMarker1.position(new LatLng(37.396912, 127.126074));
            friendMarker1.title("친구 1\n");
            friendMarker1.icon(BitmapDescriptorFactory.fromResource(pictureResId));
            map.addMarker(friendMarker1);
        } else {
            friendMarker1.position(new LatLng(location.getLatitude()+3000, location.getLongitude()+3000));
        }

        pictureResId = R.mipmap.ic_end;

        if (friendMarker2 == null) {
            friendMarker2 = new MarkerOptions();
            friendMarker2.position(new LatLng(37.396802, 127.122814));
            friendMarker2.title("친구 2\n");
            friendMarker2.icon(BitmapDescriptorFactory.fromResource(pictureResId));
            map.addMarker(friendMarker2);
        } else {
            friendMarker2.position(new LatLng(location.getLatitude()+2000, location.getLongitude()-1000));
        }

    }



    @Override
    protected void onPause() {
        super.onPause();

        if (map != null) {
            map.setMyLocationEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.setMyLocationEnabled(true);
        }
    }

}
