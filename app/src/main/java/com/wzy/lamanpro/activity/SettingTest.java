package com.wzy.lamanpro.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.wzy.lamanpro.R;

public class SettingTest extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_setmode);
//        bindPreferenceSummaryToValue(findPreference("use_mode"));
        bindPreferenceSummaryToValue(findPreference("time"));
        bindPreferenceSummaryToValue(findPreference("once"));

    }

    /*
     * 自定义方法，设置监听器，查看设置中的选项是否有变更
     * */
    private void bindPreferenceSummaryToValue(Preference preference) {
        //设置监听器，查看设置中的选项是否有变更
        preference.setOnPreferenceChangeListener(this);

        //有选项变更时立即将preference文件中的value进行相应的变更
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference instanceof CheckBoxPreference) {


        }else{
            preference.setSummary(stringValue);
        }

        return true;
    }
}
