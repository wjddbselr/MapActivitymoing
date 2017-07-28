package app.gotogether.com.mapactivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BossPage extends AppCompatActivity {

    static final int REQUEST_TAKE_ALBUM = 2002;
    static final int REQUEST_IMAGE_CROP = 2003;
    String _input;
    String mCurrentPhotoPath;
    Uri photoURI, albumURI;

    boolean isAlbum = false;

 ///   EditText et = null;

    ArrayAdapter addressAdapter;
    //ArrayList<MyData> addressList;

    // 그리드뷰 달력
    /**
     * 월별 캘린더 뷰 객체
     */
    GridView monthView;

    /**
     * 월별 캘린더 어댑터
     */
    MonthAdapter monthViewAdapter;

    /**
     * 월을 표시하는 텍스트뷰
     */
    TextView monthText;

    /**
     * 현재 연도
     */
    int curYear;

    /**
     * 현재 월
     */
    int curMonth;
    int curDay;

    String content;

    String me;
    String str;
    int id = 0; // 회원 고유아이디 (가정)

    EditText edit;

    private int startYear, startMonth, startDay;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    ArrayList<String> as;
    ArrayList<DayData> dayData;
    ArrayList <participant> part;
    ListView lv;
    ListView information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_boss_page);

        // 월별 캘린더 뷰 객체 참조
        // 어댑터 생성
        monthView = (GridView) findViewById(R.id.monthView);
        monthViewAdapter = new MonthAdapter(this); // 어댑터
        monthView.setAdapter(monthViewAdapter);


        // 달력 누르면 아래에 일정 뜨게

        dayData = new ArrayList<DayData>();

        //ArrayList<String> ts = new ArrayList<String>();
        //ts.add("1");
        //ts.add("2");

        lv = (ListView)findViewById(R.id.listView);


        // 리스너 설정
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 현재 선택한 일자 정보 표시
                MonthItem curItem = (MonthItem) monthViewAdapter.getItem(position);
                curDay = curItem.getDay();
                as = new ArrayList<String>();
                for(int i =0; i< dayData.size();i++){
                    if(dayData.get(i).getDay() ==curDay){
                        as.add(dayData.get(i).getScadule()); // 스케쥴 가져오기
                    }

                }

                // 리스트뷰 클릭시 참석 여부 질문

                updateLv();

            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                // 이미 참석했을 경우, 아직 참석하지 않은 경우로 나누어야함 - 일일이 비교
                // 참석한 경우는 롱클릭시 참석 취소
                // 각각의 스케쥴 마다

                AlertDialog.Builder builder = new AlertDialog.Builder(BossPage.this);
                builder.setTitle("참석여부");
                builder.setMessage("참석하시겠습니까?");


                me = "최예슬"; // 현재 접속자로 가정

                part = new ArrayList<participant>(); // 예비 리스트

                builder.setPositiveButton("네", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 현재 접속하고 있는 사람의 이름을 리스트에 넣어준다

                        part.add(new participant(2017, 7, 25, me)); // 참석자 목록에 나를 넣는다

                        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                        //View customView = inflater.inflate(R.layout.activity_information, null);

                        // 다이얼로그에 리스트뷰 나타내기
                        information = (ListView)findViewById(R.id.listView2);
                        adapter2 = new ArrayAdapter(BossPage.this, R.layout.activity_information, part);
                        information.setAdapter(adapter2);


                    }});
                builder.setNegativeButton("아니요", null);


                builder.create().show();


                return true;
            }

        });

        // 리스트 클릭시 참석자 정보 및 자세한 정보 나오도록
        // 나중에 디자인 고려시 커스텀 리스트뷰로 구현
        // 상세정보는 추후 시간 기능까지 추가 후 구현
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                View customView = inflater.inflate(R.layout.activity_information, null);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(BossPage.this);
                builder.setView(customView); // Set the view of the dialog to your custom layout
                builder.setTitle("상세정보");
                builder.setPositiveButton("확인", null);

                builder.create().show();
            }
        });

        // 넘어가는 월 처리

        monthText = (TextView) findViewById(R.id.monthText);
        setMonthText();

        // 이전 월로 넘어가는 이벤트 처리
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

    /**
     * 월 표시 텍스트 설정
     */
    private void setMonthText() {
        curYear = monthViewAdapter.getCurYear();
        curMonth = monthViewAdapter.getCurMonth();

        monthText.setText(curYear + "년 " + (curMonth + 1) + "월");
    }

    public void onClick(View v){
        final EditText et ;
        switch(v.getId()){
            // 일정 추가 코드
            case R.id.plus :

                // Inflate your custom layout containing 2 DatePickers
                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                View customView = inflater.inflate(R.layout.activity_datetimepicker, null);

                // Define your date pickers
                final DatePicker dpStartDate = (DatePicker) customView.findViewById(R.id.dpStartDate);

                // Build the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(customView); // Set the view of the dialog to your custom layout
                builder.setTitle("일정추가");

                //View view = (View) getLayoutInflater().inflate(R.layout.activity_datetimepicker, null);
               // edit = (EditText)findViewById(R.id.editText2);

                LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
                LayoutInflater layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layoutInflater.inflate(R.layout.activity_datetimepicker,linearLayout,true);



                builder.setPositiveButton("추가", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        startYear = dpStartDate.getYear();
                        startMonth = dpStartDate.getMonth() + 1;
                        startDay = dpStartDate.getDayOfMonth();

                        //String year = Integer.valueOf(startYear).toString();
                        //String month = Integer.valueOf(startMonth).toString();
                        //String day = Integer.valueOf(startDay).toString();

                        // 나중에 수정
                        //TextView text = (TextView) findViewById(R.id.title);
                      // content = edit.getText().toString(); // 일정내용

                 //일단지움       dayData = new ArrayList<DayData>();
                        // 우선은 일정 하나만 추가 가능하도록
                        // editText 수정
                        edit = (EditText)findViewById(R.id.editText2);

                        _input = edit.getText().toString();
                        for(int i = 0; i <= _input.length(); i++){
                            dayData.add(new DayData(startYear, startMonth, startDay,_input.substring(i)));
                        }
                       // dayData.add(new DayData(startYear, startMonth, startDay, str));
                        // 후에 추가하면 동그라미 버튼 나타나게 or 색이 바뀌게

                  //      Toast.makeText(BossPage.this, dayData.get(dayData.size() - 1).getString() , Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }});
                builder.setNegativeButton("취소", null);

                // Create and show the dialog
                builder.create().show();

                break;

            //case R.id.btnTest1 :
            //captureCamera();
            //break;
            // 이름 변경 코드
            case R.id.btnSet :
                getAlbum();
                break;
            case R.id.btnTitle :

                AlertDialog.Builder ad = new AlertDialog.Builder(BossPage.this);

                ad.setTitle("이름변경");       // 제목 설정
                ad.setMessage("변경할 이름을 입력해주세요");   // 내용 설정

                et = new EditText(this);
                ad.setView(et);


                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TextView text = (TextView) findViewById(R.id.title);
                        String title = et.getText().toString();

                        text.setText(title);

                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }


                });

                ad.show();

                break;

            case R.id.member :

                AlertDialog.Builder member = new AlertDialog.Builder(BossPage.this);

                member.setTitle("팀원관리");       // 제목 설정
                member.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }

                });
                member.setAdapter(addressAdapter, null);

                member.show();

                break;

        }
    }



    // 카메라코드

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/pathvalue/"+ imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = storageDir.getAbsolutePath();
        Log.i("mCurrentPhotoPath", mCurrentPhotoPath);
        return storageDir;
    }

    public void getAlbum(){
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }

    public void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(photoURI, "image/*");
        //cropIntent.putExtra("outputX", 200); // crop한 이미지의 x축 크기
        //cropIntent.putExtra("outputY", 200); // crop한 이미지의 y축 크기
        //cropIntent.putExtra("aspectX", 1); // crop 박스의 x축 비율
        //cropIntent.putExtra("aspectY", 1); // crop 박스의 y축 비율
        cropIntent.putExtra("scale", true);

        if(isAlbum == false) {
            cropIntent.putExtra("output", photoURI); // 크랍된 이미지를 해당 경로에 저장
        } else if(isAlbum == true){
            cropIntent.putExtra("output", albumURI); // 크랍된 이미지를 해당 경로에 저장
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

    // 갤러리 새로고침, ACTION_MEDIA_MOUNTED는 하나의 폴더, FILE은 하나의 파일을 새로 고침할 때 사용함

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("onActivityResult", "CALL");
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //case REQUEST_TAKE_PHOTO:
            //isAlbum = false;
            //cropImage();
            // break;

            case REQUEST_TAKE_ALBUM:
                isAlbum = true;
                File albumFile = null;
                try {
                    albumFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (albumFile != null) {
                    albumURI = Uri.fromFile(albumFile);
                }
                photoURI = data.getData();
                cropImage();
                break;

            case REQUEST_IMAGE_CROP:
                galleryAddPic();
                // 업로드
                //uploadFile(mCurrentPhotoPath);
                break;
        }
    }

    // 리스트뷰 업데이트
    public void updateLv(){
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,as);
        lv.setAdapter(adapter);
    }


}

