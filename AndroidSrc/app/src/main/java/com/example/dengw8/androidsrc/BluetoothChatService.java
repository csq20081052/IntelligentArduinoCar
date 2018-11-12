package com.example.dengw8.androidsrc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothChatService
{
    private static final boolean D = true;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static final String NAME_SECURE = "BluetoothChatSecure";
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;
    private static final String TAG = "BluetoothChatService";
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    private AcceptThread mInsecureAcceptThread;
    private AcceptThread mSecureAcceptThread;
    private int mState = 0;

    public BluetoothChatService(Context paramContext, Handler paramHandler)
    {
        this.mHandler = paramHandler;
    }

    private void connectionFailed()
    {
        Message localMessage = this.mHandler.obtainMessage(5);
        Bundle localBundle = new Bundle();
        localBundle.putString("toast", "无法连接蓝牙设备");
        localMessage.setData(localBundle);
        this.mHandler.sendMessage(localMessage);
        start();
    }

    private void connectionLost()
    {
        Message localMessage = this.mHandler.obtainMessage(5);
        Bundle localBundle = new Bundle();
        localBundle.putString("toast", "设备蓝牙连接丢失");
        localMessage.setData(localBundle);
        this.mHandler.sendMessage(localMessage);
        start();
    }

    private void setState(int paramInt)
    {
        try
        {
            Log.d("BluetoothChatService", "setState() " + this.mState + " -> " + paramInt);
            this.mState = paramInt;
            this.mHandler.obtainMessage(1, paramInt, -1).sendToTarget();
            return;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public void connect(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
    {
        try
        {
            Log.d("BluetoothChatService", "connect to: " + paramBluetoothDevice);
            if ((this.mState == 2) && (this.mConnectThread != null))
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            this.mConnectThread = new ConnectThread(paramBluetoothDevice, paramBoolean);
            this.mConnectThread.start();
            setState(2);
            return;
        }
        finally {}
    }

    public void connected(BluetoothSocket paramBluetoothSocket, BluetoothDevice paramBluetoothDevice, String paramString)
    {
        try
        {
            Log.d("BluetoothChatService", "connected, Socket Type:" + paramString);
            if (this.mConnectThread != null)
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            if (this.mSecureAcceptThread != null)
            {
                this.mSecureAcceptThread.cancel();
                this.mSecureAcceptThread = null;
            }
            if (this.mInsecureAcceptThread != null)
            {
                this.mInsecureAcceptThread.cancel();
                this.mInsecureAcceptThread = null;
            }
            this.mConnectedThread = new ConnectedThread(paramBluetoothSocket, paramString);
            this.mConnectedThread.start();
            paramBluetoothSocket = this.mHandler.obtainMessage(4);
            paramString = new Bundle();
            paramString.putString("device_name", paramBluetoothDevice.getName());
            paramBluetoothSocket.setData(paramString);
            this.mHandler.sendMessage(paramBluetoothSocket);
            setState(3);
            return;
        }
        finally {}
    }

    public int getState()
    {
        try
        {
            int i = this.mState;
            return i;
        }
        finally
        {
            localObject = finally;
            throw ((Throwable)localObject);
        }
    }

    public void start()
    {
        try
        {
            Log.d("BluetoothChatService", "start");
            if (this.mConnectThread != null)
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            setState(1);
            if (this.mSecureAcceptThread == null)
            {
                this.mSecureAcceptThread = new AcceptThread(true);
                this.mSecureAcceptThread.start();
            }
            if (this.mInsecureAcceptThread == null)
            {
                this.mInsecureAcceptThread = new AcceptThread(false);
                this.mInsecureAcceptThread.start();
            }
            return;
        }
        finally {}
    }

    public void stop()
    {
        try
        {
            Log.d("BluetoothChatService", "stop");
            if (this.mConnectThread != null)
            {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }
            if (this.mConnectedThread != null)
            {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }
            if (this.mSecureAcceptThread != null)
            {
                this.mSecureAcceptThread.cancel();
                this.mSecureAcceptThread = null;
            }
            if (this.mInsecureAcceptThread != null)
            {
                this.mInsecureAcceptThread.cancel();
                this.mInsecureAcceptThread = null;
            }
            setState(0);
            return;
        }
        finally {}
    }

    public void write(byte[] paramArrayOfByte)
    {
        try
        {
            if (this.mState != 3) {
                return;
            }
            ConnectedThread localConnectedThread = this.mConnectedThread;
            localConnectedThread.write(paramArrayOfByte);
            return;
        }
        finally {}
    }

    private class AcceptThread
            extends Thread
    {
        private String mSocketType;
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(boolean paramBoolean)
        {
            Object localObject = null;
            String str;
            if (paramBoolean) {
                str = "Secure";
            }
            for (;;)
            {
                this.mSocketType = str;
                if (paramBoolean) {}
                try
                {
                    for (this$1 = BluetoothChatService.this.mAdapter.listenUsingRfcommWithServiceRecord("BluetoothChatSecure", BluetoothChatService.MY_UUID_SECURE);; this$1 = BluetoothChatService.this.mAdapter.listenUsingInsecureRfcommWithServiceRecord("BluetoothChatInsecure", BluetoothChatService.MY_UUID_INSECURE))
                    {
                        this.mmServerSocket = BluetoothChatService.this;
                        return;
                        str = "Insecure";
                        break;
                    }
                }
                catch (IOException this$1)
                {
                    for (;;)
                    {
                        Log.e("BluetoothChatService", "Socket Type: " + this.mSocketType + "listen() failed", BluetoothChatService.this);
                        this$1 = (BluetoothChatService)localObject;
                    }
                }
            }
        }

        public void cancel()
        {
            Log.d("BluetoothChatService", "Socket Type" + this.mSocketType + "cancel " + this);
            try
            {
                this.mmServerSocket.close();
                return;
            }
            catch (IOException localIOException)
            {
                Log.e("BluetoothChatService", "Socket Type" + this.mSocketType + "close() of server failed", localIOException);
            }
        }

        public void run()
        {
            Log.d("BluetoothChatService", "Socket Type: " + this.mSocketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + this.mSocketType);
            if (BluetoothChatService.this.mState == 3)
            {
                Log.i("BluetoothChatService", "END mAcceptThread, socket Type: " + this.mSocketType);
                return;
            }
            for (;;)
            {
                try
                {
                    BluetoothSocket localBluetoothSocket1 = this.mmServerSocket.accept();
                    if (localBluetoothSocket1 == null) {
                        break;
                    }
                    synchronized (BluetoothChatService.this)
                    {
                        switch (BluetoothChatService.this.mState)
                        {
                        }
                    }
                }
                catch (IOException localIOException1)
                {
                    Log.e("BluetoothChatService", "Socket Type: " + this.mSocketType + "accept() failed", localIOException1);
                }
                BluetoothChatService.this.connected(localBluetoothSocket2, localBluetoothSocket2.getRemoteDevice(), this.mSocketType);
                continue;
                try
                {
                    localBluetoothSocket2.close();
                }
                catch (IOException localIOException2)
                {
                    Log.e("BluetoothChatService", "Could not close unwanted socket", localIOException2);
                }
            }
        }
    }

    private class ConnectThread
            extends Thread
    {
        private String mSocketType;
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
        {
            this.mmDevice = paramBluetoothDevice;
            Object localObject = null;
            if (paramBoolean) {
                this$1 = "Secure";
            }
            for (;;)
            {
                this.mSocketType = BluetoothChatService.this;
                if (paramBoolean) {}
                try
                {
                    for (this$1 = paramBluetoothDevice.createRfcommSocketToServiceRecord(BluetoothChatService.MY_UUID_SECURE);; this$1 = paramBluetoothDevice.createInsecureRfcommSocketToServiceRecord(BluetoothChatService.MY_UUID_INSECURE))
                    {
                        this.mmSocket = BluetoothChatService.this;
                        return;
                        this$1 = "Insecure";
                        break;
                    }
                }
                catch (IOException this$1)
                {
                    for (;;)
                    {
                        Log.e("BluetoothChatService", "Socket Type: " + this.mSocketType + "create() failed", BluetoothChatService.this);
                        this$1 = (BluetoothChatService)localObject;
                    }
                }
            }
        }

        public void cancel()
        {
            try
            {
                this.mmSocket.close();
                return;
            }
            catch (IOException localIOException)
            {
                Log.e("BluetoothChatService", "close() of connect " + this.mSocketType + " socket failed", localIOException);
            }
        }

        /* Error */
        public void run()
        {
            // Byte code:
            //   0: ldc 52
            //   2: new 54	java/lang/StringBuilder
            //   5: dup
            //   6: ldc 89
            //   8: invokespecial 59	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
            //   11: aload_0
            //   12: getfield 30	com/vk/BTcar/BluetoothChatService$ConnectThread:mSocketType	Ljava/lang/String;
            //   15: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   18: invokevirtual 69	java/lang/StringBuilder:toString	()Ljava/lang/String;
            //   21: invokestatic 93	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
            //   24: pop
            //   25: aload_0
            //   26: new 54	java/lang/StringBuilder
            //   29: dup
            //   30: ldc 94
            //   32: invokespecial 59	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
            //   35: aload_0
            //   36: getfield 30	com/vk/BTcar/BluetoothChatService$ConnectThread:mSocketType	Ljava/lang/String;
            //   39: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   42: invokevirtual 69	java/lang/StringBuilder:toString	()Ljava/lang/String;
            //   45: invokevirtual 97	com/vk/BTcar/BluetoothChatService$ConnectThread:setName	(Ljava/lang/String;)V
            //   48: aload_0
            //   49: getfield 21	com/vk/BTcar/BluetoothChatService$ConnectThread:this$0	Lcom/vk/BTcar/BluetoothChatService;
            //   52: invokestatic 101	com/vk/BTcar/BluetoothChatService:access$0	(Lcom/vk/BTcar/BluetoothChatService;)Landroid/bluetooth/BluetoothAdapter;
            //   55: invokevirtual 107	android/bluetooth/BluetoothAdapter:cancelDiscovery	()Z
            //   58: pop
            //   59: aload_0
            //   60: getfield 42	com/vk/BTcar/BluetoothChatService$ConnectThread:mmSocket	Landroid/bluetooth/BluetoothSocket;
            //   63: invokevirtual 110	android/bluetooth/BluetoothSocket:connect	()V
            //   66: aload_0
            //   67: getfield 21	com/vk/BTcar/BluetoothChatService$ConnectThread:this$0	Lcom/vk/BTcar/BluetoothChatService;
            //   70: astore_1
            //   71: aload_1
            //   72: monitorenter
            //   73: aload_0
            //   74: getfield 21	com/vk/BTcar/BluetoothChatService$ConnectThread:this$0	Lcom/vk/BTcar/BluetoothChatService;
            //   77: aconst_null
            //   78: invokestatic 114	com/vk/BTcar/BluetoothChatService:access$5	(Lcom/vk/BTcar/BluetoothChatService;Lcom/vk/BTcar/BluetoothChatService$ConnectThread;)V
            //   81: aload_1
            //   82: monitorexit
            //   83: aload_0
            //   84: getfield 21	com/vk/BTcar/BluetoothChatService$ConnectThread:this$0	Lcom/vk/BTcar/BluetoothChatService;
            //   87: aload_0
            //   88: getfield 42	com/vk/BTcar/BluetoothChatService$ConnectThread:mmSocket	Landroid/bluetooth/BluetoothSocket;
            //   91: aload_0
            //   92: getfield 26	com/vk/BTcar/BluetoothChatService$ConnectThread:mmDevice	Landroid/bluetooth/BluetoothDevice;
            //   95: aload_0
            //   96: getfield 30	com/vk/BTcar/BluetoothChatService$ConnectThread:mSocketType	Ljava/lang/String;
            //   99: invokevirtual 118	com/vk/BTcar/BluetoothChatService:connected	(Landroid/bluetooth/BluetoothSocket;Landroid/bluetooth/BluetoothDevice;Ljava/lang/String;)V
            //   102: return
            //   103: astore_1
            //   104: aload_0
            //   105: getfield 42	com/vk/BTcar/BluetoothChatService$ConnectThread:mmSocket	Landroid/bluetooth/BluetoothSocket;
            //   108: invokevirtual 82	android/bluetooth/BluetoothSocket:close	()V
            //   111: aload_0
            //   112: getfield 21	com/vk/BTcar/BluetoothChatService$ConnectThread:this$0	Lcom/vk/BTcar/BluetoothChatService;
            //   115: invokestatic 122	com/vk/BTcar/BluetoothChatService:access$4	(Lcom/vk/BTcar/BluetoothChatService;)V
            //   118: return
            //   119: astore_1
            //   120: ldc 52
            //   122: new 54	java/lang/StringBuilder
            //   125: dup
            //   126: ldc 124
            //   128: invokespecial 59	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
            //   131: aload_0
            //   132: getfield 30	com/vk/BTcar/BluetoothChatService$ConnectThread:mSocketType	Ljava/lang/String;
            //   135: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   138: ldc 126
            //   140: invokevirtual 63	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   143: invokevirtual 69	java/lang/StringBuilder:toString	()Ljava/lang/String;
            //   146: aload_1
            //   147: invokestatic 75	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   150: pop
            //   151: goto -40 -> 111
            //   154: astore_2
            //   155: aload_1
            //   156: monitorexit
            //   157: aload_2
            //   158: athrow
            // Local variable table:
            //   start	length	slot	name	signature
            //   0	159	0	this	ConnectThread
            //   103	1	1	localIOException1	IOException
            //   119	37	1	localIOException2	IOException
            //   154	4	2	localObject	Object
            // Exception table:
            //   from	to	target	type
            //   59	66	103	java/io/IOException
            //   104	111	119	java/io/IOException
            //   73	83	154	finally
            //   155	157	154	finally
        }
    }

    private class ConnectedThread
            extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket paramBluetoothSocket, String paramString)
        {
            Log.d("BluetoothChatService", "create ConnectedThread: " + paramString);
            this.mmSocket = paramBluetoothSocket;
            this$1 = null;
            localObject = null;
            try
            {
                paramString = paramBluetoothSocket.getInputStream();
                this$1 = paramString;
                paramBluetoothSocket = paramBluetoothSocket.getOutputStream();
                this$1 = paramString;
            }
            catch (IOException paramBluetoothSocket)
            {
                for (;;)
                {
                    Log.e("BluetoothChatService", "temp sockets not created", paramBluetoothSocket);
                    paramBluetoothSocket = (BluetoothSocket)localObject;
                }
            }
            this.mmInStream = BluetoothChatService.this;
            this.mmOutStream = paramBluetoothSocket;
        }

        public void cancel()
        {
            try
            {
                this.mmSocket.close();
                return;
            }
            catch (IOException localIOException)
            {
                Log.e("BluetoothChatService", "close() of connect socket failed", localIOException);
            }
        }

        public void run()
        {
            Log.i("BluetoothChatService", "BEGIN mConnectedThread");
            byte[] arrayOfByte = new byte['��'];
            try
            {
                for (;;)
                {
                    int i = this.mmInStream.read(arrayOfByte);
                    BluetoothChatService.this.mHandler.obtainMessage(2, i, -1, arrayOfByte).sendToTarget();
                }
                return;
            }
            catch (IOException localIOException)
            {
                Log.e("BluetoothChatService", "disconnected", localIOException);
                BluetoothChatService.this.connectionLost();
                BluetoothChatService.this.start();
            }
        }

        public void write(byte[] paramArrayOfByte)
        {
            try
            {
                this.mmOutStream.write(paramArrayOfByte);
                BluetoothChatService.this.mHandler.obtainMessage(3, -1, -1, paramArrayOfByte).sendToTarget();
                return;
            }
            catch (IOException paramArrayOfByte)
            {
                Log.e("BluetoothChatService", "Exception during write", paramArrayOfByte);
            }
        }
    }
}

