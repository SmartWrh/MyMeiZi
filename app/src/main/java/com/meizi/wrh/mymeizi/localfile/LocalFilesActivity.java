package com.meizi.wrh.mymeizi.localfile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.activity.BaseActivity;
import com.meizi.wrh.mymeizi.adapter.LocalFilesAdapter;
import com.meizi.wrh.mymeizi.listener.OnItemClickListener;
import com.meizi.wrh.mymeizi.util.DividerItemDecoration;
import com.meizi.wrh.mymeizi.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class LocalFilesActivity extends BaseActivity implements OnItemClickListener{

    private List<File> mFileData = new ArrayList<>();
    private FileUtil fileUtil;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private LocalFilesAdapter fileAdapter;
    private AlertDialog.Builder mDeleteDialog;
    private Subscription mSubscriber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_files);
        toolbar = (Toolbar) findViewById(R.id.local_files_toolbar);
        toolbar.setTitle(getResources().getString(R.string.local_files_manager));
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.local_list);
           recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileAdapter = new LocalFilesAdapter(this, mFileData);
        fileAdapter.setOnItemClickListener(this);
          recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(fileAdapter);
         fileUtil = new FileUtil(this);
         loadData();
    }

    private void loadData() {
        mSubscriber = Observable.from(fileUtil.getDiskCacheDir().listFiles()).subscribe(new Subscriber<File>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(File file) {
                mFileData.add(file);
                fileAdapter.notifyItemInserted( mFileData.size());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSubscriber != null && mSubscriber.isUnsubscribed()) {
            mSubscriber.unsubscribe();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (mDeleteDialog == null) {
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.alertDialogTheme, outValue, true);
            mDeleteDialog = new AlertDialog.Builder(LocalFilesActivity.this, outValue.resourceId);
            mDeleteDialog.setTitle("删除");
            mDeleteDialog.setNegativeButton("取消", null);
            mDeleteDialog.create();
        }
          mDeleteDialog.setPositiveButton("确认", new DialogClick(position));
        mDeleteDialog.setMessage(mFileData.get(position).getName());
        mDeleteDialog.show();
    }

    private class DialogClick implements DialogInterface.OnClickListener {

        private int mPosition;

        public DialogClick(int position) {
            this.mPosition = position;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            fileUtil.deleteFile(mFileData.get(mPosition));
            fileAdapter.notifyItemRemoved(mPosition);
            mFileData.remove(mPosition);
        }
    }
}
