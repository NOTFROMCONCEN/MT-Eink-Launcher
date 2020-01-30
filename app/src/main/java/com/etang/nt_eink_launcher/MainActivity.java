package com.etang.nt_eink_launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.etang.nt_eink_launcher.adapter.DeskTopGridViewBaseAdapter;
import com.etang.nt_eink_launcher.adapter.GetApps;
import com.etang.nt_eink_launcher.mysql.MyDataBaseHelper;
import com.etang.nt_eink_launcher.toast.DiyToast;
import com.etang.nt_eink_launcher.util.AppInfo;
import com.etang.nt_eink_launcher.util.StreamTool;
import com.etang.nt_eink_launcher.view.MyCircleView;
import com.etang.nt_launcher.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @year 2019
 * @Todo TODO 桌面主页
 * @package_name com.example.dklauncherdemo
 * @project_name DKLauncherDemo
 * @file_name MainActivity.java
 * @我的博客 https://naiyouhuameitang.club/
 */
public class MainActivity extends Activity implements OnClickListener {
    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;
    private Handler handler;
    private Runnable runnable;
    private TextView tv_user_id, tv_time_hour, tv_time_min,
            tv_desk_top_time, tv_battery_show, tv_city, tv_wind, tv_temp_state,
            tv_last_updatetime;
    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;
    private ImageView iv_setting_button;
    private LinearLayout line_wather;
    private MyCircleView circleView;
    public static ToggleButton tg_apps_state;
    public static String string_app_info = "";
    public static ImageView iv_index_back;
    public static GridView mListView;
    public static List<AppInfo> appInfos = new ArrayList<AppInfo>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);//应用运行时，保持屏幕高亮，不锁屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.activity_main);
        initView();// 绑定控件
        new_time_Thread();// 启用更新时间进程
        rember_name();// 记住昵称
        initAppList(this);// 获取应用列表
        monitorBatteryState();// 监听电池信息


        /**
         * 隐藏未完成功能
         */
        circleView.setVisibility(View.GONE);
        /**
         * 判断是不是第一次使用
         */
        if (isFirstStart(MainActivity.this)) {//第一次
            /**
             * 填充预设数据
             */
            SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putString("images_info", "applist");
            editor.putString("images_app_listifo", "true");
            editor.apply();
        } else {//有过使用信息
            images_upgrade();//更新图像信息
        }
        // 长按弹出APP信息
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                string_app_info = appInfos.get(position).getPackageName();
                Intent intent = new Intent(MainActivity.this, UnInstallActivity.class);
                startActivity(intent);
                return true;
            }
        });
        // 当点击GridView时，获取ID和应用包名并启动
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Intent intent=appInfos.get(position).getIntent();
                // startActivity(intent);
                Intent intent = getPackageManager().getLaunchIntentForPackage(
                        appInfos.get(position).getPackageName());
                if (intent != null) {
                    intent.putExtra("type", "110");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        // 默认抽屉显示状态
        mListView.setVisibility(View.GONE);
        iv_index_back.setVisibility(View.VISIBLE);
        //切换应用列表
        tg_apps_state.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    mListView.setVisibility(View.VISIBLE);
                    iv_index_back.setVisibility(View.GONE);
                    initAppList(MainActivity.this);
                } else {
                    mListView.setVisibility(View.GONE);
                    iv_index_back.setVisibility(View.VISIBLE);
                    initAppList(MainActivity.this);
                }
            }
        });
        // 点击更新天气
        line_wather.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Cursor cursor = db.rawQuery("select * from wather_city", null);
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    update_wather(MainActivity.this,
                            cursor.getString(cursor.getColumnIndex("city")));
                }
            }
        });
        line_wather.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(getApplicationContext(),
                        WatherActivity.class));
                return true;
            }
        });
    }

    public boolean isFirstStart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "SHARE_APP_TAG", 0);
        Boolean isFirst = preferences.getBoolean("FIRSTStart", true);
        if (isFirst) {// 第一次
            preferences.edit().putBoolean("FIRSTStart", false).commit();
            return true;
        } else {
            return false;
        }
    }


    private void images_upgrade() {
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        String images_mode = sharedPreferences.getString("images_info", null);
        if (images_mode.equals("ql")) {
            iv_index_back.setImageResource(R.mipmap.mi_haole);
            iv_index_back.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
        if (images_mode.equals("ej")) {
            iv_index_back.setImageResource(R.mipmap.mi_erji);
            iv_index_back.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
        if (images_mode.equals("mz")) {
            iv_index_back.setImageResource(R.mipmap.mi_meizi);
            iv_index_back.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
        if (images_mode.equals("ch")) {
            iv_index_back.setImageResource(R.mipmap.mi_chahua);
            iv_index_back.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        }
        if (images_mode.equals("applist")) {
            iv_index_back.setImageResource(R.mipmap.mi_haole);
            iv_index_back.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }
        if (images_mode.equals("")) {
            iv_index_back.setImageResource(R.mipmap.mi_haole);
            iv_index_back.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
            DiyToast.showToast(this, "请选择壁纸或者应用列表（设置-壁纸设置）");
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Cursor cursor = db.rawQuery("select * from wather_city", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            update_wather(MainActivity.this,
                    cursor.getString(cursor.getColumnIndex("city")));
        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Cursor cursor = db.rawQuery("select * from wather_city", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            update_wather(MainActivity.this,
                    cursor.getString(cursor.getColumnIndex("city")));
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Cursor cursor = db.rawQuery("select * from wather_city", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            update_wather(MainActivity.this,
                    cursor.getString(cursor.getColumnIndex("city")));
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Cursor cursor = db.rawQuery("select * from wather_city", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            update_wather(MainActivity.this,
                    cursor.getString(cursor.getColumnIndex("city")));
        }
    }

    /**
     * +获取应用列表
     *
     * @param context
     */
    public static void initAppList(Context context) {
        appInfos = GetApps.GetAppList1(context);
        DeskTopGridViewBaseAdapter deskTopGridViewBaseAdapter = new DeskTopGridViewBaseAdapter(appInfos,
                context);
        mListView.setAdapter(deskTopGridViewBaseAdapter);
    }

    /**
     * 读取昵称
     * <p>
     * SQLite
     */
    private void rember_name() {
        Cursor cursor = db.rawQuery("select * from name", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            tv_user_id.setText(cursor.getString(cursor
                    .getColumnIndex("username")));
        }
    }


    /**
     * 更新时间
     */
    private void new_time_Thread() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                SimpleDateFormat simpleDateFormat_hour = new SimpleDateFormat(
                        "HH");
                SimpleDateFormat simpleDateFormat_min = new SimpleDateFormat(
                        "mm");
                SimpleDateFormat simpleDateFormat_year = new SimpleDateFormat(
                        "yyyy年MM月dd日");
                tv_desk_top_time.setText(simpleDateFormat_year
                        .format(new java.util.Date()));
                tv_time_hour.setText(simpleDateFormat_hour
                        .format(new java.util.Date()));
                tv_time_min.setText(simpleDateFormat_min
                        .format(new java.util.Date()));
                handler.postDelayed(runnable, 500);
            }
        };
        runnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };
        handler.post(runnable);
    }

    /**
     * 绑定控件
     */
    private void initView() {
        mListView = (GridView) findViewById(R.id.mListView);
        tv_battery_show = (TextView) findViewById(R.id.tv_battery_show);
        iv_setting_button = (ImageView) findViewById(R.id.iv_setting_button);
        tv_time_hour = (TextView) findViewById(R.id.tv_time_hour);
        tg_apps_state = (ToggleButton) findViewById(R.id.tg_apps_state);
        tv_time_min = (TextView) findViewById(R.id.tv_time_min);
        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        tv_desk_top_time = (TextView) findViewById(R.id.tv_desk_top_time);
        line_wather = (LinearLayout) findViewById(R.id.line_wather);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        iv_index_back = (ImageView) findViewById(R.id.iv_index_back);
        tv_temp_state = (TextView) findViewById(R.id.tv_temp_state);
        tv_last_updatetime = (TextView) findViewById(R.id.tv_last_updatetime);
        circleView = (MyCircleView) findViewById(R.id.circle);
        circleView.setProgress(0);
        tv_user_id.setOnClickListener(this);
        iv_setting_button.setOnClickListener(this);
        dbHelper = new MyDataBaseHelper(getApplicationContext(), "info.db",
                null, 2);
        db = dbHelper.getWritableDatabase();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String fengxiang = "";
                    String fengli = "";
                    String high = "";
                    String type = "";
                    String low = "";
                    String date = "";
                    JSONArray dataArray = (JSONArray) msg.obj;
                    try {
                        String json_today = dataArray.getString(0);
                        JSONObject jsonObject = dataArray.getJSONObject(0);
                        System.out.println(jsonObject);
                        if (jsonObject != null) {
                            fengxiang = jsonObject.optString("fengxiang");
                            fengli = jsonObject.optString("fengli");
                            high = jsonObject.optString("high");
                            type = jsonObject.optString("type");
                            low = jsonObject.optString("low");
                            date = jsonObject.optString("date");
                        }
                        Cursor cursor = db.rawQuery("select * from wather_city",
                                null);
                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            tv_city.setText(cursor.getString(cursor
                                    .getColumnIndex("city")) + "  " + type);
                        } else {
                            DiyToast.showToast(getApplicationContext(), "请选择天气城市");
                        }
                        tv_wind.setText(fengxiang + "  " + fengli);
                        tv_temp_state.setText(high + "  " + low);
                        tv_last_updatetime.setText("最后更新时间：" + date
                                + tv_time_hour.getText().toString() + ":"
                                + tv_time_min.getText().toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };

    public void update_wather(Context context, final String city) {

        if (TextUtils.isEmpty(city)) {
            DiyToast.showToast(context, "城市错误，不在数据库中");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                try {
                    URL url = new URL(
                            "http://wthrcdn.etouch.cn/weather_mini?city="
                                    + URLEncoder.encode(city, "UTF-8"));
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        // 连接网络成功
                        InputStream in = conn.getInputStream();
                        String data = StreamTool.decodeStream(in);
                        // 解析json格式的数据
                        JSONObject jsonObj = new JSONObject(data);
                        // 获得desc的值
                        String result = jsonObj.getString("desc");
                        if ("OK".equals(result)) {
                            // 城市有效，返回了需要的数据
                            JSONObject dataObj = jsonObj.getJSONObject("data");
                            JSONArray jsonArray = dataObj
                                    .getJSONArray("forecast");
                            // 通知更新ui
                            Message msg = Message.obtain();
                            msg.obj = jsonArray;
                            msg.what = 0;
                            mHandler.sendMessage(msg);
                        } else {
                            // 城市无效
                            Message msg = Message.obtain();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            }

            ;
        }.start();
    }

    /**
     * 拦截返回键、Home键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Activity被销毁的同时销毁广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryLevelRcvr);
    }

    /**
     * 充电状态显示
     * <p>
     * Code Copy from http://blog.sina.com.cn/s/blog_c79c5e3c0102uyun.html
     */
    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    sb.append("'s battery feels very hot!");
                } else {
                    if (status == BatteryManager.BATTERY_STATUS_FULL) {//充电完成
                        sb.append(String.valueOf(level) + "% " + " 充电完成 ");
                    }
                    if (status == BatteryManager.BATTERY_STATUS_CHARGING) {//充电
                        sb.append(String.valueOf(level) + "% " + " 正在充电 ");
                    }
                    if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {//放电
                        sb.append(String.valueOf(level) + "% " + " 使用中 ");
                    }
                    if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {//未在充电
                        sb.append(String.valueOf(level) + "% " + " 使用中 ");
                    }
                }
                circleView.setProgress(level);
                sb.append(' ');
                tv_battery_show.setText(sb.toString());
            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }


    private void show_name_dialog() {
        final AlertDialog builder = new AlertDialog.Builder(
                MainActivity.this).create();
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.dialog_name_show, null, false);
        builder.setView(view);
        Window window = builder.getWindow();
        builder.getWindow();
        window.setGravity(Gravity.CENTER); // 底部位置
        window.setContentView(view);
        final EditText et_name_get = (EditText) view
                .findViewById(R.id.et_title_name);
        final RadioButton ra_0 = (RadioButton) view
                .findViewById(R.id.radio0);
        final RadioButton ra_1 = (RadioButton) view
                .findViewById(R.id.radio1);
        final RadioButton ra_2 = (RadioButton) view
                .findViewById(R.id.radio2);
        final RadioButton ra_3 = (RadioButton) view
                .findViewById(R.id.radio3);
        final Button btn_con = (Button) view.findViewById(R.id.btn_dialog_rename_con);
        final Button btn_cls = (Button) view.findViewById(R.id.btn_dialog_rename_cls);
        builder.setTitle("请输入你的要显示的内容");
        btn_cls.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        btn_con.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name_get.getText().toString().isEmpty()
                        && !ra_0.isChecked() && !ra_2.isChecked()
                        && !ra_3.isChecked() && !ra_1.isChecked()) {
                    db.execSQL("update name set username = ?",
                            new String[]{""});
                } else {
                    if (ra_0.isChecked() || ra_1.isChecked()
                            || ra_2.isChecked() || ra_3.isChecked()) {
                        if (ra_0.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_0.getText()
                                            .toString() + "多看电纸书"});
                        }
                        if (ra_1.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_1.getText()
                                            .toString() + "多看电纸书"});
                        }
                        if (ra_2.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_2.getText()
                                            .toString() + "多看电纸书"});
                        }
                        if (ra_3.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_3.getText()
                                            .toString() + "多看电纸书"});
                        }
                    } else {
                        db.execSQL("update name set username = ?",
                                new String[]{et_name_get
                                        .getText().toString()});
                    }
                }
                builder.dismiss();
                rember_name();
            }
        });
        builder.show();
    }




    /**
     * 桌面左下角设置 点击事件监听
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_setting_button:
                final AlertDialog alertDialog = new AlertDialog.Builder(
                        MainActivity.this).create();
                alertDialog.show();
                View window_view = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.popwindow_setting, null, false);
                Window window = alertDialog.getWindow();
                alertDialog.getWindow();//设置window
                window.setGravity(Gravity.BOTTOM); // 底部位置
                window.setContentView(window_view);//设置View
                //绑定
                LinearLayout lv_open_dk_window = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_open_dk_window);
                LinearLayout lv_open_dksetting = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_open_dksetting);
                LinearLayout lv_get_systeminfo = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_get_systeminfo);
                LinearLayout lv_desktop_setting = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_desktop_setting);
                LinearLayout lv_name_setting = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_name_setting);
                LinearLayout lv_update_applist = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_update_applist);
                LinearLayout lv_about_activity = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_about_activity);
                LinearLayout lv_shuoming_activity = (LinearLayout) alertDialog
                        .findViewById(R.id.lv_shuoming_activity);
                //隐藏暂时无用的选项
                lv_about_activity.setVisibility(View.GONE);//关于
                lv_shuoming_activity.setVisibility(View.GONE);//说明

                if (SystemInFo.getDeviceManufacturer().toString().equals("Allwinner")) {
                    lv_open_dksetting.setVisibility(View.VISIBLE);
                    lv_open_dk_window.setVisibility(View.VISIBLE);
                } else {
                    lv_open_dksetting.setVisibility(View.GONE);
                    lv_open_dk_window.setVisibility(View.GONE);
                }
                //打开多看悬浮球设置
                lv_open_dk_window.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //启动其他APP的Activity示例
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName("com.moan.moanwm", "com.moan.moanwm.MainActivity");
                        intent.setComponent(cn);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                });
                //打开系统设置
                lv_open_dksetting.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //启动其他APP的Activity示例
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName("com.duokan.mireader", "com.duokan.home.SystemSettingActivity");
                        intent.setComponent(cn);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                });
                //壁纸设置
                lv_desktop_setting.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, ChoseImagesActivity.class));
                        alertDialog.dismiss();
                    }
                });
                //获取设备信息
                lv_get_systeminfo.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, SystemStringInfo.class));
                        alertDialog.dismiss();
                    }
                });
                //昵称设置
                lv_name_setting.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show_name_dialog();
                        alertDialog.dismiss();
                    }
                });
                //更新应用列表
                lv_update_applist.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initAppList(MainActivity.this);
                        alertDialog.dismiss();
                    }
                });

                break;
            default:
                break;
        }
    }
}