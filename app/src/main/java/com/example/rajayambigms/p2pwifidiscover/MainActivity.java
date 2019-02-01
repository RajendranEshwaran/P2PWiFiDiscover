package com.example.rajayambigms.p2pwifidiscover;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private BroadcastReceiver receiver;
    private WifiManager wifi;


    private Button wifiOn, wifiDis, msgBtn;
    private ListView listView;

    private ArrayList<String> arrayList = new ArrayList<>();
    private List<ScanResult> results;
    private ArrayAdapter adapter;
    private String[] wifiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiOn = (Button) findViewById(R.id.wifiBtn);
        wifiDis = (Button) findViewById(R.id.wifiDisBtn);
        listView =(ListView)findViewById(R.id.wifiListView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }

        wifiList = new String[]{"one","two","three"};

        for(int i =0; i< wifiList.length;i++)
        {
            arrayList.add(wifiList[i]);
        }

        wifiOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (wifi.isWifiEnabled()) {
                    wifi.setWifiEnabled(false);
                    wifiOn.setText("WifiOFF");
                } else if (!wifi.isWifiEnabled()) {
                    wifi.setWifiEnabled(true);
                    wifiOn.setText("WifiON");
                }
            }
        });


        wifiDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWifi();

            }
        });
        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(!wifi.isWifiEnabled())
        {
            wifi.setWifiEnabled(true);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(adapter);

        scanWifi();

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            results = wifi.getScanResults();
            unregisterReceiver(this);

            for (ScanResult scanResult : results) {
                if(scanResult.SSID != null)
                    arrayList.add(scanResult.SSID);

                System.out.println("rajaywifi" + arrayList.toString());
                adapter.notifyDataSetChanged();
            }

        }};


    public void scanWifi() {
        arrayList.clear();
        registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();
        Toast.makeText(this,"Scanning...WiFi",Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("wifierror", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }

}

