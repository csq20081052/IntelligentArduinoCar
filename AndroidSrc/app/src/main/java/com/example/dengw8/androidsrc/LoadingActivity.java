package com.example.dengw8.androidsrc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class LoadingActivity extends Activity
{
    private Button but_blue;
    private Button but_wifi;

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_loading);

        but_wifi = findViewById(R.id.but_wifi);
        but_wifi.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent paramAnonymousView = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(paramAnonymousView);
            }
        });

        but_blue = findViewById(R.id.but_blue);
        but_blue.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                Intent paramAnonymousView = new Intent(LoadingActivity.this, BTcar.class);
                startActivity(paramAnonymousView);
            }
        });
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
    {
        if (paramInt == 4)
        {
            finish();
            return true;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }
}
