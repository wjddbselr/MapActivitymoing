package app.gotogether.com.mapactivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyActivity extends AppCompatActivity {

    EditText et;
    ArrayAdapter addressAdapter;
    GridView monthView;
    MonthAdapter monthViewAdapter;
    TextView monthText;
    public int curYear;
    public int curMonth;
    public int curDay;
    String content;
    EditText edit;
    private int startYear, startMonth, startDay;
    public MonthItem curItem;
    String value;
    Dialog dig;

    //ArrayList<DayData> dayData;
    ArrayList<String> team_list; // 팀리스트 배열
    ListView team_lv; // 팀리스트 리스트뷰

    // 서버코드 - 스케쥴 불러오기
    private static String TAG = "schedule_test";
    private TextView mTextViewResult; //insert 성공시 보여주는것 - 앱에서 불필요함

    // 서버코드 - 스케쥴 일단 2로 지정 (나중에 이름 바꾸기)
    private static String TAG2 = "schedule_json";
    private static final String TAG_JSON2="schedule_json";
    private static final String TAG_ID2 = "id";
    private static final String TAG_GroupID2 = "groupId";
    private static final String TAG_Date = "date";
    private static final String TAG_Plan="plan";

    // 서버 - 그룹리스트 불러오기

    private static String TAG3 = "grouplist_json";
    private static final String TAG_JSON="grouplist_json";
    private static final String TAG_GroupID = "id";
    private static final String TAG_GroupName = "name";

    // 서버 - 멤버리스트 받아오기

    private static String TAG4 = "memberlist_json";

    private static final String TAG_JSON3="memberlist_json";
    private static final String TAG_GroupID3 = "group_id";
    private static final String TAG_UID = "uid";
    private static final String TAG_Name = "name";
    private static final String TAG_Position ="position";
    private static final String TAG_Phone ="phone";

    private String d ="";

    ListView lv;

    ArrayList<String> as = new ArrayList<String>(); // 170231    2017  7  5;

    //ArrayList<HashMap<String, String>> mArrayList;
    //ListView mlistView;
    String mJsonString;

    EditText edit_alarm;

    EditText edit_content;

    private String message = "";

    private String content_message = "";

    ArrayList<String> phone_list;

    ArrayAdapter<String> adapter;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);

        Intent intent = getIntent();

        String id = intent.getStringExtra("sId"); // 메인액티비티의 my_name의 값을 불러옴 전달받음
        String pw = intent.getStringExtra("sPw"); // 메인액티비티의 phone의 값을 불러옴 전달받음

        // 아이디와 비밀번호값 받아오기
        Toast.makeText(MyActivity.this, id, Toast.LENGTH_SHORT).show();
        Toast.makeText(MyActivity.this, pw, Toast.LENGTH_SHORT).show();


        // 월별 캘린더 뷰 객체 참조
        // 어댑터 생성
        monthView = (GridView) findViewById(R.id.monthView);
        monthViewAdapter = new MonthAdapter(this); // 어댑터
        monthView.setAdapter(monthViewAdapter);


        // 달력 누르면 아래에 일정 뜨게

        //dayData = new ArrayList<DayData>();


        // 그룹 정보 불러오기
        mTextViewResult = (TextView)findViewById(R.id.result_text);


        GetData task = new GetData();
        task.execute("http://211.253.9.84/getschedule.php");


        // 리스너 설정
        // 캘린더 버튼 누르면
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // 서버 - 정보 갖고오기

                // 현재 선택한 일자 정보 표시
                curItem = (MonthItem) monthViewAdapter.getItem(position);
                curDay = curItem.getDay();

                String day = String.valueOf(curItem.getDay()); // 현재 날짜와 같으면
                String year = String.valueOf(monthViewAdapter.getCurYear());
                String month = String.valueOf(monthViewAdapter.getCurMonth() + 1);
                d = year + month + day ;

                //Toast.makeText(MyActivity.this, date, Toast.LENGTH_SHORT).show();
                Toast.makeText(MyActivity.this, d, Toast.LENGTH_SHORT).show();

                GetData_test task = new GetData_test();
                task.execute("http://211.253.9.84/getschedule.php");

                for(String data : as) {
                Toast.makeText(MyActivity.this, data, Toast.LENGTH_SHORT).show();
            }

                lv = (ListView)findViewById(R.id.listView2);
                adapter = new ArrayAdapter<String>(MyActivity.this, android.R.layout.simple_list_item_1, as);
                lv.setAdapter(adapter);



            }
        });


        monthText = (TextView) findViewById(R.id.monthText);
        setMonthText();

        Button monthPrevious = (Button) findViewById(R.id.monthPrevious);
        monthPrevious.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setPreviousMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });

        // 다음 월로 넘어가는 이벤트 처리
        Button monthNext = (Button) findViewById(R.id.monthNext);
        monthNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                monthViewAdapter.setNextMonth();
                monthViewAdapter.notifyDataSetChanged();

                setMonthText();
            }
        });


    }

    private void setMonthText() {
        curYear = monthViewAdapter.getCurYear();
        curMonth = monthViewAdapter.getCurMonth();

        monthText.setText(curYear + "년 " + (curMonth + 1) + "월");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_context, menu);
        return true;
    }

    //액션버튼을 클릭했을때의 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        //or switch문을 이용하면 될듯 하다.
        switch (item.getItemId()){
            case R.id.context_menu01:
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("정말 탈퇴하시겠습니까?");       // 제목 설정
                builder.setMessage("ID를 입력하세요");   // 내용 설정

// EditText 삽입하기
                final EditText et = new EditText(MyActivity.this);
                builder.setView(et);

// 확인 버튼 설정
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Text 값 받아서 로그 남기기
                        value = et.getText().toString();
                        Log.v(TAG, value);
                        Toast.makeText(MyActivity.this,value,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
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
        }


        return super.onOptionsItemSelected(item);
    }

    //액션바 숨기기
    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
    }


    // 서버코드2

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MyActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG2, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult_sche();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG2, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG2, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult_sche(){
        try {

            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON2);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String id = item.getString(TAG_ID2);
                String groupId = item.getString(TAG_GroupID2);
                String date = item.getString(TAG_Date);
                String plan = item.getString(TAG_Plan);

            }

        } catch (JSONException e) {

            Log.d(TAG2, "showResult : ", e);
        }

    }

    // 서버코드2

    private class GetData_test extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MyActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG2, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult_sche_test();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG2, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG2, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult_sche_test(){


        try {

            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON2);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String date = item.getString(TAG_Date);
                String plan = item.getString(TAG_Plan);

                if(date.equals(d)){
                    as.add(plan);
                }
            }



//            for(String data : as) {
//                Toast.makeText(MyActivity.this, data, Toast.LENGTH_SHORT).show();
//            }


        } catch (JSONException e) {

            Log.d(TAG2, "showResult : ", e);
        }

    }


}

