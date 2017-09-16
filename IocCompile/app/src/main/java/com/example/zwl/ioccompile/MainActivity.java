package com.example.zwl.ioccompile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.ioc_annotation.BindView;
import com.example.ioc_api.ViewInjector;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_hello)
    TextView mTvHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInjector.bind(this);
        mTvHello.setText("hello sange");
    }
}
