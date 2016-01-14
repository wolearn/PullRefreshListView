package sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.empty.pullrefreshlistview.PullRefreshListView;
import com.empty.pullrefreshlistview.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PullRefreshListView.OnPullRefreshListener {
    private List<String> mDatas;
    private ArrayAdapter<String> mAdapter;
    private PullRefreshListView mPullRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void init() {
        try {
            mPullRefreshListView = (PullRefreshListView) findViewById(R.id.pullRefreshListView);
            initData();
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDatas);
            mPullRefreshListView.setOnPullRefreshListener(this);
            mPullRefreshListView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        mDatas = new ArrayList<>();
        for (int i=0; i<20; i++)
        {
            mDatas.add(String.valueOf(i));
        }
    }

    @Override
    public void onRefresh() {
        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟耗时任务
                    Thread.sleep(3000);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //任务执行完毕
                            mPullRefreshListView.complete();
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
