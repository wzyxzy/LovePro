package com.wzy.lamanpro.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wzy.lamanpro.R;
import com.wzy.lamanpro.bean.Users;
import com.wzy.lamanpro.dao.UserDaoUtils;

public class UserDetails extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText;
    private EditText id_num;
    private EditText name_text;
    private EditText password;
    private EditText email;
    private EditText pemission_level;
    private Button change;
    private Button delete;
    private String account_;
    private Users users;
    private EditText account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initView();
        initData();
    }

    private void initData() {
        account_ = getIntent().getStringExtra("account");
        users = new UserDaoUtils(this).queryUser(account_);
        nameText.setText(account_);
        id_num.setText(users.getId());
        name_text.setText(users.getName());
        password.setText(users.getPassword());
        email.setText(users.getEmail());
        account.setText(users.getAccount());

    }

    private void initView() {
        nameText = (TextView) findViewById(R.id.nameText);
        id_num = (EditText) findViewById(R.id.id_num);
        name_text = (EditText) findViewById(R.id.name_text);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        pemission_level = (EditText) findViewById(R.id.pemission_level);
        change = (Button) findViewById(R.id.change);
        delete = (Button) findViewById(R.id.delete);

        change.setOnClickListener(this);
        delete.setOnClickListener(this);
        account = (EditText) findViewById(R.id.account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change:

                break;
            case R.id.delete:

                break;
        }
    }

    private void submit() {
        // validate
        String num = id_num.getText().toString().trim();
        if (TextUtils.isEmpty(num)) {
            Toast.makeText(this, "num不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = name_text.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "text不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordString = password.getText().toString().trim();
        if (TextUtils.isEmpty(passwordString)) {
            Toast.makeText(this, "passwordString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            Toast.makeText(this, "emailString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String level = pemission_level.getText().toString().trim();
        if (TextUtils.isEmpty(level)) {
            Toast.makeText(this, "level不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // validate
        String accountString = account.getText().toString().trim();
        if (TextUtils.isEmpty(accountString)) {
            Toast.makeText(this, "accountString不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }

}
