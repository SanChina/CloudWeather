package com.maple.cloudweather.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maple.cloudweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.bannner)
    ImageView mBannner;
    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.bt_code)
    Button mBtCode;
    @BindView(R.id.bt_blog)
    Button mBtBlog;
    @BindView(R.id.bt_weibo)
    Button mBtPay;
    @BindView(R.id.bt_share)
    Button mBtShare;
    @BindView(R.id.bt_update)
    Button mBtUpdate;
    @BindView(R.id.bt_bug)
    Button mBtBug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);


        mToolbar.setTitle("枫叶天气");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @OnClick({R.id.bt_code, R.id.bt_blog, R.id.bt_weibo, R.id.bt_share, R.id.bt_update, R.id.bt_bug})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_code:
                visitWeb(getString(R.string.github_url));
                break;
            case R.id.bt_blog:
                visitWeb(getString(R.string.jianshu_url));
                break;
            case R.id.bt_weibo:
                visitWeb(getString(R.string.weibo_url));
                break;
            case R.id.bt_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                intent.putExtra(Intent.EXTRA_TEXT, "欢迎使用＂枫叶天气＂");
                startActivity(Intent.createChooser(intent, "分享-枫叶天气"));
                break;
            case R.id.bt_update:
                break;
            case R.id.bt_bug:
                break;
        }
    }

    private void visitWeb(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
    }
}
