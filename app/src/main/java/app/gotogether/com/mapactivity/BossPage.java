
package app.gotogether.com.mapactivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static app.gotogether.com.mapactivity.R.id.listView;

public class BossPage extends AppCompatActivity {

    // 달력에 대한 변수들 지정
    EditText et;
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

    // 서버공통 - 성공시 텍스트 띄우기
    private TextView mTextViewResult; //insert 성공시 보여주는것 - 앱에서 불필요함
    String mJsonString;

    // 서버코드 - 스케쥴 불러오기
    private static String TAG = "schedule_test";

    // 서버코드 - 스케쥴 정보 불러오기 (나중에 이름 바꾸기)
    private static String TAG2 = "schedule_json";
    private static final String TAG_JSON2="schedule_json";
    private static final String TAG_ID2 = "id";
    private static final String TAG_GroupID2 = "groupId";
    private static final String TAG_Date = "date";
    private static final String TAG_Plan="plan";

    // 서버코드 - 그룹리스트 불러오기
    private static String TAG3 = "grouplist_json";
    private static final String TAG_JSON="grouplist_json";
    private static final String TAG_GroupID = "id";
    private static final String TAG_GroupName = "name";

    // 서버코드 - 멤버리스트 받아오기
    private static String TAG4 = "memberlist_json";
    private static final String TAG_JSON3="memberlist_json";
    private static final String TAG_GroupID3 = "group_id";
    private static final String TAG_UID = "uid";
    private static final String TAG_Name = "name";
    private static final String TAG_Position ="position";
    private static final String TAG_Phone ="phone";

    // 서버코드 - 대기자리스트
    private static String TAG5 = "admission_json";
    private static final String TAG_JSON5="admission_json";
    private static final String TAG_GroupID5 = "group_id";
    private static final String TAG_ID5 = "id";
    private static final String TAG_Name5 = "name";
    private static final String TAG_Phone5 ="phone";

    // 팀장위임
    private static String TAG6 = "update_captain";

    // 그룹이름변경
    private static String TAG7 = "update_captain";

    // 대기자 분리하기
    ArrayList<String> items;
    ArrayList<String> ok_id;
    private okMyAdapter ok_myAdapter;
    private ArrayList<OkData> okDataList;

    // 클릭시 달력 하단에 리스트뷰 뜨도록
    ArrayAdapter<String> adapter;
    ArrayList<String> as;
    ListView lv;
    ArrayList<String> team_list;

    // 알람 보내기
    EditText edit_alarm;
    EditText edit_content;
    private String message = ""; // 보낼 메세지
    ArrayList<String> phone_list; // 전화 걸 목록

    // 일정 수정하기
    private String content_message = ""; // 수정할 내용
    private String d = "";
    private String p_data = "";
    private String sche_id = ""; // 수정할 일정 id

    // 팀장위임
    private ArrayList<MyData> myDataList;
    private MyAdapter myAdapter;
    private String result = "";
    ArrayList<String> id_list;

    String groupId = "";
    String my_id = "";

    // 그룹이름변경
    TextView text;


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =1;

