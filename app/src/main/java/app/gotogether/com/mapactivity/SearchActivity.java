package app.gotogether.com.mapactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends Activity {
    ListView lv;
    SearchView sv;
    ArrayAdapter<String> adapter;
    private static String TAG = "grouplist_json";

    //db랑 php랑 맞춘거라 바꾸면 안돼요~~
    private static final String TAG_JSON = "grouplist_json";
    private static final String TAG_GroupID = "id";
    private static final String TAG_GroupName = "name";

    private TextView mTextViewResult;
    private GroupAdapter GroupAdapter;
    private ArrayList<GroupData> GroupDataList;
    ArrayList<HashMap<String, String>> mArrayList;

    ListView mlistView;
    String mJsonString;
    ArrayList<String> group_name_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sv = (SearchView) findViewById(R.id.searchView1);


        GetData2 task = new GetData2();
        task.execute("http://211.253.9.84/showgrouplist.php");

        final LinearLayout group_view = (LinearLayout) View.inflate(this, R.layout.activity_search, null);
        ListView list = (ListView) group_view.findViewById(R.id.listView1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, group_name_list);

        list.setAdapter(adapter);
    }
//    public boolean onCreateOptionsMenu(final Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.actions,menu);
//        SearchView mSearchView = (SearchView)menu.findItem(R.id.action_search).getActionView();
//        mSearchView.setQueryHint("그룹이름을 입력하세요");
//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
//        {
//            @Override public boolean onQueryTextSubmit(String query){return false; }
//            @Override public boolean onQueryTextChange(String newText){return false; }
//
//
//        });
//        return true;
//    }
    private class GetData2 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SearchActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null) {

                mTextViewResult.setText(errorString);
            } else {

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
                Log.d(TAG, "response code - " + responseStatusCode);

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


                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


    private void showResult_team() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String group_id = item.getString(TAG_GroupID);
                String group_name = item.getString(TAG_GroupName);
                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_GroupID, group_id);
                hashMap.put(TAG_GroupName, group_name);

                mArrayList.add(hashMap);

            }

            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }



}

