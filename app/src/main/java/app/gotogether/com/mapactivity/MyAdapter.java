package app.gotogether.com.mapactivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 동덕 on 2017-05-11.
 */

// 어댑터는 기본적으로 구현되는 BaseAdapter 를 상속받음

public class MyAdapter extends BaseAdapter {

    // 멤버변수 선언
    private Context context;
    private int layout;
    private ArrayList<MyData> myDataList; // myDate 객체를 반환
    private LayoutInflater layoutInflater;

    // 생성자 재정의
    // layout과 원본데이터는 외부에서 받는것 생성자 내부에서 인플레이터에게 전달
    // context ????
    public MyAdapter(Context context, int layout, ArrayList<MyData> myDataList){
        this.context = context; // 전 시간에 했던 어댑터 관련 생성자? 거기서 첫번째 매개변수가 하는 역할이랑 비슷한 역할
        this.layout = layout;
        this.myDataList = myDataList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // 보통 xml 타입은 자바객체로 변환해줘야함 보통 setContentView 를 이용
        //근데 우리가 하는 것은 뷰가 아닌 adapter 이므로 직접 inflater 생성 해줘야함

    }

    // 상속 받았으므로 메소드 재정의
    @Override
    public int getCount() { // 원본 데이터의 갯수 반환
        return myDataList.size(); // 멤버변수이므로 이 메소드 안에서도 사용 가능
    }

    @Override
    public Object getItem(int position) { // Object 어떤 타입이든 반환 가능
        return myDataList.get(position); // 위치에 있는 데이터 객체 하나를 반환
    }

    @Override
    public long getItemId(int position) {
        return myDataList.get(position).get_id(); // 특정한 객체의 아이디 반환
        // 객체이므로 . ~ 가능
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) { // 화면 만드는 내용 구현
        // 제일 처음에는 View 에 화면이 없으므로 null 이 들어옴 화면이 만들어지는 작업 하고
        // 원본데이터 set하고 반환

        final int pos = position;
        // 원본데이터가 3개면 getView 도 3개
        // pos에 위치가 들어옴


        // 제일 처음에 화면을 만든적이 없으므로 null 이 들어옴
        if (view == null) {
            view = layoutInflater.inflate(layout, parent, false);
        }

        // 인플레이터를 통해 만든 한 화면 한칸을 찾음 아직 값은 x
        TextView textNo = (TextView) view.findViewById(R.id.textViewNo); // view.findViewById : 뷰안에서 찾기 때문
        // 지금 만든 한칸에 해당하기 때문에 view. 을 붙여줘야 한다.
        TextView textName = (TextView) view.findViewById(R.id.textViewName);
        TextView textPhone = (TextView) view.findViewById(R.id.textViewPhone);
        Button btnCheck = (Button) view.findViewById(R.id.buttonCheck);

        // 값들을 화면 요소들에 set 해줌 화면들에 값이 적힘
        textNo.setText(Integer.valueOf(myDataList.get(position).get_id()).toString());
        textName.setText(myDataList.get(position).getName());
        textPhone.setText(myDataList.get(position).getPhone());

        btnCheck.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, myDataList.get(pos).getPhone() + "선택", Toast.LENGTH_SHORT).show();
            }
        });


        // position 데이터와 view 가 결합해서 화면에 보임 return view

        return view;

    }

}

