package com.etang.mt_launcher.launcher.welecome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.etang.mt_launcher.R;
import com.etang.mt_launcher.tool.mtcore.MTCore;

/**
 * @Package: com.etang.nt_launcher.launcher.welecome
 * @ClassName: OneFragment
 * @Description: “欢迎界面”第一个碎片
 * @CreateDate: 2021/3/19 8:17
 * @UpdateDate: 2021/5/29 21:52
 */
public class OneFragment extends Fragment {
    //当前页面TAG
    private static String TAG = "OneFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_welecome_1, null, false);
        return view;
    }
}
