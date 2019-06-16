package com.wzy.lamanpro.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.adapter.DataAdapter;
import com.wzy.lamanpro.bean.HisData;
import com.wzy.lamanpro.bean.ProductData;
import com.wzy.lamanpro.dao.DataDaoUtils;
import com.wzy.lamanpro.dao.HisDaoUtils;
import com.wzy.lamanpro.dao.UserDaoUtils;
import com.wzy.lamanpro.utils.PdfManager;
import com.wzy.lamanpro.utils.SPUtility;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.wzy.lamanpro.common.LaManApplication.isManager;

public class ManageData extends AppCompatActivity implements View.OnClickListener {

    private ListView dataList;
    private List<ProductData> productData;
    private DataAdapter dataAdapter;
    private FloatingActionButton menu_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data);
        initView();
        initData();
    }

    private void initData() {
        productData = new DataDaoUtils(this).queryAllData();
        dataAdapter = new DataAdapter(productData, this, R.layout.item_data);
        dataList.setAdapter(dataAdapter);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageData.this, DataDetails.class);
                intent.putExtra("id", productData.get(position).getId());
                startActivity(intent);
            }
        });

        if (!isManager) {
            return;
        }
        dataList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(ManageData.this)
                        .setTitle("温馨提示")
                        .setMessage("确定要删除吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DataDaoUtils(ManageData.this).deleteUser(productData.get(position).getId());
                                productData = new DataDaoUtils(ManageData.this).queryAllData();
                                dataAdapter.updateRes(productData);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("取消", null).create().show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.user_data:
                productData = new DataDaoUtils(this).queryAllData();
                dataAdapter.updateRes(productData);
//                startActivity(new Intent(AddLibrary.this, SettingTest.class));
                return true;
            case R.id.standard_data:
                dataAdapter.removeAll();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dataList = (ListView) findViewById(R.id.dataList);
        menu_search = (FloatingActionButton) findViewById(R.id.menu_search);
        menu_search.setOnClickListener(this);
        Toast.makeText(this, "长按条目即可删除", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        productData = new DataDaoUtils(this).queryAllData();
        dataAdapter.updateRes(productData);
    }

    //选择日期
    private void showCalendar(final TextView editText, final int type) {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear + 1;
                        if (month < 10 && dayOfMonth < 10) {
                            editText.setText(year + "/0" + month
                                    + "/0" + dayOfMonth);
                        } else if (month < 10 && dayOfMonth >= 10) {
                            editText.setText(year + "/0" + month
                                    + "/" + dayOfMonth);
                        } else if (month >= 10 && dayOfMonth < 10) {
                            editText.setText(year + "/" + month
                                    + "/0" + dayOfMonth);
                        } else {
                            editText.setText(year + "/" + month
                                    + "/" + dayOfMonth);
                        }

                        if (type == 0)
                            editText.append(" 00:00:00");
                        else
                            editText.append(" 23:59:59");


                    }
                }
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
                .get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_search:

                Snackbar.make(v, "点击可以进行条件查询，确定查询吗？", Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View view = LayoutInflater.from(ManageData.this).inflate(R.layout.content_search_data, null, false);
                                final EditText editText = view.findViewById(R.id.name);
                                final TextView timeFrom = view.findViewById(R.id.time_from);
                                final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
                                timeFrom.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showCalendar(timeFrom, 0);
                                    }
                                });
                                final TextView timeTo = view.findViewById(R.id.time_to);
                                timeTo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showCalendar(timeTo, 1);
                                    }
                                });
                                new AlertDialog.Builder(ManageData.this).setView(view).setTitle("条件查询").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ProgressDialog pd2 = ProgressDialog.show(ManageData.this, "温馨提示", "正在搜索...");
                                        String name = editText.getText().toString();
                                        String timeFromText = timeFrom.getText().toString().isEmpty() ? "1800/00/00 00:00:00" : timeFrom.getText().toString();
                                        String timeToText = timeTo.getText().toString().isEmpty() ? "2999/12/31 00:00:00" : timeTo.getText().toString();
                                        List<ProductData> productDataNew = new ArrayList<>();
                                        for (ProductData productDatum : productData) {
                                            try {
                                                long datePro = format.parse(productDatum.getDate()).getTime();
                                                if (productDatum.getProName().contains(name) && format.parse(timeFromText).getTime() <= datePro && datePro <= format.parse(timeToText).getTime()) {
                                                    productDataNew.add(productDatum);
                                                }

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        dataAdapter.removeAll();
                                        dataAdapter.updateRes(productDataNew);
                                        pd2.dismiss();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", null).create().show();


//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                    String path = Environment.getExternalStorageDirectory() + File.separator + new Date().getTime() + ".pdf";
//                                    PdfManager.makeViewEveryPdf(new View[]{view_pdf}, path);
//                                    Toast.makeText(HisDetails.this, "文件已经保存在:" + path, Toast.LENGTH_SHORT).show();
//                                }
                            }
                        }).show();
                break;
        }
    }
}
