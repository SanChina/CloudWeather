package com.maple.cloudweather.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maple.cloudweather.R;
import com.maple.cloudweather.ui.service.AutoUpdateService;
import com.maple.cloudweather.uitl.FileUtil;
import com.maple.cloudweather.uitl.PrefUtil;
import com.maple.cloudweather.uitl.RxUtil;
import com.maple.cloudweather.uitl.UIUtil;

import java.io.File;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment
        implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    private PrefUtil mPref;
    private Preference mChangeUpdate;
    private Preference mClearCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mPref = PrefUtil.getInstance();

        mChangeUpdate = findPreference(PrefUtil.AUTO_UPDATE);
        mClearCache = findPreference(PrefUtil.CLEAR_CACHE);

        //设置监听器
        mClearCache.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mClearCache == preference) {
            Glide.get(getActivity()).clearMemory();
            Observable
                    .just(FileUtil.delete(new File(UIUtil.getContext().getCacheDir() + "/net_cache")))
                    .filter(new Func1<Boolean, Boolean>() {
                        @Override
                        public Boolean call(Boolean aBoolean) {
                            return aBoolean;
                        }
                    })
                    .compose(RxUtil.<Boolean>rxSchedulerHelper())
                    .subscribe(new Action1<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
//                            mClearCache.setSummary(FileSizeUitl.getAutoFileOrFilesSize(UIUtil.getContext().ca"/net_cache"));
                            Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        } else if (mChangeUpdate == preference) {
            showAutoDialog();
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    private void showAutoDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_update, (ViewGroup) getActivity().findViewById(R.id.dialog_update_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);
        final AlertDialog dialog = builder.create();

        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        final TextView showHour = (TextView) view.findViewById(R.id.tv_show_hour);
        TextView done = (TextView) view.findViewById(R.id.tv_done);

        seekBar.setMax(24);
        seekBar.setProgress(mPref.getAutoUpdate());
        showHour.setText(String.format("每%s小时", seekBar.getProgress()));
        dialog.show();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPref.setAutoUpdate(seekBar.getProgress());
                mChangeUpdate.setSummary(
                        mPref.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mPref.getAutoUpdate() + "小时更新");
                Intent intent = new Intent(getActivity(), AutoUpdateService.class);
                getActivity().startService(intent);
                dialog.dismiss();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showHour.setText(String.format("每%s小时", seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