    /* 추가)
       1. 정확한값 넣고 돌려보기
       2. wait 서버코드 합치고 -> group_id 받아온것 쓰기
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_boss_page);

        // 월별 캘린더 뷰 객체 참조
        // 어댑터 생성
        monthView = (GridView) findViewById(R.id.monthView);
        monthViewAdapter = new MonthAdapter(this); // 어댑터
        monthView.setAdapter(monthViewAdapter);

        // 그룹과 자신의 id값 받아오기
        Intent intent = getIntent(); // 운영체에가 인텐트를 띄어줌 인텐트를 받아옴
        //groupId = intent.getStringExtra("group_id"); //  그룹 id
        groupId = "1"; //  그룹 id
        my_id = intent.getStringExtra("my_id"); // 개인 id
        //Toast.makeText(BossPage.this, groupId, Toast.LENGTH_SHORT).show();


        // 달력 누르면 아래에 일정 뜨게
        lv = (ListView)findViewById(listView);

        // 그룹 정보 불러오기
        mTextViewResult = (TextView)findViewById(R.id.result_text);

        // 그룹리스트 DB 갖고오기
        GetData2 task = new GetData2(); // 서버에서 데이터 갖고오기
        task.execute("http://211.253.9.84/getgrouplist.php");

        // 팀원리스트 DB 갖고오기 -> 팀원 수 계산 , 팀장 이름 갖고오기
        GetData3 task2 = new GetData3();
        task2.execute("http://211.253.9.84/getmemberlist.php");

        // 리스너 설정
        // 캘린더 버튼 누르면
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // 현재 선택한 일자 정보 표시
                curItem = (MonthItem) monthViewAdapter.getItem(position);
                curDay = curItem.getDay();

                // 서버 - 정보 갖고오기

                // 일정정보가져오기
                GetData task = new GetData();
                task.execute("http://211.253.9.84/getschedule.php");

            }
        });

        // 일정수정
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 비교할 값 갖고오기

                p_data= as.get(position);
                String day = String.valueOf(curItem.getDay()); // 현재 날짜와 같으면
                String year = String.valueOf(monthViewAdapter.getCurYear());
                String month = String.valueOf(monthViewAdapter.getCurMonth() + 1);
                d = year + month + day ;

                GetData_test task = new GetData_test();
                task.execute("http://211.253.9.84/getschedule.php");

                AlertDialog.Builder alarm = new AlertDialog.Builder(BossPage.this);

                alarm.setTitle("수정하기");       // 제목 설정
                alarm.setMessage("일정을 수정해주세요");   // 내용 설정
                edit_content = new EditText(BossPage.this);
                alarm.setView(edit_content);
                alarm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        content_message = edit_content.getText().toString();

                        InsertData2 task2 = new InsertData2();
                        task2.execute(sche_id, content_message); // 넘어갈 id와 내용

                        GetData task3 = new GetData();
                        task3.execute("http://211.253.9.84/getschedule.php");

                        dialog.dismiss();
                    }
                });

                alarm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }


                });

                Toast.makeText(BossPage.this, content_message, Toast.LENGTH_SHORT).show();

                alarm.show();


            }

        });

        // *예슬
        // 롱클릭시 일정삭제
        // 추가) 우선 intent 페이지로 구현 후 후에 수정 ( 아마 수정, 삭제는 커스텀 버튼으로, intent는 클릭으로)

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                // 비교할 값 갖고오기

                p_data= as.get(position);
                String day = String.valueOf(curItem.getDay()); // 현재 날짜와 같으면
                String year = String.valueOf(monthViewAdapter.getCurYear());
                String month = String.valueOf(monthViewAdapter.getCurMonth() + 1);
                d = year + month + day ;

                AlertDialog.Builder builder = new AlertDialog.Builder(BossPage.this);
                builder.setTitle("이동");
                builder.setMessage("이동하시겠습니까?");

                // id만 넘기기
                GetData_test task = new GetData_test();
                task.execute("http://211.253.9.84/getschedule.php");


                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        GetData task3 = new GetData();
                        task3.execute("http://211.253.9.84/getschedule.php");

                        //*예슬
                        //Toast.makeText(BossPage.this, sche_id, Toast.LENGTH_SHORT).show(); // sche_id 이게 넘어갈 id

                        Intent intent = new Intent(BossPage.this, MapsActivity.class);
                        intent.putExtra("group_id", "1"); // 그룹 id
                        intent.putExtra("my_id", "sss"); // 내 id
                        intent.putExtra("schedule_id", sche_id); // 스케쥴 id
                        startActivity(intent);


                    }});
                builder.setNegativeButton("아니요", null);


                builder.create().show();


                return true;
            }

        });


        monthText = (TextView) findViewById(R.id.monthText);
        setMonthText();

        // 지난 월로 넘어가는 이벤트 처리
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


    public void onClick(View v){
        switch(v.getId()){

            // 일정 추가 코드
            case R.id.plus :

                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                View customView = inflater.inflate(R.layout.activity_datetimepicker, null);

                final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(customView); // Set the view of the dialog to your custom layout
                builder.setTitle("일정추가");

                View view = (View) getLayoutInflater().inflate(R.layout.activity_datetimepicker, null);

                // 서버 성공하면 insert
                mTextViewResult = (TextView)findViewById(R.id.result_text);

                builder.setPositiveButton("추가", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startYear = dpStartDate.getYear();
                        startMonth = dpStartDate.getMonth() + 1;
                        startDay = dpStartDate.getDayOfMonth();

                        edit = (EditText)((AlertDialog)dialog).findViewById(R.id.editText2);
                        content = edit.getText().toString(); // 일정내용

                        String y = String.valueOf(startYear);
                        String m = String.valueOf(startMonth);
                        String d = String.valueOf(startDay);

                        String date_D = y + m + d;

                        InsertData task = new InsertData();
                        task.execute(date_D, content);

                        // 후에 추가하면 동그라미 버튼 나타나게 or 색이 바뀌게
                        dialog.dismiss();
                    }});

                builder.setNegativeButton("취소", null);
                builder.create().show();

                break;

            // 팀원승인코드
            //*예슬 - GetData_wait 클래스안에다 구현했어!
            case R.id.newbtn:

                GetData_wait task_wait = new GetData_wait();
                task_wait.execute("http://211.253.9.84/getadmission.php");

                break;

            // 그룹이름변경코드
            //*예슬
            case R.id.btnTitle :

                AlertDialog.Builder ad = new AlertDialog.Builder(BossPage.this);

                ad.setTitle("이름변경");       // 제목 설정
                ad.setMessage("변경할 이름을 입력해주세요");   // 내용 설정

                et = new EditText(this);
                ad.setView(et);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = et.getText().toString();

                        UpdateData_groupName task = new UpdateData_groupName();
                        task.execute(groupId, name);

                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }


                });

                ad.show();

                break;

            // 팀원관리코드 (팀원 목록 보기)
            case R.id.member :

                GetData3 task = new GetData3();
                task.execute("http://211.253.9.84/getmemberlist.php");

                final LinearLayout member_view = (LinearLayout)View.inflate(this, R.layout.activity_information, null);
                ListView list = (ListView)member_view.findViewById(R.id.team_listView);
                adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,team_list);
                list.setAdapter(adapter);

                AlertDialog.Builder member = new AlertDialog.Builder(BossPage.this);

                member.setView(member_view); // 리스트뷰 다이얼로그에 넣기
                member.setTitle("팀원관리");       // 제목 설정
                member.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }});

                member.show();

                // 나중에 커스텀으로 구성할것!

                break;


            // 팀원 전체에게 알람 보내기 (문자메세지)
            case R.id.alarm :

                AlertDialog.Builder alarm = new AlertDialog.Builder(BossPage.this);

                alarm.setTitle("알림보내기");       // 제목 설정
                alarm.setMessage("팀원들에게 보낼 메세지를 입력해주세요");   // 내용 설정

                edit_alarm = new EditText(this);
                alarm.setView(edit_alarm);
                alarm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String tel = "01020891228";

                        message = edit_alarm.getText().toString();

                        //String tel = value2;
                        if (ContextCompat.checkSelfPermission(BossPage.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(BossPage.this, new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                        }
                        else{

                            GetData3 task = new GetData3();
                            task.execute("http://211.253.9.84/getmemberlist.php");


                            SmsManager smsManager = SmsManager.getDefault();

                            for(int i=0;i< phone_list.size();i++){
                                smsManager.sendTextMessage(phone_list.get(i) ,null,message,null,null);
                                //Toast.makeText(BossPage.this, phone_list.get(i), Toast.LENGTH_SHORT).show();

                            }
                        }

                        dialog.dismiss();
                    }
                });

                alarm.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }

                });

                alarm.show();

                break;


        }
    }

    // 리스트뷰 업데이트
    public void updateLv(){
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,as);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bossmenu_context, menu);
        return true;
    }


    //*예슬
    // 팀장위임코드
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();

        switch (id){
            case R.id.context_boss:

                GetData3 task = new GetData3();
                task.execute("http://211.253.9.84/getmemberlist.php");

                // memberlist 값들을 myDataList에 모두 넣어준다
                int count = 0;

                myDataList = new ArrayList<MyData>();

                for(int i = 0; i < team_list.size(); i++){
                    count ++;
                    myDataList.add(new MyData(id_list.get(i),team_list.get(i)));
                }

                final LinearLayout boss_view = (LinearLayout)View.inflate(this, R.layout.activity_boss, null);
                ListView list = (ListView)boss_view.findViewById(R.id.boss_listView);
                myAdapter = new MyAdapter(BossPage.this, R.layout.activity_new, myDataList);
                list.setAdapter(myAdapter);

                AlertDialog.Builder boss = new AlertDialog.Builder(BossPage.this);
                boss.setView(boss_view); // 리스트뷰 다이얼로그에 넣기
                boss.setTitle("팀장위임");       // 제목 설정
                boss.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //String group_id = "1";
                        result = myAdapter.getResult();

                        Toast.makeText(BossPage.this, result, Toast.LENGTH_SHORT).show();

                        if(!result.equals("")) {
                            UpdateData task = new UpdateData();
                            task.execute(groupId, result);
                        }


                    }});

                boss.show();
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




    // *예슬
    // 여기서부터 전부 서버코드

    // 서버 - 스케줄입력
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String date = (String)params[0];
            String plan = (String)params[1];
            int group_id = Integer.parseInt(groupId);

            String serverURL = "http://211.253.9.84/insertschedule.php";
            String postParameters = "groupId=" + group_id + "&date=" + date + "&plan=" + plan;


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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
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

    // 서버 - 스케줄 데이터 수정
    class InsertData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String id = (String)params[0];
            String plan = (String)params[1];

            String serverURL = "http://211.253.9.84/updateschedule.php";
            String postParameters = "id=" + id + "&plan=" + plan;


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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
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

    // 서버 - 스케줄 아이디 받아오기
    private class GetData_test extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
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
                String id = item.getString(TAG_ID2);
                String plan = item.getString(TAG_Plan);
                String group_id = item.getString(TAG_GroupID2);


                if(group_id.equals(groupId)) {
                    if (date.equals(d)) {
                        if (plan.equals(p_data)) {
                            sche_id = id;
                            //Toast.makeText(BossPage.this, sche_id, Toast.LENGTH_SHORT).show(); // sche_id 이게 넘어갈 id
                        }
                    }
                }

            }

        } catch (JSONException e) {

            Log.d(TAG2, "showResult : ", e);
        }

    }

    // 서버 - 스케줄 받아오기
    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
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

            as = new ArrayList<String>(); // 170231    2017  7  5

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String id = item.getString(TAG_ID2);
                String group_id = item.getString(TAG_GroupID2);
                String date = item.getString(TAG_Date);
                String plan = item.getString(TAG_Plan);

                String day = String.valueOf(curItem.getDay()); // 현재 날짜와 같으면
                String year = String.valueOf(monthViewAdapter.getCurYear());
                String month = String.valueOf(monthViewAdapter.getCurMonth() + 1);
                String d = year + month + day ;

                //Toast.makeText(BossPage.this, date, Toast.LENGTH_SHORT).show();
                //Toast.makeText(BossPage.this, d, Toast.LENGTH_SHORT).show();

                if(group_id.equals(groupId)) {
                    if (date.equals(d)) {
                        as.add(plan); // 스케쥴 가져오기
                    }
                }

            }

            updateLv();

        } catch (JSONException e) {

            Log.d(TAG2, "showResult : ", e);
        }

    }

    // 서버 - 그룹 리스트 받아오기
    private class GetData2 extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG3, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult_team();
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
                Log.d(TAG3, "response code - " + responseStatusCode);

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

                Log.d(TAG3, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult_team(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            TextView text = (TextView) findViewById(R.id.group_title);
            text.setText("뀨");

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String group_id = item.getString(TAG_GroupID);
                String group_name = item.getString(TAG_GroupName);

                //Toast.makeText(BossPage.this, group_id, Toast.LENGTH_SHORT).show();

                // 팀에 맞는 이름 설정

                // id는 임시로 나중에 intent로 받아올것
                if(group_id.equals(groupId)) {
                    // 이름 갖고오기
                    text = (TextView) findViewById(R.id.group_title);
                    text.setText(group_name);

                    Toast.makeText(BossPage.this, group_name, Toast.LENGTH_SHORT).show();


                }


            }


        } catch (JSONException e) {

            Log.d(TAG3, "showResult : ", e);
        }

    }

    // 서버 - 팀원리스트 받아오기
    private class GetData3 extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG4, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult3();
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
                Log.d(TAG4, "response code - " + responseStatusCode);

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

                Log.d(TAG4, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult3(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON3);

            team_list = new ArrayList<String>();
            phone_list = new ArrayList<String>();
            id_list = new ArrayList<String>();

            int member_num = 0;
            String boss_name = "";


            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String group_id = item.getString(TAG_GroupID3);
                String uid = item.getString(TAG_UID);
                String name = item.getString(TAG_Name);
                String position = item.getString(TAG_Position);
                String phone = item.getString(TAG_Phone);

                // *중요* 나중에 꼭 바꾸기
                if(group_id.equals(groupId)){
                    // 팀장일때
                    if(position.equals("1")){
                        boss_name = name;
                    }
                    team_list.add(name);
                    phone_list.add(phone);
                    id_list.add(uid);

                    member_num ++;
                }


            }

            // 팀 수와 팀장 이름 갖고오기

            TextView textNum = (TextView)findViewById(R.id.textNum);
            textNum.setText(String.valueOf(member_num));

            TextView bossName = (TextView)findViewById(R.id.bossName);
            bossName.setText(boss_name);


        } catch (JSONException e) {

            Log.d(TAG4, "showResult : ", e);
        }

    }

    // 서버 - 대기자리스트 받아오기
    private class GetData_wait extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG5, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult_wait();
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
                Log.d(TAG5, "response code - " + responseStatusCode);

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

                Log.d(TAG5, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult_wait(){

        items = new ArrayList<String>();
        ok_id = new ArrayList<String>();

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON5);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                String group_id = item.getString(TAG_GroupID5);
                String id = item.getString(TAG_ID5);
                String name = item.getString(TAG_Name5);
                String phone = item.getString(TAG_Phone5);

                items.add(name);
                ok_id.add(id);


            }

            // *예슬
            // 수락된 팀원리스트와 수락받지못한 팀원리스트 구분

            // 배열로 바꾸기 (동적배열)
//            final String[] simpleArray = new String[ items.size() ];
//            items.toArray( simpleArray );

//            final ArrayList<String> selectedItems = new ArrayList<String>(); // 수락된 팀원리스트
//            final ArrayList<String> nselectedItems = new ArrayList<String>(); // 수락 x 팀원리스트

            okDataList = new ArrayList<OkData>();

            for(int i = 0; i < items.size(); i++){
                okDataList.add(new OkData(ok_id.get(i),items.get(i), groupId));
            }

            final LinearLayout ok_view = (LinearLayout)View.inflate(this, R.layout.activity_ok, null);
            ListView list = (ListView)ok_view.findViewById(R.id.listView_ok);
            ok_myAdapter = new okMyAdapter(BossPage.this, R.layout.activity_ok_custom, okDataList);
            list.setAdapter(ok_myAdapter);

            AlertDialog.Builder ok = new AlertDialog.Builder(BossPage.this);
            ok.setView(ok_view); // 리스트뷰 다이얼로그에 넣기
            ok.setTitle("팀원수락");       // 제목 설정
            ok.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // 수락코드

                }});

            ok.show();


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    // 서버코드 - 팀장위임

    class UpdateData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG6, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {



            String serverURL = "http://211.253.9.84/updatecaptain.php";
            String group_id = (String) params[0];
            String id = (String) params[1];

            String postParameters = "group_id=" + group_id + "&id=" + id;

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
                Log.d(TAG6, "POST response code - " + responseStatusCode);

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
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();

                inputStream.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG6, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    // 서버코드 - 그룹이름변경

    class UpdateData_groupName extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BossPage.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG7, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {



            String serverURL = "http://211.253.9.84/updategroupname.php";
            String group_id = (String) params[0];
            String name = (String) params[1];

            String postParameters = "group_id=" + group_id + "&name=" + name;

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
                Log.d(TAG7, "POST response code - " + responseStatusCode);

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
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();

                inputStream.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG7, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
}
