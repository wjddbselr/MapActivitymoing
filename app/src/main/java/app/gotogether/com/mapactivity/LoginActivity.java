package app.gotogether.com.mapactivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by seuls on 2017-07-29.
 */

public class LoginActivity extends AppCompatActivity {

    EditText et_id, et_pw;
    String sId, sPw;
    final static int SUB_ACTIVITY_CODE = 100; // 상수로 선언
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = (EditText) findViewById(R.id.et_Id);
        et_pw = (EditText) findViewById(R.id.et_Password);

    }

public void onClick(View v)
{
    switch (v.getId())
    {
        case R.id.register:
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
            break;
    }
}
    public void bt_Login(View v) {
        try{
            sId = et_id.getText().toString();
            sPw = et_pw.getText().toString();
        }catch (NullPointerException e)
        {
            Log.e("err",e.getMessage());
        }


        loginDB lDB = new loginDB();
        lDB.execute();

    }


    public class loginDB extends AsyncTask<Void, Integer, Void> {


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(LoginActivity.this);
        String data ="";



        @Override
        protected Void doInBackground(Void... unused) {


/* 인풋 파라메터값 생성 */

            String param = "id=" + sId + "&pw=" + sPw;
            Log.e("POST", param);
            try {
/* 서버연결 */
                URL url = new URL("http://211.253.9.84/applogin.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

 /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

 /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

 /* 서버에서 응답 */
                Log.e("RECV DATA", data);

                if(data.equals("0"))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                }
                else
                {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(data.equals("1"))
            {
                Log.e("RESULT","성공적으로 로그인되었습니다!");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 로그인되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LoginActivity.this, MyActivity.class);
                                intent.putExtra("sId", sId);
                                intent.putExtra("sPw", sPw);
                                startActivityForResult(intent, SUB_ACTIVITY_CODE);

                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else if(data.equals("0"))
            {
                Log.e("RESULT","비밀번호가 일치하지 않습니다.");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("비밀번호가 일치하지 않습니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + data);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        }

    }
}