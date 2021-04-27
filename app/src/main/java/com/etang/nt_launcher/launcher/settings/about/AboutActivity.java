package com.etang.nt_launcher.launcher.settings.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.etang.nt_launcher.R;
import com.etang.nt_launcher.tool.dialog.CheckUpdateDialog;
import com.etang.nt_launcher.tool.dialog.PayMeDialog;
import com.etang.nt_launcher.tool.permission.SavePermission;
import com.etang.nt_launcher.tool.toast.DiyToast;
import com.etang.nt_launcher.tool.util.MTCore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * @Package: com.etang.nt_launcher.launcher.settings.about
 * @ClassName: AboutActivity
 * @Description: “关于”界面
 * @CreateDate: 2021/3/16 8:51
 * @UpdateDate: 2021/4/04 01:50
 */
public class AboutActivity extends AppCompatActivity {
    private ImageView iv_about_logo;//关于 LOGO
    //文本，分别是文本_返回，文本_标题，文本_按钮，文本_关于APP版本，文本_关于捐赠我
    private TextView tv_back, tv_title, tv_button, tv_about_appversion, tv_about_juanzeng;
    //返回LinearLayout
    private LinearLayout lv_back;
    //检查更新按钮
    private Button btn_about_checkup_button;
    //当前TAG
    private static String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置填充屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.setting_about);
        initView();
        //标题
        tv_title.setText(getString(R.string.string_about));
        tv_button.setText(getString(R.string.string_version));
        //APP关于 文本
        tv_about_appversion.setText(MTCore.get_my_appVERSIONNAME());
        //按钮 文本
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(AboutActivity.this)
                        .setTitle("部分图片来自：iconfont.cn")
                        .setMessage("图标（launcher icon）：小白熊_猫草君 | \"糖果\"icon")
                        .setNegativeButton("关闭", null).show();
            }
        });
        //返回 文本 点击事件
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //返回 线性布局 点击事件
        lv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //关于logo 图片 点击事件
        iv_about_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查存储权限
                SavePermission.check_save_permission(AboutActivity.this);
                //检查更新
                CheckUpdateDialog.check_update(AboutActivity.this, AboutActivity.this, "about");
            }
        });
        //关于检查更新 按钮 点击事件
        btn_about_checkup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查存储权限
                SavePermission.check_save_permission(AboutActivity.this);
                //检查更新
                CheckUpdateDialog.check_update(AboutActivity.this, AboutActivity.this, "about");
            }
        });
        //关于捐赠 文本 点击事件
        tv_about_juanzeng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast提示
                DiyToast.showToast(getApplicationContext(), "请注意：捐赠并不影响正常使用", true);
                //显示弹出框
                PayMeDialog.show_dialog(AboutActivity.this);
            }
        });
    }

    /**
     * 绑定控件
     */
    private void initView() {
        lv_back = (LinearLayout) findViewById(R.id.lv_back);
        iv_about_logo = (ImageView) findViewById(R.id.iv_about_logo);
        tv_back = (TextView) findViewById(R.id.tv_title_back);
        tv_button = (TextView) findViewById(R.id.tv_title_button);
        tv_title = (TextView) findViewById(R.id.tv_title_text);
        btn_about_checkup_button = (Button) findViewById(R.id.btn_about_checkup_button);
        tv_about_appversion = (TextView) findViewById(R.id.tv_about_appversion);
        tv_about_juanzeng = (TextView) findViewById(R.id.tv_about_juanzeng);
    }

    /**
     * 设置当前Activity结束时，无动画
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
