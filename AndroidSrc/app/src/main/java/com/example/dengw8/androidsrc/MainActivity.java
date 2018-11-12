package com.example.dengw8.androidsrc;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity
        extends Activity
        implements View.OnTouchListener, View.OnClickListener
{
    private static final String ACTION = "com.vk.BTcar.action.NEW_FILE";
    private static final String ACTION_FINISH = "com.vk.BTcar.action.UPLOAD_FINISH";
    public static final String IP = "192.168.4.1";
    public static final int PORT = 9000;
    public static final String STR_BACK = "8";
    public static final String STR_BZ = "3";
    public static final String STR_LEFT = "4";
    public static final String STR_RIGHT = "6";
    public static final String STR_STOP = "5";
    public static final String STR_UP = "2";
    public static final String STR_XJ = "1";
    private static int rate = 100;
    private volatile String CON = null;
    private boolean CON_FLAG = false;
    private final BroadcastReceiver UploadList = new BroadcastReceiver()
    {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
            paramAnonymousContext = paramAnonymousIntent.getStringExtra("RESULT");
            if (paramAnonymousContext.equals("1"))
            {
                MainActivity.this.Able();
                MainActivity.this.CON_FLAG = true;
                MainActivity.this.CON = null;
                MainActivity.this.thread = new MainActivity.startCon(MainActivity.this, null);
                MainActivity.this.thread.requestStart();
                MainActivity.this.thread.start();
                MainActivity.this.title.setText("������������");
            }
            do
            {
                return;
                if (paramAnonymousContext.equals("end"))
                {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "���������������������������������������", 0).show();
                    MainActivity.this.disAble();
                    MainActivity.this.finish();
                    return;
                }
                if (paramAnonymousContext.equals("r"))
                {
                    Toast.makeText(MainActivity.this.getApplicationContext(), "������������", 0).show();
                    return;
                }
                if (paramAnonymousContext.equals("3"))
                {
                    paramAnonymousContext = MainActivity.this;
                    paramAnonymousContext.js += 1;
                    if (MainActivity.this.js == 5) {
                        MainActivity.this.js = 1;
                    }
                    paramAnonymousContext = MainActivity.repeat(".", MainActivity.this.js);
                    MainActivity.this.title.setText("������������" + paramAnonymousContext);
                    return;
                }
            } while (!paramAnonymousContext.equals("msg"));
            paramAnonymousContext = paramAnonymousIntent.getStringExtra("CON");
            paramAnonymousIntent = (Editable)MainActivity.this.con_text.getText();
            paramAnonymousIntent.append(paramAnonymousContext);
            paramAnonymousIntent.append("\n");
            MainActivity.this.con_text.setText(paramAnonymousIntent);
            MainActivity.this.scro.fullScroll(130);
        }
    };
    private Button btn_back;
    private Button btn_bz;
    private Button btn_con;
    private Button btn_left;
    private Button btn_right;
    private Button btn_stop;
    private Button btn_sz;
    private Button btn_up;
    private Button btn_xj;
    private Socket client;
    TextView con_text;
    private final Handler handler = new Handler()
    {
        public void handleMessage(Message paramAnonymousMessage)
        {
            switch (paramAnonymousMessage.what)
            {
                default:
                    return;
                case 1:
                    paramAnonymousMessage = (Editable)MainActivity.this.con_text.getText();
                    paramAnonymousMessage.append(MainActivity.this.revdata);
                    paramAnonymousMessage.append("\n");
                    MainActivity.this.con_text.setText(paramAnonymousMessage);
                    MainActivity.this.scro.fullScroll(130);
                    return;
                case 0:
                    MainActivity.this.Able();
                    MainActivity.this.CON_FLAG = true;
                    MainActivity.this.CON = null;
                    MainActivity.this.thread = new MainActivity.startCon(MainActivity.this, null);
                    MainActivity.this.thread.requestStart();
                    MainActivity.this.thread.start();
                    MainActivity.this.title.setText("������������");
                    MainActivity.this.con_text.setText("");
                    return;
                case -1:
                    Toast.makeText(MainActivity.this.getApplicationContext(), "������������������������������������", 0).show();
                    MainActivity.this.finish();
                    return;
            }
            MainActivity.this.title.setText("������������");
            MainActivity.this.closeSocket();
            Toast.makeText(MainActivity.this.getApplicationContext(), "������������", 0).show();
        }
    };
    InputStream input;
    private int js = 0;
    OutputStream out;
    String revdata;
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

    public static String repeat(String paramString, int paramInt)
    {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = 0;
        for (;;)
        {
            if (i >= paramInt) {
                return localStringBuffer.toString();
            }
            localStringBuffer.append(paramString);
            i += 1;
        }
    }

    private void sendToCar(String paramString)
    {
        sendMsg(paramString);
    }

    public void SocketClient()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                byte[] arrayOfByte = new byte['��'];
                try
                {
                    MainActivity.this.client = new Socket("192.168.4.1", 9000);
                    MainActivity.this.out = MainActivity.this.client.getOutputStream();
                    MainActivity.this.input = MainActivity.this.client.getInputStream();
                    Object localObject = new Message();
                    ((Message)localObject).what = 0;
                    MainActivity.this.handler.sendMessage((Message)localObject);
                    if (MainActivity.this.client != null) {
                        try
                        {
                            for (;;)
                            {
                                localObject = new String(arrayOfByte, 0, MainActivity.this.input.read(arrayOfByte));
                                if ((localObject != null) && (((String)localObject).length() > 0))
                                {
                                    MainActivity.this.revdata = ((String)localObject);
                                    localObject = new Message();
                                    ((Message)localObject).what = 1;
                                    MainActivity.this.handler.sendMessage((Message)localObject);
                                }
                            }
                            Message localMessage1;
                            return;
                        }
                        catch (Exception localException1)
                        {
                            localMessage1 = new Message();
                            localMessage1.what = -2;
                            MainActivity.this.handler.sendMessage(localMessage1);
                        }
                    }
                }
                catch (Exception localException2)
                {
                    for (;;)
                    {
                        Message localMessage2 = new Message();
                        localMessage2.what = -1;
                        MainActivity.this.handler.sendMessage(localMessage2);
                    }
                }
            }
        }).start();
    }

    public void closeSocket()
    {
        try
        {
            if (this.client != null) {
                this.client.close();
            }
            this.out = null;
            this.input = null;
            this.CON_FLAG = false;
            if (this.thread != null) {
                this.thread.requestExit();
            }
            return;
        }
        catch (Exception localException)
        {
            for (;;)
            {
                localException.printStackTrace();
            }
        }
    }

    public boolean isConn()
    {
        boolean bool = false;
        ConnectivityManager localConnectivityManager = (ConnectivityManager)getApplicationContext().getSystemService("connectivity");
        if (localConnectivityManager.getActiveNetworkInfo() != null) {
            bool = localConnectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return bool;
    }

    public void onClick(final View paramView)
    {
        switch (paramView.getId())
        {
            case 2131230746:
            case 2131230749:
            case 2131230750:
            case 2131230752:
            case 2131230753:
            case 2131230754:
            case 2131230755:
            default:
            case 2131230748:
            case 2131230747:
            case 2131230751:
                do
                {
                    do
                    {
                        do
                        {
                            return;
                        } while (!this.CON_FLAG);
                        sendMsg("1");
                        sendMsg("1");
                        return;
                    } while (!this.CON_FLAG);
                    sendMsg("3");
                    sendMsg("3");
                    return;
                } while (!this.CON_FLAG);
                sendMsg("5");
                sendMsg("5");
                this.con_text.setText("");
                return;
            case 2131230745:
                paramView = new EditText(this);
                new AlertDialog.Builder(this).setTitle("������������������").setView(paramView).setPositiveButton("������", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
                    {
                        paramAnonymousDialogInterface = "CMD" + paramView.getText().toString();
                        MainActivity.this.sendMsg(paramAnonymousDialogInterface);
                    }
                }).setNegativeButton("������", null).show();
                return;
            case 2131230744:
                this.title.setText("������������");
                SocketClient();
                return;
        }
        this.con_text.setText("");
        Toast.makeText(getApplicationContext(), "������", 0).show();
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(2130903044);
        this.btn_xj = ((Button)findViewById(2131230748));
        this.btn_xj.setOnClickListener(this);
        this.btn_bz = ((Button)findViewById(2131230747));
        this.btn_bz.setOnClickListener(this);
        this.btn_sz = ((Button)findViewById(2131230745));
        this.btn_sz.setOnClickListener(this);
        this.btn_con = ((Button)findViewById(2131230744));
        this.btn_con.setOnClickListener(this);
        this.btn_stop = ((Button)findViewById(2131230751));
        this.btn_stop.setOnClickListener(this);
        this.btn_up = ((Button)findViewById(2131230752));
        this.btn_back = ((Button)findViewById(2131230750));
        this.btn_left = ((Button)findViewById(2131230754));
        this.btn_right = ((Button)findViewById(2131230753));
        this.btn_up.setOnTouchListener(this);
        this.btn_back.setOnTouchListener(this);
        this.btn_left.setOnTouchListener(this);
        this.btn_right.setOnTouchListener(this);
        this.con_text = ((TextView)findViewById(2131230756));
        this.con_text.setText(this.con_text.getText(), TextView.BufferType.EDITABLE);
        this.con_text.setOnClickListener(this);
        this.title = ((TextView)findViewById(2131230749));
        this.scro = ((ScrollView)findViewById(2131230755));
        disAble();
    }

    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            if (this.client != null) {
                this.client.close();
            }
            this.out = null;
            this.input = null;
            this.CON_FLAG = false;
            if (this.thread != null) {
                this.thread.requestExit();
            }
            return;
        }
        catch (Exception localException)
        {
            for (;;)
            {
                localException.printStackTrace();
            }
        }
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

    protected void onStart()
    {
        super.onStart();
        if (isConn())
        {
            SocketClient();
            return;
        }
        Toast.makeText(getApplicationContext(), "���������������������", 0).show();
        finish();
    }

    protected void onStop()
    {
        super.onStop();
        try
        {
            if (this.client != null) {
                this.client.close();
            }
            this.out = null;
            this.input = null;
            this.CON_FLAG = false;
            if (this.thread != null) {
                this.thread.requestExit();
            }
            return;
        }
        catch (Exception localException)
        {
            for (;;)
            {
                localException.printStackTrace();
            }
        }
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

    public void sendMsg(String paramString)
    {
        try
        {
            this.out.write(paramString.getBytes());
            this.out.flush();
            return;
        }
        catch (Exception paramString)
        {
            paramString = new Message();
            paramString.what = -2;
            this.handler.sendMessage(paramString);
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
                    if (MainActivity.this.CON != null) {
                        MainActivity.this.sendToCar(MainActivity.this.CON);
                    }
                    try
                    {
                        Thread.sleep(MainActivity.rate);
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
