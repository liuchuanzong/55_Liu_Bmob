package com.example.administrator.a55_liu_bmob;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.a55_liu_bmob.bean.Note;

import java.io.Serializable;
import java.util.Date;

public class NoteEditActivity extends AppCompatActivity {
    //判断新建还是修改的常量
    private static final int UPDATA_OP = 0x1;
    private static final int ADD_OP = 0x2;
    //判断新建还是修改的标量(标记)
    private int op;
    private TextView textView_edit_date;
    private EditText editText_edit_content;
    private Note upnote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        textView_edit_date = (TextView) findViewById(R.id.textView_edit_date);
        editText_edit_content = (EditText) findViewById(R.id.editText_edit_content);
        initData();
    }

    private void initData() {
        Serializable data = getIntent().getSerializableExtra("note");
        if(data!=null){
            op = UPDATA_OP;
            upnote = (Note) data;
            textView_edit_date.setText(upnote.getUpdatedAt());
            editText_edit_content.setText(upnote.getContent());
        }else {
            op = ADD_OP;
            textView_edit_date.setText(DateUtils.toDate(new Date()));
        }
    }


}
