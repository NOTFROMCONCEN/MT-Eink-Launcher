package com.etang.mt_launcher.launcher.settings.launcherimage;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.etang.mt_launcher.R;
import com.etang.mt_launcher.launcher.MainActivity;
import com.etang.mt_launcher.tool.mtcore.MTCore;
import com.etang.mt_launcher.tool.mtcore.permission.SavePermission;

import java.io.IOException;

/**
 * @Package: com.etang.nt_launcher.launcher.settings.launcherimage
 * @ClassName: ChoseImagesActivity
 * @Description: “壁纸设置”界面
 * @CreateDate: 2021/3/19 8:15
 * @UpdateDate: 2021/5/29 21:52
 */
public class ChoseImagesActivity extends AppCompatActivity {
    //单选按钮，选择壁纸，妹子、情侣、APP列表、萝莉、治愈、显示壁纸、显示壁纸和APP列表
    private RadioButton ra_meizi, ra_qinglv, ra_applist, ra_luoli, ra_zhiyu, ra_wallpaper, ra_wallpaper_and_applist;
    //设置系统壁纸
    private Button btn_set_wallpaperimage;
    //说实话忘了这个有什么用了
    private static final int IMAGE_PICK = 2654;
    //bitmap，用于存储壁纸
    Bitmap bitmap = null;
    //文本，返回，按钮，标题
    private TextView tv_button, tv_title;
    //返回LinearLayout
    private LinearLayout lv_back;
    //当前TAG
    private static String TAG = "ChoseImagesActivity";
    //判断返回到的Activity
    private static final int IMAGE_REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.setting_chose_images);
        initView();//绑定控件
        check_image();
        //APP列表
        ra_applist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "applist");
                editor.apply();
                MainActivity.tg_apps_state.setChecked(true);
                MainActivity.iv_index_back.setVisibility(View.INVISIBLE);
                MainActivity.mListView.setVisibility(View.VISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：应用列表", true);
            }
        });
        //妹子
        ra_meizi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "mz");
                editor.apply();
                MainActivity.iv_index_back.setImageResource(R.drawable.mi_meizi);
                MainActivity.tg_apps_state.setChecked(false);
                MainActivity.iv_index_back.setVisibility(View.VISIBLE);
                MainActivity.mListView.setVisibility(View.INVISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：妹子", true);
            }
        });
        //情侣
        ra_qinglv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "ql");
                editor.apply();
                MainActivity.iv_index_back.setImageResource(R.drawable.mi_haole);
                MainActivity.tg_apps_state.setChecked(false);
                MainActivity.iv_index_back.setVisibility(View.VISIBLE);
                MainActivity.mListView.setVisibility(View.INVISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：情侣", true);
            }
        });
        //萝莉
        ra_luoli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "ll");
                editor.apply();
                MainActivity.iv_index_back.setImageResource(R.drawable.mi_luoli);
                MainActivity.tg_apps_state.setChecked(false);
                MainActivity.iv_index_back.setVisibility(View.VISIBLE);
                MainActivity.mListView.setVisibility(View.INVISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：萝莉", true);
            }
        });
        //知遇
        ra_zhiyu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "zy");
                editor.apply();
                MainActivity.iv_index_back.setImageResource(R.drawable.mi_zhiyu);
                MainActivity.tg_apps_state.setChecked(false);
                MainActivity.iv_index_back.setVisibility(View.VISIBLE);
                MainActivity.mListView.setVisibility(View.INVISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：知遇", true);
            }
        });
        //系统壁纸
        ra_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "app_wallpaper");
                editor.apply();
                //
//                SavePermission.check_save_permission(ChoseImagesActivity.this, ChoseImagesActivity.this);//检查存取权限
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                MainActivity.iv_index_back.setImageResource(R.drawable.mi_yali);
                MainActivity.tg_apps_state.setChecked(false);
                MainActivity.iv_index_back.setVisibility(View.VISIBLE);
                MainActivity.mListView.setVisibility(View.INVISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：系统壁纸", true);
                MainActivity.initSkinMode(ChoseImagesActivity.this, "app_wallpaper");
            }
        });
        //系统壁纸+应用列表
        ra_wallpaper_and_applist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString("images_info", "app_wallpaper_applist");
                editor.apply();
//                //
//                SavePermission.check_save_permission(ChoseImagesActivity.this, ChoseImagesActivity.this);//检查存取权限
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, IMAGE_PICK);
//                MainActivity.iv_index_back.setImageResource(R.drawable.mi_yali);
                MainActivity.tg_apps_state.setVisibility(View.GONE);
                MainActivity.iv_index_back.setVisibility(View.VISIBLE);
                MainActivity.mListView.setVisibility(View.VISIBLE);
                MTCore.showToast(ChoseImagesActivity.this, "已更换：系统壁纸和应用列表", true);
                MainActivity.initSkinMode(ChoseImagesActivity.this, "app_wallpaper_applist");
            }
        });
        tv_title.setText("壁纸设置");
        tv_button.setText("预览壁纸");
        lv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTCore.check_save_permission(ChoseImagesActivity.this);//检查存取权限
                show_dialog();
            }
        });
        btn_set_wallpaperimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MTCore.check_save_permission(ChoseImagesActivity.this);//检查存取权限
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, IMAGE_PICK);
//                Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
//                startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE);
                if (ContextCompat.checkSelfPermission(ChoseImagesActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChoseImagesActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                }
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICK);
            }
        });
    }

    private void show_dialog() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        String images_mode = sharedPreferences.getString("images_info", null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ChoseImagesActivity.this);
        View view = LayoutInflater.from(ChoseImagesActivity.this).inflate(R.layout.dialog_chose_image, null);
        builder.setView(view);
        ImageView iv_see_image = (ImageView) view
                .findViewById(R.id.iv_see_image);
