package com.example.dengw8.androidsrc;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({"HandlerLeak"})
public class UploadFileService extends Service
{
    private static final String ACTION = "com.vk.BTcar.action.NEW_FILE";
    private static final String ACTION_FINISH = "com.vk.BTcar.action.UPLOAD_FINISH";
    public static final String IP = "192.168.4.1";
    public static final int PORT = 9000;
    public static List<Map<String, Object>> data = new ArrayList();

    private final BroadcastReceiver UploadReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context paramAnonymousContext, Intent arg2)
        {
            String type = arg2.getStringExtra("TYPE");
            Map<String, Object> item = new HashMap();
            item.put("TYPE", type);
            synchronized (UploadFileService.data)
            {
                UploadFileService.data.add(item);
                UploadFileService.data.notify();
                return;
            }
        }
    };
    private Socket client;
    DataInputStream input;
    DataOutputStream out;
    private HandleThread thread;
    private String type = null;

    private void initFileInfo(Map<String, Object> paramMap)
    {
        this.type = ((String)paramMap.get("TYPE"));
    }

    private void noticeUploadList(String paramString1, String paramString2)
    {
        Intent localIntent = new Intent("com.vk.BTcar.action.UPLOAD_FINISH");
        localIntent.putExtra("RESULT", paramString1);
        localIntent.putExtra("CON", paramString2);
        sendBroadcast(localIntent);
    }

    public boolean SocketClient(String paramString, int paramInt)
    {
        try
        {
            this.client = new Socket(paramString, paramInt);
            this.out = new DataOutputStream(this.client.getOutputStream());
            this.input = new DataInputStream(this.client.getInputStream());
            revMsg();
            return true;
        }
        catch (IOException exception) {}
        return false;
    }

    public void closeSocket()
    {
        try
        {
            if (this.client != null)
            {
                this.client.close();
                this.out = null;
                this.input = null;
            }
            return;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
    }

    public IBinder onBind(Intent paramIntent)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();
        IntentFilter localIntentFilter = new IntentFilter("com.example.dengw8.androidsrc.action.NEW_FILE");
        registerReceiver(this.UploadReceiver, localIntentFilter);
        this.thread = new HandleThread();
        this.thread.start();
    }

    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(this.UploadReceiver);
        this.thread.requestExit();
        try
        {
            if (this.client != null) {
                this.client.close();
            }
            return;
        }
        catch (IOException localIOException)
        {
            localIOException.printStackTrace();
        }
    }

    public void revMsg()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    for (;;)
                    {
                        String str = UploadFileService.this.input.readUTF();
                        if ((str != null) && (str.length() > 0)) {
                            UploadFileService.this.noticeUploadList("msg", str);
                        }
                    }
                    return;
                }
                catch (Exception localException)
                {
                    System.out.println(localException.toString());
                }
            }
        }).start();
    }

    public boolean sendMsg(String paramString)
    {
        try
        {
            this.out.write(paramString.getBytes());
            this.out.flush();
            return true;
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        return false;
    }

    private class HandleThread
            extends Thread
    {
        private boolean bRun = true;
        private Map<String, Object> cache = null;

        private HandleThread() {}

        public void requestExit()
        {
            this.bRun = false;
            synchronized (UploadFileService.data)
            {
                UploadFileService.data.notify();
                return;
            }
        }

        public void run()
        {
            int k = 0;
            int i = 0;
            for (;;)
            {
                int j = i + 1;
                if (i >= 20) {}
                for (i = k;; i = 1)
                {
                    if (j < 20) {
                        break;
                    }
                    UploadFileService.this.noticeUploadList("end", "0");
                    return;
                    if (!UploadFileService.this.SocketClient("192.168.4.1", 9000)) {
                        break;
                    }
                    UploadFileService.this.noticeUploadList("1", "0");
                    UploadFileService.data.clear();
                }
                UploadFileService.this.noticeUploadList("3", "0");
                try
                {
                    sleep(1000L);
                    i = j;
                }
                catch (InterruptedException localInterruptedException1)
                {
                    localInterruptedException1.printStackTrace();
                    i = j;
                }
            }
            for (;;)
            {
                synchronized (UploadFileService.data)
                {
                    if (UploadFileService.data.size() > 0)
                    {
                        this.cache = ((Map)UploadFileService.data.get(0));
                        if (this.cache != null)
                        {
                            UploadFileService.this.initFileInfo(this.cache);
                            this.cache = null;
                            UploadFileService.data.remove(0);
                            if (!UploadFileService.this.sendMsg(UploadFileService.this.type))
                            {
                                UploadFileService.this.closeSocket();
                                if (!UploadFileService.this.SocketClient("192.168.4.1", 9000)) {
                                    break label274;
                                }
                                UploadFileService.this.noticeUploadList("r", "0");
                            }
                        }
                        label228:
                        if (!this.bRun) {
                            break;
                        }
                        if (i == 1) {
                            continue;
                        }
                        return;
                    }
                }
                try
                {
                    UploadFileService.data.wait();
                    continue;
                    localObject = finally;
                    throw ((Throwable)localObject);
                }
                catch (InterruptedException localInterruptedException2)
                {
                    for (;;)
                    {
                        Log.d("data", "data.wait");
                    }
                }
                label274:
                UploadFileService.this.noticeUploadList("end", "0");
            }
        }
    }
}

