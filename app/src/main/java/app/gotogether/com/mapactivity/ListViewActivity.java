package app.gotogether.com.mapactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
/**
 * Created by so yeon on 2017-08-02.
 */

public class ListViewActivity extends AppCompatActivity {
    private ArrayList<MyData> myDataList;
    private MyAdapter myAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        myDataList = new ArrayList<MyData>();

        myAdapter = new MyAdapter(this, R.layout.activity_listview, myDataList);
        // 첫번째 자리 context, 두번째 자리 뷰, 세번째 자리 원본데이터
        // 생성자 참고

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(myAdapter);
    }
}

