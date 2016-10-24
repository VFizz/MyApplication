package cn.itcase.first.myapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    protected static final int CHANGE_UI = 1;
    protected static final int ERROR = 2;
    private EditText et_path;
    private ImageView iv;
    //主线程创建消息处理器
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == CHANGE_UI) {
                Bitmap bitmap = (Bitmap) msg.obj;
                iv.setImageBitmap(bitmap);
            } else if (msg.what == ERROR) {
                Toast.makeText(MainActivity.this, "显示图片错误", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_path = (EditText) findViewById(R.id.et_path);
        iv = (ImageView) findViewById(R.id.lv);
    }

    public void click(View view) {
        final String path = et_path.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "图片路径不能为空", Toast.LENGTH_LONG).show();
        } else {
            //子线程请求网络，Android 4.0以后访问网络不能放在主线程中
            new Thread() {
                private HttpURLConnection conn;
                private Bitmap bitmap;

                @Override
                public void run() {
                    //连接服务器get请求，获取图片
                    try {
                        //创建url对象
                        URL url = new URL(path);
                        //根据url发送http请求
                        conn = (HttpURLConnection) url.openConnection();
                        //设置请求模式
                        conn.setRequestMethod("GET");
                        //设置请求的方式
                        conn.setConnectTimeout(5000);
                        //设置请求头User-Agent浏览器的版本
//                        conn.setRequestProperty();
                        //得到服务器返回码是200
                        int code = conn.getResponseCode();
                        //请求网络成功后返回码是200
                        if(code==200)
                        {
                            //获取输入流
                            InputStream is = conn.getInputStream();
                            //将流对象转换成Bitmap对象
                            bitmap = BitmapFactory.decodeStream(is);
                            //告诉主线程一个消息：帮我更改界面，内容：bitmap
                            Message msg = new Message();
                            msg.what = CHANGE_UI;
                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Message msg = new Message();
                        msg.what = ERROR;
                        handler.sendMessage(msg);
                    }

                }
            }.start();
        }
    }
}