//        if (images_mode.equals("ql")) {
//            iv_see_image.setImageResource(R.drawable.mi_haole);
//        }
//        if (images_mode.equals("mz")) {
//            iv_see_image.setImageResource(R.drawable.mi_meizi);
//        }
//        if (images_mode.equals("ll")) {
//            iv_see_image.setImageResource(R.drawable.mi_luoli);
//        }
//        if (images_mode.equals("zy")) {
//            iv_see_image.setImageResource(R.drawable.mi_zhiyu);
//        }
//        if (images_mode.equals("applist")) {
//            iv_see_image.setImageResource(R.drawable.ic_setting);
//        }
//        if (images_mode.equals("")) {
//            MTCore.showToast(ChoseImagesActivity.this, "请选择壁纸或者应用列表", true);
//        }
//        if (images_mode.equals("app_wallpaper")) {
//            iv_see_image.setImageBitmap(bitmap);
//        }
//        if (images_mode.equals("app_wallpaper_applist")) {
//            iv_see_image.setImageBitmap(bitmap);
//        }
        switch (images_mode) {
            case "ql":
                iv_see_image.setImageResource(R.drawable.mi_haole);
                break;
            case "mz":
                iv_see_image.setImageResource(R.drawable.mi_meizi);
                break;
            case "ll":
                iv_see_image.setImageResource(R.drawable.mi_luoli);
                break;
            case "zy":
                iv_see_image.setImageResource(R.drawable.mi_zhiyu);
                break;
            case "applist":
                iv_see_image.setImageResource(R.drawable.ic_setting);
                break;
            case "":
                MTCore.showToast(ChoseImagesActivity.this, "请选择壁纸或者应用列表", true);
                break;
            case "app_wallpaper":
                iv_see_image.setImageBitmap(bitmap);
                break;
            case "app_wallpaper_applist":
                iv_see_image.setImageBitmap(bitmap);
                break;
        }
        builder.setTitle("图片预览：" + images_mode);
        builder.setPositiveButton("关闭", null);
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                if (data != null) {
                    // 得到图片的全路径
                    Uri uri = data.getData();
                    System.out.println("壁纸URI：" + uri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        //                            setWallpaper(bitmap);
//                        MainActivity.iv_index_back.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        MTCore.ErrorDialog(ChoseImagesActivity.this, "获取图片时出现错误：" + e.toString(), TAG);
                    }
                }
                MTCore.showToast(getApplicationContext(), "选择成功，可点击右上角进行预览。\n路径：", true);
            } else {
                MTCore.showToast(getApplicationContext(), "你并没有选择什么", true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void check_image() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        String images_mode = sharedPreferences.getString("images_info", null);
        switch (images_mode) {
            case "ql":
                ra_qinglv.setChecked(true);
                break;
            case "mz":
                ra_meizi.setChecked(true);
                break;
            case "ll":
                ra_luoli.setChecked(true);
                break;
            case "zy":
                ra_zhiyu.setChecked(true);
                break;
            case "applist":
                ra_applist.setChecked(true);
                break;
            case "":
                MTCore.showToast(this, "请选择壁纸或者应用列表", true);
                break;
            case "app_wallpaper":
                ra_wallpaper.setChecked(true);
                break;
            case "app_wallpaper_applist":
                ra_wallpaper_and_applist.setChecked(true);
                break;
        }
//
//        if (images_mode.equals("ql")) {
//            ra_qinglv.setChecked(true);
//        }
//        if (images_mode.equals("mz")) {
//            ra_meizi.setChecked(true);
//        }
//        if (images_mode.equals("ll")) {
//            ra_luoli.setChecked(true);
//        }
//        if (images_mode.equals("zy")) {
//            ra_zhiyu.setChecked(true);
//        }
//        if (images_mode.equals("applist")) {
//            ra_applist.setChecked(true);
//        }
//        if (images_mode.equals("")) {
//            MTCore.showToast(this, "请选择壁纸或者应用列表", true);
//        }
//        if (images_mode.equals("app_wallpaper")) {
//            ra_wallpaper.setChecked(true);
//        }
//        if (images_mode.equals("app_wallpaper_applist")) {
//            ra_wallpaper_and_applist.setChecked(true);
//        }
    }

    /**
     * 绑定控件
     */
    private void initView() {
        lv_back = (LinearLayout) findViewById(R.id.lv_back);
        tv_button = (TextView) findViewById(R.id.tv_title_button);
        tv_title = (TextView) findViewById(R.id.tv_title_text);
        btn_set_wallpaperimage = (Button) findViewById(R.id.btn_set_wallpaperimage);
        ra_applist = (RadioButton) findViewById(R.id.ra_chose_applist_info);
        ra_meizi = (RadioButton) findViewById(R.id.ra_chose_meizi);
        ra_qinglv = (RadioButton) findViewById(R.id.ra_chose_qinglv);
        ra_luoli = (RadioButton) findViewById(R.id.ra_chose_luoli);
        ra_zhiyu = (RadioButton) findViewById(R.id.ra_chose_zhiyu);
        ra_wallpaper_and_applist = (RadioButton) findViewById(R.id.ra_wallpaper_and_applist);
        ra_wallpaper = (RadioButton) findViewById(R.id.ra_wallpaper);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
