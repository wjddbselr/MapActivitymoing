package app.gotogether.com.mapactivity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



/**
 * Created by 동덕 on 2017-05-11.
 */

public class okMyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<OkData> myDataList;
    private LayoutInflater layoutInflater;
    private String result_id = "";
    private String group_id = "";

    //서버
    private static String TAG = "php_deltest";

    public okMyAdapter(Context context, int layout, ArrayList<OkData> myDataList) {
        this.context = context;
        this.layout = layout;
        //원본 데이터를 가지고 있다(MyDataList)
        this.myDataList = myDataList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //원본 데이터의 개수를 반환
        return myDataList.size();
    }

    @Override
    public Object getItem(int position) {
        //어떠한 위치에 있는 원본 데이터의 항목 반환(어떠한 타입도 반환 가능-object이기 때문)
        return myDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //어떠한 위치에 있는 원본 데이터의 항복의 식별자를 반환
        int r = Integer.parseInt(myDataList.get(position).get_id());
        return r;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final int pos = position;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);

        }
        //view안에서 찾기 때문에 view.findviewbyid를 해주어야 한다.
        TextView textNo = (TextView) view.findViewById(R.id.textViewNo);
        TextView textName = (TextView) view.findViewById(R.id.textViewName);
        final Button btnCheck = (Button) view.findViewById(R.id.refuse);
        //숫자는 아이디를 찾기 때문에 문자열로 변환해주어야 한다.
        textNo.setText(myDataList.get(position).get_id());
        textName.setText(myDataList.get(position).getName());

        // 거절 버튼 클릭
        btnCheck.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {

                AlertDialog.Builder boss = new AlertDialog.Builder(context);

                boss.setTitle("진심으로 거절하시겠습니까?");       // 제목 설정
                boss.setPositiveButton("YES,,~", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        result_id = myDataList.get(pos).get_id();
                        group_id = myDataList.get(pos).getGroupId();

                        Toast.makeText(context, result_id + "" + group_id, Toast.LENGTH_SHORT).show();

                        InsertData task = new InsertData();
                        task.execute(group_id, result_id);

                        btnCheck.setBackgroundColor(0);


                    }});
                boss.setNegativeButton("NO,,~", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }});


                boss.show();


            }
        });


        return view;
    }


    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String groupId = group_id;
            String id = result_id;


            String serverURL = "http://211.253.9.84/deleteadm.php";
            String postParameters = "groupId=" + groupId + "&id=" + id;


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


}