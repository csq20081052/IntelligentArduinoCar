package com.example.dengw8.androidsrc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BTcar extends Activity implements OnTouchListener,OnClickListener{
    public static final String STR_UP = "2";
    public static final String STR_BACK = "8";
    public static final String STR_RIGHT = "6";
    public static final String STR_LEFT = "4";
    public static final String STR_STOP = "5";
    public static final String STR_BZ = "3";
    public static final String STR_XJ = "1";

    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
//	private String mConnectedDeviceName = null;
//	 Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

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




    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK ) {
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the window layout
        setContentView(R.layout.blue);

        // liten buttons
        btn_sz = (Button)findViewById(R.id.b_sz);
        btn_sz.setOnClickListener(this);

        btn_con= (Button)findViewById(R.id.b_con);
        btn_con.setOnClickListener(this);

        btn_xj = (Button)findViewById(R.id.b_xj);
        btn_xj.setOnClickListener(this);

        btn_bz= (Button)findViewById(R.id.b_bz);
        btn_bz.setOnClickListener(this);



        btn_stop= (Button)findViewById(R.id.but_stop);
        btn_stop.setOnClickListener(this);


        // touch liten
        btn_up= (Button)findViewById(R.id.but_up);
        btn_back= (Button)findViewById(R.id.but_below);
        btn_left= (Button)findViewById(R.id.but_left);
        btn_right= (Button)findViewById(R.id.but_right);


        btn_up.setOnTouchListener(this);
        btn_back.setOnTouchListener(this);
        btn_left.setOnTouchListener(this);
        btn_right.setOnTouchListener(this);

        getBTadapter();
//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.setDisplayShowTitleEnabled(true);
//		actionBar.setHomeButtonEnabled(true);
//		actionBar.setDisplayHomeAsUpEnabled(false);
//		forceShowOverflowMenu();

        con_text = (TextView)findViewById(R.id.con_text);
        con_text.setText(con_text.getText(), TextView.BufferType.EDITABLE);
        con_text.setOnClickListener(this);

//		setOnLongClickListener(new View.OnLongClickListener() {
//			@Override
//	       public boolean onLongClick(View v) {
//				  con_text.setText("");
//				  Toast.makeText(getApplicationContext(),"清屏", Toast.LENGTH_SHORT).show();
//				  return true;
//			 }
//		 });




        title = (TextView)findViewById(R.id.title);

        scro = (ScrollView)findViewById(R.id.scro);


        disAble();
//		FLAG=true;
//	    CON="tin";
//	    thread = new startCon();
//	    thread.requestStart();
//	    thread.start();
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


    private void getBTadapter() {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }




    private void setupChat() {
        Log.d(TAG, "setupChat()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null)
            mChatService.stop();

        if (thread != null)
            thread.requestExit();

        CON_FLAG=false;
    }

    public void ensureDiscoverable() {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            CON_FLAG=true;
                            CON=null;

                            thread = new startCon();
                            thread.requestStart();
                            thread.start();
                            Able();
                            title.setText("connected");
                            con_text.setText("");
//					setStatus(getString(R.string.title_connected_to,mConnectedDeviceName));
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//					setStatus(R.string.title_connecting);
                            title.setText("connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//					setStatus(R.string.title_not_connected);
                            title.setText("disconnect");
                            disAble();
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
//				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//				Toast.makeText(getApplicationContext(),
//						"Connected to " + mConnectedDeviceName,
//						Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Editable text = (Editable)con_text.getText();
                    text.append(readMessage);
                    text.append("\n");
                    con_text.setText(text);
                    scro.fullScroll(ScrollView.FOCUS_DOWN);

                    break;
                case -1:

                    break;

            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
//			address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.but_below:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_BACK;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                }
                break;
            case R.id.but_up:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_UP;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                }
                break;
            case R.id.but_left:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_LEFT;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                }
                break;
            case R.id.but_right:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    CON=STR_RIGHT;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    CON=null;
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                }
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()){
            case R.id.b_xj:
//          		mChatService.write("Vibrate:1;".getBytes());
                if(CON_FLAG){
                    mChatService.write(STR_XJ.getBytes());
                    mChatService.write(STR_XJ.getBytes());
//                	 con_text.setText("");
                }
                break;
            case R.id.b_bz:
//          		mChatService.write("Preset2;".getBytes());
                if(CON_FLAG){
//             		   con_text.setText("");
                    mChatService.write(STR_BZ.getBytes());
                    mChatService.write(STR_BZ.getBytes());
                }
                break;
            case R.id.but_stop:
//          		mChatService.write("PowerOff;".getBytes());
                if(CON_FLAG){
                    con_text.setText("");
                    mChatService.write(STR_STOP.getBytes());
                    mChatService.write(STR_STOP.getBytes());
                }
                break;
            case R.id.b_con:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                break;
            case R.id.b_sz:
//           		mChatService.write("Vibrate:20;".getBytes());
                final EditText et = new EditText(BTcar.this);
                new AlertDialog.Builder(BTcar.this)
                        .setTitle("Modify obstacle avoidance parameters")
                        .setView(et)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int whichButton){
                                String input = "CMD"+et.getText().toString();
                                mChatService.write(input.getBytes());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();


                break;
            case R.id.con_text:
                con_text.setText("");
                Toast.makeText(getApplicationContext(),"clear", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void sendToCar(String data) {
        mChatService.write(data.getBytes());
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
                        Log.d("ffffffffffffffffffffff", CON);
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
