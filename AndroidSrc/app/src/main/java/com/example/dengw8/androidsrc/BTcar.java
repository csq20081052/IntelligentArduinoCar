package com.example.dengw8.androidsrc;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import java.lang.reflect.Field;

public class BTcar extends Activity
        implements View.OnTouchListener, View.OnClickListener
{
    private static final boolean D = true;
    public static final String DEVICE_NAME = "device_name";
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    static final int REQUEST_ENABLE_BT = 3;
    public static final String STR_BACK = "8";
    public static final String STR_BZ = "3";
    public static final String STR_LEFT = "4";
    public static final String STR_RIGHT = "6";
    public static final String STR_STOP = "5";
    public static final String STR_UP = "2";
    public static final String STR_XJ = "1";
    private static final String TAG = "BluetoothChat";
    public static final String TOAST = "toast";
    private static int rate = 100;
    private volatile String CON = null;
    private boolean CON_FLAG = false;
    private Button btn_back;
    private Button btn_bz;
    private Button btn_con;
    private Button btn_left;
    private Button btn_right;
    private Button btn_stop;
    private Button btn_sz;
    private Button btn_up;
    private Button btn_xj;
    TextView con_text;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private String mConnectedDeviceName = null;
    private final Handler mHandler = new Handler()
    {
        public void handleMessage(Message paramAnonymousMessage)
        {
            switch (paramAnonymousMessage.what)
            {
                case 3:
                default:
                    return;
                case 1:
                    switch (paramAnonymousMessage.arg1)
                    {
                        default:
                            return;
                        case 0:
                        case 1:
                            BTcar.this.title.setText("没有连接");
                            BTcar.this.disAble();
                            return;
                        case 3:
                            BTcar.this.CON_FLAG = true;
                            BTcar.this.CON = null;
                            BTcar.this.thread = new BTcar.startCon(BTcar.this, null);
                            BTcar.this.thread.requestStart();
                            BTcar.this.thread.start();
                            BTcar.this.Able();
                            BTcar.this.title.setText("已经连接");
                            BTcar.this.con_text.setText("");
                            return;
                    }
                    BTcar.this.title.setText("正在连接");
                    return;
                case 4:
                    BTcar.this.mConnectedDeviceName = paramAnonymousMessage.getData().getString("device_name");
                    return;
                case 5:
                    Toast.makeText(BTcar.this.getApplicationContext(), paramAnonymousMessage.getData().getString("toast"), 0).show();
                    return;
            }
            paramAnonymousMessage = new String((byte[])paramAnonymousMessage.obj, 0, paramAnonymousMessage.arg1);
            Editable localEditable = (Editable)BTcar.this.con_text.getText();
            localEditable.append(paramAnonymousMessage);
            localEditable.append("\n");
            BTcar.this.con_text.setText(localEditable);
            BTcar.this.scro.fullScroll(130);
        }
    };
    ScrollView scro;
    private startCon thread;
    TextView title;

    private void Able()
    {
        this.btn_stop.setEnabled(true);
        this.btn_up.setEnabled(true);
        this.btn_back.setEnabled(true);
        this.btn_left.setEnabled(true);
        this.btn_right.setEnabled(true);
        this.btn_bz.setEnabled(true);
        this.btn_xj.setEnabled(true);
        this.btn_sz.setEnabled(true);
        this.btn_con.setEnabled(false);
    }

    private void connectDevice(Intent paramIntent, boolean paramBoolean)
    {
        paramIntent = paramIntent.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        paramIntent = this.mBluetoothAdapter.getRemoteDevice(paramIntent);
        this.mChatService.connect(paramIntent, paramBoolean);
    }

    private void disAble()
    {
        this.btn_stop.setEnabled(false);
        this.btn_up.setEnabled(false);
        this.btn_back.setEnabled(false);
        this.btn_left.setEnabled(false);
        this.btn_right.setEnabled(false);
        this.btn_bz.setEnabled(false);
        this.btn_xj.setEnabled(false);
        this.btn_sz.setEnabled(false);
        this.btn_con.setEnabled(true);
    }

    private void forceShowOverflowMenu()
    {
        try
        {
            ViewConfiguration localViewConfiguration = ViewConfiguration.get(this);
            Field localField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (localField != null)
            {
                localField.setAccessible(true);
                localField.setBoolean(localViewConfiguration, false);
            }
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    private void getBTadapter()
    {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available", 1).show();
            finish();
        }
    }

    private void sendToCar(String paramString)
    {
        this.mChatService.write(paramString.getBytes());
    }

    private void setupChat()
    {
        Log.d("BluetoothChat", "setupChat()");
        this.mChatService = new BluetoothChatService(this, this.mHandler);
    }

    public void ensureDiscoverable()
    {
        Log.d("BluetoothChat", "ensure discoverable");
        if (this.mBluetoothAdapter.getScanMode() != 23)
        {
            Intent localIntent = new Intent("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
            localIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 300);
            startActivity(localIntent);
        }
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
        Log.d("BluetoothChat", "onActivityResult " + paramInt2);
        switch (paramInt1)
        {
            case 2:
            default:
            case 1:
                do
                {
                    return;
                } while (paramInt2 != -1);
                connectDevice(paramIntent, true);
                return;
        }
        if (paramInt2 == -1)
        {
            setupChat();
            return;
        }
        Log.d("BluetoothChat", "BT not enabled");
        Toast.makeText(this, 2131034118, 0).show();
        finish();
    }

    public void onClick(final View paramView)
    {
        switch (paramView.getId())
        {
            case 2131230727:
            case 2131230730:
            case 2131230731:
            case 2131230733:
            case 2131230734:
            case 2131230735:
            case 2131230736:
            default:
            case 2131230729:
            case 2131230728:
            case 2131230732:
                do
                {
                    do
                    {
                        do
                        {
                            return;
                        } while (!this.CON_FLAG);
                        this.mChatService.write("1".getBytes());
                        this.mChatService.write("1".getBytes());
                        return;
                    } while (!this.CON_FLAG);
                    this.mChatService.write("3".getBytes());
                    this.mChatService.write("3".getBytes());
                    return;
                } while (!this.CON_FLAG);
                this.con_text.setText("");
                this.mChatService.write("5".getBytes());
                this.mChatService.write("5".getBytes());
                return;
            case 2131230725:
                startActivityForResult(new Intent(this, DeviceListActivity.class), 1);
                return;
            case 2131230726:
                paramView = new EditText(this);
                new AlertDialog.Builder(this).setTitle("修改规避参数").setView(paramView).setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        paramAnonymousDialogInterface = "CMD" + paramView.getText().toString();
                        BTcar.this.mChatService.write(paramAnonymousDialogInterface.getBytes());
                    }
                }).setNegativeButton("取消", null).show();
                return;
        }
        this.con_text.setText("");
        Toast.makeText(getApplicationContext(), "清屏", 0).show();
    }

    public void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(2130903041);
        this.btn_sz = ((Button)findViewById(2131230726));
        this.btn_sz.setOnClickListener(this);
        this.btn_con = ((Button)findViewById(2131230725));
        this.btn_con.setOnClickListener(this);
        this.btn_xj = ((Button)findViewById(2131230729));
        this.btn_xj.setOnClickListener(this);
        this.btn_bz = ((Button)findViewById(2131230728));
        this.btn_bz.setOnClickListener(this);
        this.btn_stop = ((Button)findViewById(2131230732));
        this.btn_stop.setOnClickListener(this);
        this.btn_up = ((Button)findViewById(2131230733));
        this.btn_back = ((Button)findViewById(2131230731));
        this.btn_left = ((Button)findViewById(2131230735));
        this.btn_right = ((Button)findViewById(2131230734));
        this.btn_up.setOnTouchListener(this);
        this.btn_back.setOnTouchListener(this);
        this.btn_left.setOnTouchListener(this);
        this.btn_right.setOnTouchListener(this);
        getBTadapter();
        this.con_text = ((TextView)findViewById(2131230737));
        this.con_text.setText(this.con_text.getText(), TextView.BufferType.EDITABLE);
        this.con_text.setOnClickListener(this);
        this.title = ((TextView)findViewById(2131230730));
        this.scro = ((ScrollView)findViewById(2131230736));
        disAble();
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (this.mChatService != null) {
            this.mChatService.stop();
        }
        if (this.thread != null) {
            this.thread.requestExit();
        }
        this.CON_FLAG = false;
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

    public void onResume()
    {
        try
        {
            super.onResume();
            if ((this.mChatService != null) && (this.mChatService.getState() == 0)) {
                this.mChatService.start();
            }
            return;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public void onStart()
    {
        super.onStart();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
        while (this.mChatService != null) {
            return;
        }
        setupChat();
    }

    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
        switch (paramView.getId())
        {
        }
        for (;;)
        {
            return false;
            if (paramMotionEvent.getAction() == 0) {
                this.CON = "8";
            }
            if (paramMotionEvent.getAction() == 1)
            {
                this.CON = null;
                continue;
                if (paramMotionEvent.getAction() == 0) {
                    this.CON = "2";
                }
                if (paramMotionEvent.getAction() == 1)
                {
                    this.CON = null;
                    continue;
                    if (paramMotionEvent.getAction() == 0) {
                        this.CON = "4";
                    }
                    if (paramMotionEvent.getAction() == 1)
                    {
                        this.CON = null;
                        continue;
                        if (paramMotionEvent.getAction() == 0) {
                            this.CON = "6";
                        }
                        if (paramMotionEvent.getAction() == 1) {
                            this.CON = null;
                        }
                    }
                }
            }
        }
    }

    private class startCon
            extends Thread
    {
        public boolean FALG;

        private startCon() {}

        public void requestExit()
        {
            this.FALG = false;
        }

        public void requestStart()
        {
            this.FALG = true;
        }

        public void run()
        {
            for (;;)
            {
                if (!this.FALG) {
                    return;
                }
                try
                {
                    if (BTcar.this.CON != null) {
                        BTcar.this.sendToCar(BTcar.this.CON);
                    }
                    try
                    {
                        Thread.sleep(BTcar.rate);
                    }
                    catch (InterruptedException localInterruptedException) {}
                }
                catch (Exception localException)
                {
                    for (;;) {}
                }
            }
        }
    }
}

