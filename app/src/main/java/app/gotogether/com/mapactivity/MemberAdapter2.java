package app.gotogether.com.mapactivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by so yeon on 2017-09-18.
 */

public class MemberAdapter2 extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MemberData> mDataList;
    private LayoutInflater layoutInflater;
    private String result_id = "";
    private String group_name = "";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    //서버
    private static String TAG = "grouplist_json";

    public MemberAdapter2(Context context, int layout, ArrayList<MemberData> mDataList) {
        this.context = context;
        this.layout = layout;
        this.mDataList = mDataList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //원본 데이터의 개수를 반환
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //어떠한 위치에 있는 원본 데이터의 항복의 식별자를 반환
        int r = Integer.parseInt(mDataList.get(position).get_id());
        return r;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        final int pos = position;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);

        }

        final TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView name = (TextView) view.findViewById(R.id.name);
        final ImageButton btnCheck = (ImageButton) view.findViewById(R.id.btn1);
        //숫자는 아이디를 찾기 때문에 문자열로 변환해주어야 한다.
        name.setText(mDataList.get(position).getName());
         phone.setText(mDataList.get(position).getPhone());
        final String tel = mDataList.get(position).getPhone();

        btnCheck.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {


                // 여기에 메세지 전송 코드 넣으면돼!
                String message = "sos!";
//               if (ContextCompat.checkSelfPermission(MemberAdapter2.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
//                else {
//
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(String.valueOf(phone), null, message, null, null);
//          }

               Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + tel));
                intent.putExtra("sms_body",message);
                context.startActivity(intent);
            }
        });



        return view;
    }
}

