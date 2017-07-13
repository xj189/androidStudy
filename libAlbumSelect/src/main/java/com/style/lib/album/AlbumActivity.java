package com.style.lib.album;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.xiajun.libalbumselect.R;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


public class AlbumActivity extends BaseToolBarActivity {
    TextView tvNumber;
    RecyclerView rcvFolder;
    DrawerLayout drawerLayout;
    RecyclerView rcvImage;
    private List<PicBucket> buckets;
    private ImageFolderAdapter folderAdapter;
    private ImageItemAdapter imageAdapter;
    private ArrayList<String> paths;
    private int maxNum;
    private List<ImageItem> list;

    @Override
    protected void customWindowKitkat(Window window) {
        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLayoutResID = R.layout.activity_album;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void findView() {
        tvNumber = getView(R.id.tv_number);
        rcvFolder = getView(R.id.rcv_folder);
        drawerLayout = getView(R.id.drawer_layout);
        rcvImage = getView(R.id.rcv_image);
    }

    @Override
    public void initData() {
        getToolbarRightView().setVisibility(View.GONE);
        setToolbarTitle("相册");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        paths = getIntent().getStringArrayListExtra("paths");
        if (paths == null)
            paths = new ArrayList<>();
        maxNum = getIntent().getIntExtra("maxNum", 9);
        buckets = new ArrayList<>();
        folderAdapter = new ImageFolderAdapter(getContext(), buckets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvFolder.setLayoutManager(linearLayoutManager);
        rcvFolder.addItemDecoration(new DividerItemDecoration(getContext()));
        rcvFolder.setAdapter(folderAdapter);

        list = new ArrayList<>();
        imageAdapter = new ImageItemAdapter(getContext(), list);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rcvImage.setLayoutManager(gridLayoutManager);
        rcvImage.setAdapter(imageAdapter);

        folderAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                changeData(buckets.get(position));
            }
        });
        imageAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                ImageItem item = list.get(position);
                double sizeM = item.getSize() / 1024.00 / 1024.00;
                Log.e("GallaryActivity", "选中图片的大小是-->" + sizeM);

                boolean isSelected = item.isSelected();
                if (!isSelected) { // 未被选中
                    if (paths.size() >= maxNum) {
                        showToast("最多不能超过" + maxNum + "张图片");
                        return;
                    }
                }
                item.setSelected(!isSelected);
                imageAdapter.notifyDataSetChanged();
                folderAdapter.notifyDataSetChanged();
                String path = item.getImagePath();
                if (isSelected) { // 已经被选中
                    paths.remove(path);
                    return;
                }
                paths.add(path);
            }
        });
        getData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_with_text, menu);
        menu.getItem(0).setTitle(R.string.ok);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_text_only:
                setResult();
                break;
            case android.R.id.home:
                setResult();
                break;
        }
        return true;
    }

    public void changeData(PicBucket picBucket) {
        setToolbarTitle(picBucket.getBucketName());
        list.clear();
        list.addAll(picBucket.getImages());
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            setResult();
        }
    }

    public void setResult() {
        setResult(RESULT_OK, new Intent().putStringArrayListExtra("paths", paths));
        finish();
    }

    private void initStatus(List<PicBucket> list, List<String> paths2) {
        for (int i = 0; i < list.size(); i++) {
            List<ImageItem> items = list.get(i).getImages();
            int count = items.size();
            for (int j = 0; j < count; j++) {
                items.get(j).setSelected(false);
                String path = items.get(j).getImagePath();
                int num = paths2.size();
                for (int k = 0; k < num; k++) {
                    if (path.equals(paths2.get(k)))
                        items.get(j).setSelected(true);
                }
            }
        }
    }


    private Handler

    private void getData() {
        CachedThreadPoolManager.getInstance().runTask(TAG, new MyTaskCallBack() {
            @Override
            public Object doInBackground() {
                Log.e(AlbumActivity.this.TAG, "doInBackground");
                List<PicBucket> list = LocalImagesHelper.getPicBuckets(getContext());
                return list;
            }

            @Override
            public void onSuccess(Object object) {
                Log.e(AlbumActivity.this.TAG, "OnSuccess");
                if (object != null) {
                    List<PicBucket> list = (List<PicBucket>) object;
                    initStatus(list, paths);
                    buckets.clear();
                    buckets.addAll(list);
                    folderAdapter.notifyDataSetChanged();
                    changeData(buckets.get(0));
                }
            }

            @Override
            public void onFailed(String message) {
                super.onFailed(message);
            }
        });
    }
}
