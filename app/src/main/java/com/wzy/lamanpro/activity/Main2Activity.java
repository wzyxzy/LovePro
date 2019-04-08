package com.wzy.lamanpro.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.ui.CommonBaseDialog;
import com.wzy.lamanpro.ui.CommonDialog;
import com.wzy.lamanpro.utils.SPUtility;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_setting:
                startActivity(new Intent(Main2Activity.this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                showStyleDialog();
                break;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showStyleDialog() {
        //使用 CommonBaseDialog核心代码
//        CommonBaseDialog.showDialog(this, R.layout.common_dialog)
//                .setText(R.id.title, "温 馨 提 示 :")
//                .setDialogLocation(Gravity.CENTER, 50, 0, 50, 0)
//                .setText(R.id.message, "您确定注销吗？")
//                .setViewListener(new CommonBaseDialog.OnCloseListener() {
//                    @Override
//                    public void onClick(Dialog dialog, int viewId) {
//                        switch (viewId) {
//                            case R.id.yes:
//                                SPUtility.putSPBoolean(Main2Activity.this, "isAutoLogin", false);
//                                finish();
//                                startActivity(new Intent(Main2Activity.this, LoginActivity.class));
//                                break;
//                            case R.id.no:
//                                break;
//                        }
//                        dialog.dismiss();
//                    }
//                }, R.id.no, R.id.yes);

        CommonDialog commonDialog = new CommonDialog(this);
        commonDialog.setTitle("温 馨 提 示 :");
        commonDialog.setMessage("您确定注销吗？");
        commonDialog.setRightButtonClickListener(new CommonDialog.RightButtonClickListener() {
            @Override
            public void onRightButtonClick() {
                SPUtility.putSPBoolean(Main2Activity.this, "isAutoLogin", false);
                finish();
                Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        commonDialog.show();
    }

}
