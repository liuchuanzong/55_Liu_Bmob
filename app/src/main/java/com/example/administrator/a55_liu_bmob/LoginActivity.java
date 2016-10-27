package com.example.administrator.a55_liu_bmob;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.a55_liu_bmob.bean.User;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {

    private EditText editText_login_phone;
    private EditText editText_login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editText_login_phone = (EditText) findViewById(R.id.editText_login_phone);
        editText_login_password = (EditText) findViewById(R.id.editText_login_password);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //登录
    public void loginClick(View v) {
        String phone = editText_login_phone.getText().toString();
        String password = editText_login_password.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            toast("手机号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            toast("密码不能为空");
            return;
        }
        login(phone, password);
    }

    //发送登录请求
    private void login(final String userName, String passWord) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(passWord);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    toast("登录成功:");
                    //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                    //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                    toast("欢迎回来" + userName);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    toast("登录失败，错误："+e);
                }
            }
        });
    }

    public void leaveClick(View v){
        finish();
    }
}
