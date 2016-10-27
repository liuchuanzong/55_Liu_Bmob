package com.example.administrator.a55_liu_bmob;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.a55_liu_bmob.bean.Note;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class NotesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int DEL_ITEM = 0x1;
    private ListView listView_notes;
    private List<Note> notes ;
    private NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        listView_notes = (ListView) findViewById(R.id.listView_notes);
        //添加单机事件
        listView_notes.setOnItemClickListener(this);
        // 添加按住上下文菜单
        registerForContextMenu(listView_notes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    //初始化数据
    private void initData() {
        final BmobQuery<Note> query = new BmobQuery<>();
        query.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if (e==null){
                    notes = list;
                    notesAdapter = new NotesAdapter(NotesActivity.this,notes);
                    listView_notes.setAdapter(notesAdapter);
                }else {
                    Toast.makeText(NotesActivity.this, "并没有什么...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //添加菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //添加菜单选项
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note:
                startActivity(new Intent(this,NoteEditActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //列表点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Note note = (Note) notesAdapter.getItem(position);
        Intent intent = new Intent(this,NoteEditActivity.class);
        intent.putExtra("note",note);
        startActivity(intent);
    }

    //设置按住上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("删除");
        menu.setHeaderIcon(android.R.drawable.ic_delete);
        menu.add(0,DEL_ITEM,0,"删除");
    }

    //上下文菜单选项
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DEL_ITEM:
                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final Note note = (Note) notesAdapter.getItem(info.position);
                note.delete(note.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            notes.remove(note);
                            notesAdapter.notifyDataSetChanged();
                            Toast.makeText(NotesActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
        // TODO: 2016/10/26 返回true或者false表明什么
        return true;
    }

    private static class NotesAdapter extends BaseAdapter{
        private Context context;
        private List<Note> list ;

        public NotesAdapter(Context context, List<Note> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh;
            if (convertView==null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.notes_item,null);
                vh = new ViewHolder();
                vh.tv_content = (TextView) convertView.findViewById(R.id.textView_item_content);
                vh.tv_updateDate = (TextView) convertView.findViewById(R.id.textView_itme_date);
                convertView.setTag(vh);
            }else {
                vh = (ViewHolder) convertView.getTag();
            }
            Note n = list.get(position);
            vh.tv_content.setText(n.getContent());
            // TODO: 2016/10/26  getUpdatedAt?
            vh.tv_updateDate.setText(n.getUpdatedAt());
            return convertView;
        }
        class ViewHolder{
            TextView tv_content;
            TextView tv_updateDate;
        }
    }



}
