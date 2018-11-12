package com.example.dengw8.androidsrc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivity extends Activity
{
    private static final boolean D = true;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private static final String TAG = "DeviceListActivity";
    private BluetoothAdapter mBtAdapter;
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
            DeviceListActivity.this.mBtAdapter.cancelDiscovery();
            paramAnonymousAdapterView = ((TextView)paramAnonymousView).getText().toString();
            paramAnonymousAdapterView = paramAnonymousAdapterView.substring(paramAnonymousAdapterView.length() - 17);
            paramAnonymousView = new Intent();
            paramAnonymousView.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, paramAnonymousAdapterView);
            DeviceListActivity.this.setResult(-1, paramAnonymousView);
            DeviceListActivity.this.finish();
        }
    };
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
            paramAnonymousContext = paramAnonymousIntent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(paramAnonymousContext))
            {
                paramAnonymousContext = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (paramAnonymousContext.getBondState() != 12) {
                    DeviceListActivity.this.mNewDevicesArrayAdapter.add(paramAnonymousContext.getName() + "\n" + paramAnonymousContext.getAddress());
                }
            }
            do
            {
                do
                {
                    return;
                } while (!"android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(paramAnonymousContext));
                DeviceListActivity.this.setProgressBarIndeterminateVisibility(false);
                DeviceListActivity.this.setTitle(2131034123);
            } while (DeviceListActivity.this.mNewDevicesArrayAdapter.getCount() != 0);
            paramAnonymousContext = DeviceListActivity.this.getResources().getText(2131034125).toString();
            DeviceListActivity.this.mNewDevicesArrayAdapter.add(paramAnonymousContext);
        }
    };

    private void doDiscovery()
    {
        Log.d("DeviceListActivity", "doDiscovery()");
        setProgressBarIndeterminateVisibility(true);
        setTitle(2131034122);
        findViewById(2131230740).setVisibility(0);
        if (this.mBtAdapter.isDiscovering()) {
            this.mBtAdapter.cancelDiscovery();
        }
        this.mBtAdapter.startDiscovery();
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        requestWindowFeature(5);
        setContentView(2130903042);
        setResult(0);
        ((Button)findViewById(2131230742)).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View paramAnonymousView)
            {
                DeviceListActivity.this.doDiscovery();
                paramAnonymousView.setVisibility(8);
            }
        });
        this.mPairedDevicesArrayAdapter = new ArrayAdapter(this, 2130903043);
        this.mNewDevicesArrayAdapter = new ArrayAdapter(this, 2130903043);
        paramBundle = (ListView)findViewById(2131230739);
        paramBundle.setAdapter(this.mPairedDevicesArrayAdapter);
        paramBundle.setOnItemClickListener(this.mDeviceClickListener);
        paramBundle = (ListView)findViewById(2131230741);
        paramBundle.setAdapter(this.mNewDevicesArrayAdapter);
        paramBundle.setOnItemClickListener(this.mDeviceClickListener);
        paramBundle = new IntentFilter("android.bluetooth.device.action.FOUND");
        registerReceiver(this.mReceiver, paramBundle);
        paramBundle = new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        registerReceiver(this.mReceiver, paramBundle);
        this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        paramBundle = this.mBtAdapter.getBondedDevices();
        if (paramBundle.size() > 0)
        {
            findViewById(2131230738).setVisibility(0);
            paramBundle = paramBundle.iterator();
            for (;;)
            {
                if (!paramBundle.hasNext()) {
                    return;
                }
                BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramBundle.next();
                this.mPairedDevicesArrayAdapter.add(localBluetoothDevice.getName() + "\n" + localBluetoothDevice.getAddress());
            }
        }
        paramBundle = getResources().getText(2131034124).toString();
        this.mPairedDevicesArrayAdapter.add(paramBundle);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if (this.mBtAdapter != null) {
            this.mBtAdapter.cancelDiscovery();
        }
        unregisterReceiver(this.mReceiver);
    }
}
