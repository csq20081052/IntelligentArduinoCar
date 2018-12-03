package com.example.dengw8.androidsrc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
public class MainActivity extends Activity implements OnTouchListener,OnClickListener {
    public static final String STR_UP = "2";
    public static final String STR_BACK = "8";
    public static final String STR_RIGHT = "6";
    public static final String STR_LEFT = "4";
    public static final String STR_STOP = "5";
    public static final String STR_BZ = "3";
    public static final String STR_XJ = "1";

    private static final String ACTION = "com.vk.BTcar.action.NEW_FILE";
    private static final String ACTION_FINISH = "com.vk.BTcar.action.UPLOAD_FINISH";

    private Button btn_up;
    private Button btn_back;
    private Button btn_left;
    private Button btn_right;
    private Button btn_stop;

    private Button btn_xj;
    private Button btn_bz;
    private Button btn_sz;
    private Button btn_con;



    private boolean CON_FLAG=false;
    private volatile  String   CON=null;
    private static int         rate=50;
    private startCon thread;
    TextView  con_text,title;
    ScrollView  scro;


    private int js=0;
    String revdata;
    private volatile Socket client;
    private volatile OutputStream out;
    private volatile InputStream input;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK ) {
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //	8888
    public static final String IP = "192.168.4.1";
    public static final int PORT = 9000;

    public void SocketClient() {

        Thread background = new Thread(new Runnable() {
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;
                String msg;
                try {

                    client = new Socket(IP, PORT);
                    out =  client.getOutputStream();
                    input =client.getInputStream();

                    Message msg1=new Message();
                    msg1.what=0;
                    handler.sendMessage(msg1);
                } catch (Exception e) {
                    Message msg1=new Message();
                    msg1.what=-1;
                    handler.sendMessage(msg1);
                }

                if(client!=null){
                    try {
                        while (true) {
//	                          msg = input.readUTF();
                            bytes = input.read(buffer);
                            msg = new String(buffer, 0, bytes);
                            if (msg != null && msg.length() > 0){
                                revdata=msg;
                                Message msg1=new Message();
                                msg1.what=1;
                                handler.sendMessage(msg1);
                            }
                        }
                    } catch (Exception e) {

                        Message msg1=new Message();
                        msg1.what=-2;
                        handler.sendMessage(msg1);
                    }
                }
            }
        });
        background.start();

    }


    public void sendMsg(String msg) {

        try {
            out.write((msg).getBytes());
            out.flush();

        } catch (Exception e) {
            Message msg1=new Message();
            msg1.what=-2;
            handler.sendMessage(msg1);

        }

    }

    public void closeSocket() {
        try {
            if (client != null){
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        out = null;
        input=null;
        CON_FLAG=false;

        if (thread != null)
            thread.requestExit();
    }


    public  boolean isConn(){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isConn()){
            SocketClient();

        }else{
            Toast.makeText(getApplicationContext(),"No network connection", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (client != null){
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        out = null;
        input=null;
        CON_FLAG=false;

        if (thread != null)
            thread.requestExit();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//		unregisterReceiver(this.UploadList);
//
//		Intent serviceIntent = new Intent();
//        serviceIntent.setAction("com.vk.BTcar.service");
//        stopService(serviceIntent);

//        unbindService(serviceIntent);

        try {
            if (client != null){
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        out = null;
        input=null;
        CON_FLAG=false;

        if (thread != null)
            thread.requestExit();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi);

        // liten buttons
        btn_xj = (Button)findViewById(R.id.w_xj);
        btn_xj.setOnClickListener(this);

        btn_bz= (Button)findViewById(R.id.w_bz);
        btn_bz.setOnClickListener(this);

        btn_sz = (Button)findViewById(R.id.w_sz);
        btn_sz.setOnClickListener(this);

        btn_con= (Button)findViewById(R.id.w_con);
        btn_con.setOnClickListener(this);



        btn_stop= (Button)findViewById(R.id.w_stop);
        btn_stop.setOnClickListener(this);


        // touch liten
        btn_up= (Button)findViewById(R.id.w_up);
        btn_back= (Button)findViewById(R.id.w_below);
        btn_left= (Button)findViewById(R.id.w_left);
        btn_right= (Button)findViewById(R.id.w_right);


        btn_up.setOnTouchListener(this);
        btn_back.setOnTouchListener(this);
        btn_left.setOnTouchListener(this);
        btn_right.setOnTouchListener(this);

        con_text = (TextView)findViewById(R.id.w_text);
        con_text.setText(con_text.getText(), TextView.BufferType.EDITABLE);
        con_text.setOnClickListener(this);
//				setOnLongClickListener(new View.OnLongClickListener() {
//					@Override
//			       public boolean onLongClick(View v) {
//						  con_text.setText("");
//						  Toast.makeText(getApplicationContext(),"清屏", Toast.LENGTH_SHORT).show();
//						  return true;
//					 }
//				 });
        title = (TextView)findViewById(R.id.w_title);
        scro = (ScrollView)findViewById(R.id.w_scro);


        disAble();


//		IntentFilter filter = new IntentFilter(ACTION_FINISH);
//		registerReceiver(this.UploadList, filter);
//
//		Intent serviceIntent = new Intent();
//		serviceIntent.setAction("com.vk.BTcar.service");
//		bindService(serviceIntent, new ServiceConnection() {
//
//			@Override
//			public void onServiceDisconnected(ComponentName name) {
//
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service) {
//
//			}
//		}, Context.BIND_AUTO_CREATE);
//		startService(serviceIntent);
    }



    private final BroadcastReceiver UploadList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str=intent.getStringExtra("RESULT");

            if(str.equals("1")){
                Able();
                CON_FLAG=true;
                CON=null;

                thread = new startCon();
                thread.requestStart();
                thread.start();
                title.setText("connected");
            }else if(str.equals("end")){
                str="Device is not connected, please exit app！";
                Toast.makeText(getApplicationContext(),str, Toast.LENGTH_SHORT).show();
                disAble();

//	        		Intent serviceIntent = new Intent();
//	                serviceIntent.setAction("com.remote.carsocket");
//	                stopService(serviceIntent);
//
//	        		if (thread != null)
//	        			thread.requestExit();
//
//	        		CON_FLAG=false;

                finish();
            }else if(str.equals("r")){
                Toast.makeText(getApplicationContext(),"conected", Toast.LENGTH_SHORT).show();
            }else if(str.equals("3")){
                js++;
                if(js==5){
                    js=1;
                }
                String s=repeat(".",js);
                title.setText("connecting"+s);
            }else if(str.equals("msg")){
                str=intent.getStringExtra("CON");

                Editable text = (Editable)con_text.getText();
                text.append(str);
                text.append("\n");
                con_text.setText(text);
                scro.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }
    };

    public static String repeat(String src, int num) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < num; i++)
            s.append(src);
        return s.toString();
    }

    private void disAble(){
        btn_stop.setEnabled(false);
        btn_up.setEnabled(false);
        btn_back.setEnabled(false);
        btn_left.setEnabled(false);
        btn_right.setEnabled(false);

        btn_bz.setEnabled(false);
        btn_xj.setEnabled(false);
        btn_sz.setEnabled(false);

        btn_con.setEnabled(true);
    }

    private void Able(){
        btn_stop.setEnabled(true);
        btn_up.setEnabled(true);
        btn_back.setEnabled(true);
        btn_left.setEnabled(true);
        btn_right.setEnabled(true);

        btn_bz.setEnabled(true);
        btn_xj.setEnabled(true);
        btn_sz.setEnabled(true);
        btn_con.setEnabled(false);
    }




    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.w_below:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_BACK;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                }
                break;
            case R.id.w_up:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_UP;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                }
                break;
            case R.id.w_left:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_LEFT;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                }
                break;
            case R.id.w_right:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_RIGHT;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                }
                break;

            default:
                break;
        }
        return false;
    }


    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.w_xj:
                if(CON_FLAG){
                    sendMsg(STR_XJ);
                    sendMsg(STR_XJ);
//             	    con_text.setText("");
                }
                break;
            case R.id.w_bz:
                if(CON_FLAG){
                    sendMsg(STR_BZ);
                    sendMsg(STR_BZ);
//             	    con_text.setText("");
                }
                break;
            case R.id.w_stop:
                if(CON_FLAG){
                    sendMsg(STR_STOP);
                    sendMsg(STR_STOP);
                    con_text.setText("");
                }
                break;
            case R.id.w_sz:

                final EditText et = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Modify obstacle avoidance parameters")
                        .setView(et)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int whichButton){
                                String input = "CMD"+et.getText().toString();
                                sendMsg(input);
                            }
                        })
                        .setNegativeButton("cancel", null)
                        .show();
                break;
            case R.id.w_con:
                title.setText("connecting");
                SocketClient();
                break;
            case R.id.w_text:
                con_text.setText("");
                Toast.makeText(getApplicationContext(),"clear", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    Editable text = (Editable)con_text.getText();
                    text.append(revdata);
                    text.append("\n");
                    con_text.setText(text);
                    scro.fullScroll(ScrollView.FOCUS_DOWN);

                    break;
                case 0:
                    Able();
                    CON_FLAG=true;
                    CON=null;

                    thread = new startCon();
                    thread.requestStart();
                    thread.start();
                    title.setText("connected");
                    con_text.setText("");
                    break;
                case -1:

                    String str="Device not connected, exit app";
                    Toast.makeText(getApplicationContext(),str, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -2:
                    title.setText("disconnect");
                    closeSocket();
//				SocketClient();
                    String str1="Connection disconnect";
                    Toast.makeText(getApplicationContext(),str1, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.option_menu1, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//
//		switch (item.getItemId()) {
//		case R.id.wifi_connect_scan:
//			title.setText("正在连接");
//			SocketClient();
//		 	return true;
//		}
//		return false;
//	}


    private void sendToCar(String data) {
        sendMsg(data);
    }

    private class startCon extends Thread{
        public boolean FALG;
        public void requestExit(){
            FALG=false;

        }
        public void requestStart(){
            FALG = true;
        }
        public void run() {

            while(FALG){
                try{
                    if(CON!=null){
                        sendToCar(CON);

                    }
                }catch(Exception ex){
                }

                try {
                    Thread.sleep(rate);
                } catch (InterruptedException e1) {
                }
            }


        }
    }



}
