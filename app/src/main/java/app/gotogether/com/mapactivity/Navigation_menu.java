package app.gotogether.com.mapactivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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
import java.util.HashMap;

public class Navigation_menu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //set variables to get the server
    private static String TAG = "grouplist_json";
    private static String TAG1 = "add";
    //db랑 php랑 맞춘거라 바꾸면 안돼요~~
    private static final String TAG_JSON = "grouplist_json";
    private static final String TAG_GroupID = "id";
    private static final String TAG_GroupName = "name";

    private static String The_Group_Name = null;

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    String mJsonString;

    ArrayList<String> group_id_list = new ArrayList<String>();
    ArrayList<String> group_name_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_menu);


        // textView_main_result

        GetData task = new GetData();
        task.execute("http://211.253.9.84/getgrouplist.php");

        /* 플로팅 아이콘 구현
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                Navigation_menu.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //기본으로 띄워줄 화면
        displaySelectedScreen(R.id.login);
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Navigation_menu.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "0) onPostExecute/ response  - " + result);

            if (result == null) {
                Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
                TextView v = (TextView) findViewById(R.id.textView_frame);
                v.setText(errorString.toString());

            } else {

                mJsonString = result;
                try {
                    showResult();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(2000);
                httpURLConnection.setConnectTimeout(2000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "1) doInBackground / response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                inputStreamReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }

        private void showResult() throws InterruptedException {
            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject item = jsonArray.getJSONObject(i);
                    String group_id = item.getString(TAG_GroupID);
                    String group_name = item.getString(TAG_GroupName);

                    group_id_list.add(group_id);
                    group_name_list.add(group_name);

                    Log.d(TAG, "2) showResult / group_id_list - " + group_id_list.get(i));
                    Log.d(TAG, "2) showResult / group_name_list - " + group_name_list.get(i));

                }
                //네비게이션 메뉴 불러옴 (메뉴리스트 서버에서 불러온 후에..)
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(Navigation_menu.this);
                //메뉴추가
                addMenuItemInNavMenuDrawer();

            } catch (JSONException e) {

                Log.d(TAG, "2) showResult error: ", e);
            }

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /* 먼저 구현된 상단 우측 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    /* 직접 생성한 우측 메뉴 */
   /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item;
        if (group_name_list.size() == 0) {
            TextView v = (TextView) findViewById(R.id.textView1_frame);
            v.setText("안받아져왔어요");
        }

        for (int i = 0; i < group_name_list.size(); i++) {
            item = menu.add(i, i + 1, i, group_name_list.get(i));
            //item = menu.add(0,1,0,"메뉴항목1");
            item.setIcon(R.mipmap.ic_launcher);
        }

        return true;

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        for (int i = 0; i < group_name_list.size(); i++) {
            if (item.getItemId() == i) {
                //직접 코드 추가했을때는,즉menu.add(0,1,0,"메뉴항목1");일때는 두번쨰값이 아이디이므로 case 1:하면 된다.
                Toast.makeText(getApplicationContext(), "입력하신 그룹 이름은 " + group_name_list.get(i), Toast.LENGTH_LONG).show();
                break;
            }

        }
        return true;
    }
*/
    //그룹들
    private void addMenuItemInNavMenuDrawer() {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        Menu submenu = menu.addSubMenu("Group Menu");
        String a = "처음 널값";
        for (int i = 0; i < group_name_list.size(); i++) {
            a = group_name_list.get(i);
            submenu.add(i, i + 1, i, a);
            //item = menu.add(0,1,0,"메뉴항목1");
        }
        /*추가된 서브메뉴 클릭*/

        for(int i = 0; i < submenu.size(); i++){
            submenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    /*이곳에 intent삽입*/
                    Toast.makeText(getApplicationContext(), "getItem(i)  + item.getTitle() " + item.getTitle(), Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }
        navView.invalidate();

        //   public void add_for_new(String new_name)
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        //can it be replace the static ? can it work?
        Fragment fragment = null;




        //initializing the fragment object which is selected
        //기본 설정메뉴
        switch (itemId) {
            case R.id.navi_home_page:


                break;

            case R.id.navi_group_add_page:
                //그룹추가 다이얼로그로
                final EditText edittext = new EditText(this);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("그룹추가");
                builder.setMessage("그룹이름을 입력하세요\n 이후 그룹 관리 페이지로 이동합니다.\n");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "입력하신 그룹 이름은 " + edittext.getText().toString(), Toast.LENGTH_LONG).show();

                                Navigation_menu.InsertData task = new Navigation_menu.InsertData();
                                task.execute(edittext.getText().toString());

                                /*입력후 이에 해당하는 그룹페이지로 자동이동하게 만들어야함
                                Intent navi_boss_page = new Intent(Navigation_menu.this, BossPage.class);
                                Navigation_menu.this.startActivity(navi_boss_page);
                                */
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "아니오를 선택했습니다.", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();

                break;

            case R.id.navi_boss_page:
                Intent navi_boss_page = new Intent(Navigation_menu.this, BossPage.class);
                startActivity(navi_boss_page);

                break;
            /*
            case R.id.navi_m4:
                fragment = new Menu3();
                break;
            */
        }


        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_navi, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }

////insert 시작

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {

            String group_name = (String) params[0];

            String serverURL = "http://211.253.9.84/insertgroup.php";
            String postParameters = "group_name=" + group_name;

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
                inputStreamReader.close();

                return sb.toString();
            } catch (Exception e) {
                return new String("Error: " + e.getMessage());
            }

        }

    }


}