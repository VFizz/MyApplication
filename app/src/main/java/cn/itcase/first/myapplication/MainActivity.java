package cn.itcase.first.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cn.itcase.first.myapplication.bao.Person;
import cn.itcase.first.myapplication.bao.PersonDao2;

public class MainActivity extends Activity {

    private ListView lv;
    private List<Person> persons;
    //创建一个Handler对象用于线程间通信
    private Handler handler = new Handler(){
        @Override
        public void close() {

        }

        @Override
        public void flush() {

        }

        @Override
        public void publish(LogRecord record) {

        }

        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                //接收到数据查询完毕的消息
                case 100:
                    //UI线程适配ListView
                    lv.setAdapter(new MyAdapter());
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        //由于添加数据、查询数据是比较耗时的，因此需要在子线程中做这两个操作
        new Thread(){
            public void run(){
                //添加数据
                addData();
                //获取persons集合
                getPersons();
                //如果查询到数据 则向UI线程发送消息
                if(persons.size()>0){
//                    handler.handleMessage(100);
                }

            };
        }.start();
    }
    //往person表中插入10条数据
    public void addData(){
        PersonDao2 dao = new PersonDao2(this);
        long number=885900001;
        Random random = new Random();
        for(int i=0;i<10;i++){
            dao.add("wangwu"+i,Long.toString(number+i),random.nextInt(5000));
        }
    }
    //利用ContentResolver对象查询本应用程序使用ContentProvider暴露的数据
    private void getPersons(){
        //首先要获取查询的uri
        String path = "content://cn.itcase.first.myapplication.bao";
        Uri uri = Uri.parse(path);
        //获取ContentResolver对象，这个对象的使用后面会详细讲解
        ContentResolver contentResolver = getContentResolver();
        //利用ContentReoslver 对象查询数据得到一个Cursor对象
        Cursor cursor = contentResolver.query(uri,null,null,null,null);
        persons = new ArrayList<Person>();
        //如果cursor为空立即结束该方法
        if(cursor == null){
            return;
        }
        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            Person p = new Person(id,name,number);
            persons.add(p);
        }
        cursor.close();
    }
    //适配器
    private class MyAdapter extends BaseAdapter{

        private static  final String TAG="MyAdapter";
        //控制listView里面总共有多个条目
        @Override
        public int getCount() {
            return persons.size();          //条目个数==集合的size
        }

        @Override
        public Object getItem(int position) {
            return persons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //得到某个位置对应的person对象
            Person person = persons.get(position);
            View view =View.inflate(MainActivity.this,R.layout.list_item,null);
            //一定要在view对象里面寻找孩子的id，姓名
            TextView tv_name=(TextView) view.findViewById(R.id.tv_name);
            tv_name.setText("姓名："+person.getName());
            TextView tv_phone=(TextView) view.findViewById(R.id.tv_phone);
            tv_phone.setText("姓名："+person.getNumber());
            return view;
        }
    }
}
