package com.gaia.hello;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.gaia.hello.model.GithubUserInfo;
import com.gaia.hello.protocol.GithubUserInfoTask;

/**
 * sample entrance activity in bundle
 * @author neil
 * @since 2017.05.08
 */
public class GaiaHelloActivity extends AppCompatActivity {
    private TextView mTextView;

    private GithubUserInfoTask.IGithubUserInfoTaskListener mListener = new GithubUserInfoTask.IGithubUserInfoTaskListener() {
        @Override
        public void onDataObtain(GithubUserInfo info) {
            if (info != null) {
                mTextView.setText(info.name);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        mTextView = (TextView) findViewById(R.id.hello_txt);
        GithubUserInfoTask.queryUserInfo("neilnee", mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
