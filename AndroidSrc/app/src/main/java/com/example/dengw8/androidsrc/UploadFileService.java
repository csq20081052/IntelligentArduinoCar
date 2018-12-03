package com.example.dengw8.androidsrc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

@SuppressLint("HandlerLeak")
public class UploadFileService extends Service {
    private static final String ACTION = "com.vk.BTcar.action.NEW_FILE";
    private static final String ACTION_FINISH = "com.vk.BTcar.action.UPLOAD_FINISH";
    private HandleThread thread;
    static public List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private String type = null;
    private Socket client;
    DataOutputStream out;
    DataInputStream input;



    //	8888
    public static final String IP = "192.168.4.1";
    public static final int PORT = 9000;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(ACTION);
        registerReceiver(this.UploadReceiver, filter);

        thread = new HandleThread();
        thread.start();
    }

    public boolean SocketClient(String site, int port) {
        boolean res;
        try {
            client = new Socket(site, port);
            out =  new DataOutputStream(client.getOutputStream());
            input = new DataInputStream(client.getInputStream());
            res = true;
            revMsg();

        } catch (IOException e) {

            res = false;

        }

        return res;
    }

    public void revMsg(){
        Thread background = new Thread(new Runnable() {
            public void run() {
                String msg;
                try {
                    while (true) {
                        msg = input.readUTF();
                        if (msg != null && msg.length() > 0)
                            noticeUploadList("msg",msg);
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        });
        background.start();
    }

    public boolean sendMsg(String msg) {
        boolean flag;
        try {
            out.write((msg).getBytes());
            out.flush();
            flag=true;
        } catch (Exception e) {
            e.printStackTrace();
            flag=false;
        }

        return flag;
    }

    public void closeSocket() {
        try {
            if (client != null){
                client.close();
                out = null;
                input=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.UploadReceiver);
        thread.requestExit();
        try {
            if (client != null)
                client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver UploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("TYPE");

            Map<String, Object> item = new HashMap<String, Object>();
            item.put("TYPE", path);

            synchronized (data) {
                data.add(item);
                data.notify();
            }
        }
    };

    private void initFileInfo(Map<String, Object> cache) {
        type = (String) cache.get("TYPE");
    }

    private void noticeUploadList(String str, String con) {

        Intent intent1 = new Intent(ACTION_FINISH);
        intent1.putExtra("RESULT", str);
        intent1.putExtra("CON", con);
        sendBroadcast(intent1);

    }

    private class HandleThread extends Thread {

        private Map<String, Object> cache = null;
        private boolean bRun = true;

        public void requestExit() {
            bRun = false;
            synchronized (data) {
                data.notify();
            }
        }

        public void run() {
            int i = 0, s_flag = 0;
            while (i++ < 20) {
                if (SocketClient(IP, PORT)) {
                    noticeUploadList("1", "0");
                    data.clear();
                    s_flag = 1;
                    break;
                }
                noticeUploadList("3","0");
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(i>=20){

                noticeUploadList("end","0");

                return;
            }
            while (bRun && s_flag == 1) {

                synchronized (data) {
                    if (data.size() > 0) {
                        cache = data.get(0);
                    } else {
                        try {
                            data.wait();
                        } catch (InterruptedException e) {
                            Log.d("data", "data.wait");
                        }
                        continue;
                    }
                }
                if (cache != null) {
                    initFileInfo(cache);
                    cache = null;
                    data.remove(0);
                    boolean res = sendMsg(type);
                    if (!res) {
                        closeSocket();

                        if (SocketClient(IP, PORT)) {
//							noticeUploadList("1");
//							data.clear();
                            noticeUploadList("r","0");
                        }else{
                            noticeUploadList("end","0");
                        }

                    }
                }
            }

        }
    }
}