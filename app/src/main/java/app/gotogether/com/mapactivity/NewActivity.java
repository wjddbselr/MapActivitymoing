package app.gotogether.com.mapactivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by so yeon on 2017-08-16.
 */

public class NewActivity extends AppCompatActivity{

    private ArrayList<MyData> myDataList;
    private MyAdapter myAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        myDataList = new ArrayList<MyData>();

//        myDataList.add(new MyData(1,"홍길동"));
//        myDataList.add(new MyData(2,"오오야"));
//        myDataList.add(new MyData(3,"알리아"));

        myAdapter = new MyAdapter(this,R.layout.activity_new,myDataList);

        listView = (ListView)findViewById(R.id.listView);

        listView.setAdapter(myAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(NewActivity.this,myDataList.get(position).getName(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
}
