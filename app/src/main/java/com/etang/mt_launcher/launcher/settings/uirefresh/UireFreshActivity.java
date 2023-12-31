package com.etang.mt_launcher.launcher.settings.uirefresh;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.etang.mt_launcher.R;

/**
 * @Package: com.etang.nt_launcher.launcher.settings.uirefresh
 * @ClassName: UireFreshActivity
 * @Description: “界面刷新”设置
 * @CreateDate: 2021/3/19 8:16
 * @UpdateDate: 2021/5/29 21:52
 */
public class UireFreshActivity extends AppCompatActivity {
    //整数，用于计数
    int number = 0;
    View uirefresh_id;
    //当前页面TAG
    private static String TAG = "UireFreshActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.activity_uirefresh);
        //绑定控件
        uirefresh_id = (View) findViewById(R.id.uirefresh_id);
        //启动背景切换线程
        handler.post(timeRunnable);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 % 2 == 0) {
                uirefresh_id.setBackgroundColor(Color.BLACK);
            } else {
                uirefresh_id.setBackgroundColor(Color.WHITE);
            }
            handler.postDelayed(timeRunnable, 300);
        }
    };
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            number++;
            Message message = handler.obtainMessage();
            message.arg1 = number;
            if (number < 6) {
                handler.sendMessage(message);
            } else {
                handler.removeCallbacks(timeRunnable);
                finish();
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
