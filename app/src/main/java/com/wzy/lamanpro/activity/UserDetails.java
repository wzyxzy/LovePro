package com.wzy.lamanpro.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.dao.UserDaoUtils;

public class UserDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText;
    private EditText name_text;
    private EditText password;
    private EditText email;
    private Switch pemission_level;
    private Button change;
    private Button delete;
    private String account_;
    private Users users;
    private EditText account;
    private boolean canEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initView();
        initData();
    }

    private void initData() {
        account_ = getIntent().getStringExtra("account");
        if (TextUtils.isEmpty(account_)) {
            canEdit = true;
            name_text.setEnabled(true);
            password.setEnabled(true);
            account.setEnabled(true);
            email.setEnabled(true);
            pemission_level.setEnabled(true);
            users = new Users();
        } else {
            users = new UserDaoUtils(this).queryUser(account_);
            nameText.setText(account_);
            name_text.setText(users.getName());
            password.setText(users.getPassword());
            email.setText(users.getEmail());
            account.setText(users.getAccount());
            pemission_level.setChecked(users.getLevel() == 1);
        }

    }

    private void initView() {
        nameText = (TextView) findViewById(R.id.nameText);
        name_text = (EditText) findViewById(R.id.name_text);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        pemission_level = (Switch) findViewById(R.id.pemission_level);
        change = (Button) findViewById(R.id.change);
        delete = (Button) findViewById(R.id.delete);
        change.setOnClickListener(this);
        delete.setOnClickListener(this);
        account = (EditText) findViewById(R.id.account);
        if (canEdit) {
            name_text.setEnabled(true);
            password.setEnabled(true);
            account.setEnabled(true);
            email.setEnabled(true);
            pemission_level.setEnabled(true);
        } else {
            name_text.setEnabled(false);
            password.setEnabled(false);
            account.setEnabled(false);
            email.setEnabled(false);
            pemission_level.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change:
                canEdit = true;
                name_text.setEnabled(true);
                password.setEnabled(true);
                account.setEnabled(true);
                email.setEnabled(true);
                pemission_level.setEnabled(true);
                break;
            case R.id.delete:
                if (users.getLevel() == 1) {
                    Toast.makeText(this, "该账户为管理员账户，不可删除！", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("您确定要删除改账户吗？");
                    builder.setTitle("特别提示");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new UserDaoUtils(UserDetails.this).deleteUser(account_);
                            finish();
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
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (canEdit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("您要保存修改的数据吗？");
            builder.setTitle("特别提示");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (submit() && users != null) {
                        if (TextUtils.isEmpty(account_)) {
                            new UserDaoUtils(UserDetails.this).insertUserList(users);
                            finish();
                        } else {
                            new UserDaoUtils(UserDetails.this).updateUser(users);
                            finish();
                        }
                    }
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.create().show();
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return super.onKeyDown(keyCode, event);
//
//    }

    private boolean submit() {
        String text = name_text.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        String passwordString = password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        String emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            Toast.makeText(this, "email不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

//        String level = pemission_level.getText().toString().trim();
//        if (TextUtils.isEmpty(level)) {
//            Toast.makeText(this, "level不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
        // validate
        String accountString = account.getText().toString().trim();
        if (TextUtils.isEmpty(accountString)) {
            Toast.makeText(this, "账号不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO validate success, do something
        users.setAccount(accountString);
        users.setEmail(emailString);
        users.setLevel(pemission_level.isChecked() ? 1 : 0);
        users.setName(text);
        users.setPassword(passwordString);
        return true;

//        return new Users(num, text, accountString, passwordString,
//                emailString, pemission_level.isChecked() ? 1 : 0);

    }

}
