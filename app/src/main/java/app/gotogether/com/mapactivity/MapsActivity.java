package app.gotogether.com.mapactivity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity {

    private static final String TAG = "MapsActivity";
    private static final String TAG_JSON = "gps_json";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_LONG = "longitude";
    private static final String TAG_LATI = "latitude";

    private static String TAG_USER = "schedulemember_json";

    //db랑 php랑 맞춘거라 바꾸면 안돼요~~
    private static final String TAG_GpsJSON = "schedulemember_json";
    private static final String TAG_UserPhone = "phone";
    private static final String TAG_UserName = "name";

    private MemberAdapter2 adapter2;
    String sche_id = "";


    private TextView mTextViewResult;

    ListView mlistView;
    String mJsonString;
    HashMap<String, String> hashMap = new HashMap<>();
    ArrayList<HashMap<String, String>> mArrayList;
    private ArrayList<MemberData> myDataList;
    private MemberAdapter adapter;
    ArrayList<String> team_list;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    AlertDialog.Builder builder;
    SupportMapFragment mapFragment;
    GoogleMap map;
    String value, value2;
    Dialog dig;
    ArrayList<String> phone_list;
    ArrayList<String> id_list;
    //내 위치
    double mylatitude, mylongitude;
    //친구 위치
    double flatitude, flongitude;
    private LocationManager locationManager;
    private LocationListener listener;
    MarkerOptions myLocationMarker;
    MarkerOptions friendMarker1;
    // MarkerOptions friendMarker2;

    String groupId = "";
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        groupId = "1"; //  그룹 id
        sche_id = "3";

        GetData_member task2 = new GetData_member();
        task2.execute(sche_id);
        mArrayList = new ArrayList<>();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        configure_button();
//        requestMyLocation();
        mArrayList = new ArrayList<>();

        GetData task = new GetData();
        //사용자 아이디 인텐트로 넘어온 값

        task.execute();

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String schedule_id = (String) params[0];
            String id = (String) params[1];
            String name = (String) params[2];
            String longitude = (String) params[3];
            String latitude = (String) params[4];

            String serverURL = "http://211.253.9.84/insertgps.php";

            //서버에 들어가는 주소 ""안에 있는 변수이름 절대 변경 금지!
            String postParameters = "schedule_id=" + schedule_id + "&id=" + id + "&name=" + name + "&longitude=" + longitude + "&latitude=" + latitude;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    //JSON을 읽어와서 arraylist로 변환

    private class GetData_member extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MapsActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG_USER, "response  - " + result);

            if (result == null) {

                //  mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {


            String schedule_id = (String) params[0];

            String serverURL = "http://211.253.9.84/getschedulemember.php";

            String postParameters = "schedule_id=" + schedule_id;
            try {


                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG_USER, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }


        //JSON을 읽어와서 arraylist로 변환

        private void showResult() {

            try {

                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_GpsJSON);

                team_list = new ArrayList<String>();
                phone_list = new ArrayList<String>();
                id_list = new ArrayList<String>();

                int member_num = 1;

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String user_name = item.getString(TAG_UserName);
                    String user_phone = item.getString(TAG_UserPhone);

                    team_list.add(user_name);
                    phone_list.add(user_phone);
                    id_list.add(String.valueOf(member_num));

                    member_num++;


                }

            } catch (JSONException e) {

                Log.d(TAG_USER, "showResult : ", e);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
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

    //내 위치 10초마다 갱신해서 불러오기
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
                            try {
                                showCurrentLocation(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                            try {
                                showCurrentLocation(location);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            GetData task = new GetData();
                            task.execute();
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


        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showCurrentLocation(Location location) throws JSONException {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));

        showMyLocationMarker(location);

    }

    //내 위치 띄우기
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
        mylatitude = location.getLatitude();
        mylongitude = location.getLongitude();
    }


    public void onClick(View v) {
        GetData_member task = new GetData_member();
        task.execute(sche_id);
        switch (v.getId()) {

            case R.id.message:

//                GetData_member task2 = new GetData_member();
//                task2.execute(sche_id);

                myDataList = new ArrayList<MemberData>();

                for (int i = 0; i < team_list.size(); i++) {
                    myDataList.add(new MemberData(id_list.get(i), team_list.get(i), phone_list.get(i)));
                }

                final LinearLayout list_v = (LinearLayout) View.inflate(MapsActivity.this, R.layout.activity_member_list, null);
                ListView list = (ListView) list_v.findViewById(R.id.member_listView);
                adapter2 = new MemberAdapter2(MapsActivity.this, R.layout.activity_gps_member2, myDataList);
                list.setAdapter(adapter2);

                AlertDialog.Builder boss = new AlertDialog.Builder(MapsActivity.this);
                boss.setView(list_v); // 리스트뷰 다이얼로그에 넣기
                boss.setTitle("메세지 보내기");       // 제목 설정
                boss.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                boss.show();
                break;


            case R.id.phone:

//                GetData_member task3 = new GetData_member();
//                task3.execute(sche_id);

                myDataList = new ArrayList<MemberData>();

                for (int i = 0; i < team_list.size(); i++) {
                    myDataList.add(new MemberData(id_list.get(i), team_list.get(i), phone_list.get(i)));
                }

                final LinearLayout list_view = (LinearLayout) View.inflate(MapsActivity.this, R.layout.activity_member_list, null);
                ListView list2 = (ListView) list_view.findViewById(R.id.member_listView);
                adapter = new MemberAdapter(MapsActivity.this, R.layout.activity_gps_member, myDataList);
                list2.setAdapter(adapter);

                AlertDialog.Builder boss2 = new AlertDialog.Builder(MapsActivity.this);
                boss2.setView(list_view); // 리스트뷰 다이얼로그에 넣기
                boss2.setTitle("전화 걸기");// 제목 설정
                boss2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                boss2.show();


                break;

//        case R.id.button_dial:
//        Intent i = new Intent(Intent.ACTION_CALL);
//        i.setData(Uri.parse("tel:" + value));
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        startActivity(i);
//        break;
        case R.id.button_start:
        builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS승인");
        builder.setMessage("GPS를 키시겠습니까?");
        builder.setIcon(R.mipmap.ic_start);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                configure_button();
                requestMyLocation();
                String schedule_id = "3";
                String id = "sss";
                String name = "yeon";
                String longitude3 = Double.toString(mylongitude);
                String latitude3 = Double.toString(mylatitude);

//서버에 내 gps위치 넣기
                //InsertData task = new InsertData();

                //task.execute(schedule_id, id, name, longitude3, latitude3); // 순서 꼭 이순서로 해줘~

                //GetData task2 = new GetData();
                //task2.execute("http://211.253.9.84/getgps.php");

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
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;
        String mJsonString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            progressDialog = ProgressDialog.show(MapsActivity.this,
//                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

           // progressDialog.dismiss();
            Log.d(TAG, "response  - " + result);

            if (result == null) {

                mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();

            }
        }

        @Override
        protected String doInBackground(String... params) {


            String schedule_id = "3";
            String serverURL = "http://211.253.9.84/getgps.php";
            String postParameters = "schedule_id=" + schedule_id;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                //httpURLConnection.setRequestProperty("content-type", "application/json");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }

        private void showResult() {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                int pictureResId = R.mipmap.ic_start;
                HashMap<String, String> hashMap = new HashMap<>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String id = item.getString(TAG_ID);
                    String name = item.getString(TAG_NAME);
                    String longitude = item.getString(TAG_LONG);
                    String latitude = item.getString(TAG_LATI);


                    hashMap.put(TAG_NAME, name);
                    hashMap.put(TAG_LONG, longitude);
                    hashMap.put(TAG_LATI, latitude);

                    mArrayList.add(hashMap);

                    if (friendMarker1 == null) {

                        // for (int i = 0; i < jsonArray.length(); i++) {

//                JSONObject item = jsonArray.getJSONObject(i);
//                String schedule_id = "3";
//                String id = "ys";
//                String name = "yeseul";
//                String longitude = "longitude";
//                String latitude = "latitude";

//                JSONObject item = jsonArray.getJSONObject(i);
//                String name = item.getString(TAG_NAME);
//                String id = item.getString(TAG_ID);
//                String longitude = item.getString(TAG_LONG);
//                String latitude = item.getString(TAG_LATI);
                        //서버에 gps 위도 경도 넣기
//                hashMap.put(TAG_ID, id);
//                hashMap.put(TAG_NAME, name);
//                hashMap.put(TAG_LONG, String.valueOf(longitude));
//                hashMap.put(TAG_LATI, String.valueOf(latitude));
//                mArrayList.add(hashMap);
                        // }

                        HashMap get = new HashMap();
                        double flongitude2;
                        double flatitude2;

                        for (int j = 0; j < mArrayList.size(); j++) {
                            //서버에서 gps위도 경도 받아오기

                            get = (HashMap) mArrayList.get(j);

                            String iname = (String)get.get("name");
                            String a = (String) get.get("latitude");
                            String b = (String) get.get("longitude");

                            Log.i(TAG, "제발: " + a + b);

                            flatitude2 = Double.parseDouble(a);
                            flongitude2 = Double.parseDouble(b);


                            Log.i(TAG, "위도: " + flatitude2 + flongitude2);

                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng currentLocation = new LatLng(flatitude2, flongitude2);

                            markerOptions.position(currentLocation);
                            markerOptions.title("친구 " + iname);

                            map.addMarker(markerOptions);

                        }
                    } else {
                        friendMarker1.position(new LatLng(3000, 3000));
                    }

                }


            } catch (JSONException e) {

                Log.d(TAG, "showResult : ", e);
            }

        }

    }
}





