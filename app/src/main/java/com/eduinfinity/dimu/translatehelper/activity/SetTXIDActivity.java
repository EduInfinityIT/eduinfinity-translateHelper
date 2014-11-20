package com.eduinfinity.dimu.translatehelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eduinfinity.dimu.translatehelper.R;
import com.eduinfinity.dimu.translatehelper.adapter.Center;

public class SetTXIDActivity extends Activity {
    EditText name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_txid);
        initView();
    }

    private void initView() {
        name = (EditText) findViewById(R.id.editText_name);
        password = (EditText) findViewById(R.id.editText_password);
        findViewById(R.id.button_complete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Center.getInstance().setIDAndPassWord(name.getText().toString().trim(), password.getText().toString().trim());
                SetTXIDActivity.this.finish();
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTXIDActivity.this.finish();
            }
        });

    }


}
