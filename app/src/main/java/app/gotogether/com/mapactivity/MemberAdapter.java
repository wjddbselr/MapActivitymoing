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

public class MemberAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MemberData> mDataList;
    private LayoutInflater layoutInflater;
    private String result_id = "";
    private String group_name = "";

    //서버
    private static String TAG = "grouplist_json";

    public MemberAdapter(Context context, int layout, ArrayList<MemberData> mDataList) {
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

        TextView phone = (TextView) view.findViewById(R.id.phone);
        TextView name = (TextView) view.findViewById(R.id.name);
        final ImageButton btnCheck = (ImageButton) view.findViewById(R.id.btn1);
        //숫자는 아이디를 찾기 때문에 문자열로 변환해주어야 한다.
        name.setText(mDataList.get(position).getName());
        phone.setText(mDataList.get(position).getPhone());

        final String tel = mDataList.get(position).getPhone();

        // 거절 버튼 클릭
        btnCheck.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View view) {

                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:" + tel));

                context.startActivity(call);


            }
        });


        return view;
    }




}
