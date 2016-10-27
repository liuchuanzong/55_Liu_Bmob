package com.example.administrator.a55_liu_bmob;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.a55_liu_bmob.bean.User;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class RegisterActivity extends AppCompatActivity {

    private EditText editText_phone;
    private EditText editText_phoneword;
    private EditText editText_password;
    //系统验证码
    private int validCode = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editText_phone = (EditText) findViewById(R.id.editText_phone);
        editText_phoneword = (EditText) findViewById(R.id.editText_phoneword);
        editText_password = (EditText) findViewById(R.id.editText_password);
    }
    //获取验证码
    public void getValidCodeClick(View v){
        String name = editText_phone.getText().toString();
        if (TextUtils.isEmpty(name)){
            toast("手机号不能为空");
            return;
        }
        requestValidCode(name);
    }

    //发送请求验证码
    private void requestValidCode(String phone) {
        //参数："11位手机号码","模板名称"，
        BmobSMS.requestSMSCode(phone, "Liu_Bmob", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e==null) {
                    toast("获取验证码成功");
                }else {
                    toast("获取验证码失败");
                }
            }
        });
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //注册
    public void registerClick(View v){
        //验证验证码是否正确
        final String validCode = editText_phoneword.getText().toString();
        if(TextUtils.isEmpty(validCode)){
            toast("请输入验证码");
            return;
        }
        final String name = editText_phone.getText().toString();
        final String password = editText_password.getText().toString();
        BmobSMS.verifySmsCode(name, validCode, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null) {
                    User user = new User();
                    user.setUsername(name);
                    user.setPassword(password);
                    user.signUp( new SaveListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if(e==null){
                                toast("注册成功");
                            }else{
                                toast("注册失败:" + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    //登录
    public void goLoginClick(View v){
        BmobUser bu = BmobUser.getCurrentUser();
        if (bu != null) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
