package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.orhanobut.logger.Logger;
import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.MainAdapter;
import com.wzy.lamanpro.bean.GFs;
import com.wzy.lamanpro.dao.MainDaoUtils;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.utils.SPUtility;
import com.wzy.lamanpro.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ListView main_list;
    private FloatingActionButton fab;
    private NavigationView nav_view;
    private DrawerLayout drawer_layout;
    private MainAdapter mainAdapter;
    private List<GFs> gFsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(Main2Activity.this, SettingTest.class));
                return true;
            case R.id.use_info:
                new AlertDialog.Builder(Main2Activity.this)
                        .setMessage("内容正在完善中。。。")
                        .setTitle("使用说明:")
                        .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
//            case R.id.action_report:
//
//                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initData() {
        gFsList = new ArrayList<>();
        mainAdapter = new MainAdapter(gFsList, this, R.layout.item_main);
        main_list.setAdapter(mainAdapter);

        OkGo.<String>get("https://cnxa.top:8443/api/gfs/list").tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Logger.d(response);
                GFs[] gFs = new Gson().fromJson(response.body(), GFs[].class);
                gFsList = Arrays.asList(gFs);
                MainDaoUtils mainDaoUtils = new MainDaoUtils(Main2Activity.this);
                mainDaoUtils.deleteAllGF();
                if (mainDaoUtils.queryAllGFs() == null || mainDaoUtils.queryAllGFs().size() == 0)
                    mainDaoUtils.insertGFList(gFsList);
                mainAdapter.updateRes(gFsList);

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);

            }

            @Override
            public void onFinish() {
                super.onFinish();

            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                super.onCacheSuccess(response);
                Logger.d(response);
                GFs[] gFs = new Gson().fromJson(response.body(), GFs[].class);
                gFsList = Arrays.asList(gFs);
                mainAdapter.updateRes(gFsList);
            }
        });

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        main_list = (ListView) findViewById(R.id.main_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fab.setOnClickListener(this);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        View headerView = nav_view.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.textView);
        textView.setText(new UserDaoUtils(this).queryUserName(SPUtility.getUserId(this)));
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Main2Activity.this, GFsDetails.class);
                intent.putExtra("id", gFsList.get(position).getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                break;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_camera:
                startActivity(new Intent(Main2Activity.this, AddLibrary.class));
                break;
            case R.id.nav_gallery:
                startActivity(new Intent(Main2Activity.this, ManageData.class));
                break;
            case R.id.nav_slideshow:
                startActivity(new Intent(Main2Activity.this, ManageHis.class));
                break;
            case R.id.nav_manage:
                startActivity(new Intent(Main2Activity.this, ManageUsers.class));
                break;
            case R.id.nav_setting:
                startActivity(new Intent(Main2Activity.this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                showStyleDialog();
                break;
            case R.id.nav_shutdown:
                SystemUtils.shutDowm();
                break;
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void showStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("您确定注销吗？");
        builder.setTitle("温 馨 提 示 :");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPUtility.putSPBoolean(Main2Activity.this, "isAutoLogin", false);
                finish();
                Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
