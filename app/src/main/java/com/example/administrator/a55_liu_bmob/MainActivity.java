package com.example.administrator.a55_liu_bmob;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.a55_liu_bmob.bean.Person;
import com.example.administrator.a55_liu_bmob.bean.User;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MainActivity extends AppCompatActivity {

    private static final int GET_IMAGE_CODE = 0x1;
    private static final String TAG = "必胜";
    private ImageView imageView_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView_icon = (ImageView) findViewById(R.id.imageView_icon);
        loadUserInfo();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void addClick(View v) {
        Person person = new Person();
        person.setName("j哥");
        person.setAddress("北京");
        person.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "添加成功" + s, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateClick(View v) {
        Person person = new Person();
        person.setName("高尔夫司机");
        person.setAddress("昌平");
        person.setAge(18);
        person.update("b271179720", new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void queryClick(View v) {
        BmobQuery<Person> query = new BmobQuery<>();
        //单条
//        query.getObject("947c9b293e", new QueryListener<Person>() {
//            @Override
//            public void done(Person person, BmobException e) {
//                Toast.makeText(MainActivity.this, "查询结果为："+person.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        //多条
//        query.findObjects(new FindListener<Person>() {
//            @Override
//            public void done(List<Person> list, BmobException e) {
//                Toast.makeText(MainActivity.this, "查询结果为："+list.toString(), Toast.LENGTH_SHORT).show();
//            }
//        });
        //分页显示
        //从第几个开始显示
        query.setSkip(10);
        //一页显示多少行
        query.setLimit(5);
        //排序 前面加-表示降序
        query.order("-createdAt").findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                for (Person p : list) {
                    System.out.println(p);
                }
            }
        });

    }

    public void deleteClick(View v) {
        Person person = new Person();
        person.setObjectId("83b22b339d");
        person.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //修改密码
    public void updatePasswordClick(View v) {

        BmobUser.updateCurrentUserPassword("123456", "654321",
                new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            toast("密码修改成功");
                        } else {
                            toast("密码修改失败");
                        }
                    }
                });
    }

    //上传头像
    public void uploadIconClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GET_IMAGE_CODE);
    }

    //图库回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "resultCode: "+requestCode);
        Log.i(TAG, "requestCode: "+requestCode);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == GET_IMAGE_CODE) {
                    Log.i(TAG, "ioi: ");
                    Uri uri = data.getData();
                    imageView_icon.setImageURI(uri);
                    // TODO: 2016/10/26  参数：？
                    Cursor c = getContentResolver().query(uri
                            , new String[]{MediaStore.Images.Thumbnails.DATA}
                            , null, null, null, null);
                    if (c != null && c.moveToFirst()) {
                        String imageUrl = c.getString(0);
                        uploadIcon(imageUrl);
                        Log.i(TAG, "imageUrl: "+imageUrl);
                    }
                }
                break;
        }
    }

    private void uploadIcon(String imageUrl) {

        final BmobFile file = new BmobFile(new File(imageUrl));
        file.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User currentUser = BmobUser.getCurrentUser(User.class);
                    currentUser.setIcon(file);
                    currentUser.update(currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                toast("头像更新成功");
                            } else {
                                toast("头像更新失败");
                            }
                        }
                    });
                    toast("头像更新成功");
                } else {
                    toast("头像更新失败，失败原因：" + e);
                }
            }
        });
    }

    //加载用户信息
    private void loadUserInfo(){
        User currentUser = BmobUser.getCurrentUser(User.class);
        BmobFile file = currentUser.getIcon();
        HttpRequestUtils.getInstance(this).loadImage(file.getFileUrl()
                ,72,72,imageView_icon);

    }

    //退出
    public void exitClick(View v){
        BmobUser.logOut();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();

    }

    //便签
    public void myNoteClick(View v){
        startActivity(new Intent(MainActivity.this,NotesActivity.class));
    }
}
