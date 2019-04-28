package com.example.lenovo_pc.appbluetoothfinal;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Set;

/**
 * Created by lenovo-pc on 8/06/2017.
 */

public class DevicesList extends ListActivity {
    private BluetoothAdapter mBluetoothAdapter2=null;
    static String ADDRESS_MAC=null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBluetoothAdapter2= BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> PairedDevices = mBluetoothAdapter2.getBondedDevices();
        if (PairedDevices.size()>0){
            for (BluetoothDevice device :PairedDevices){String nameBT=device.getName(); String macBT =device.getAddress();
            ArrayBluetooth.add(nameBT+"\n"+macBT);}

        }
        setListAdapter(ArrayBluetooth);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String infogeneral =((TextView)v).getText().toString();
        //Toast.makeText(getApplicationContext(),"Info:"+infogeneral,Toast.LENGTH_LONG).show();
        String aMac = infogeneral.substring(infogeneral.length()-17);
        //Toast.makeText(getApplicationContext(),"aMac:"+aMac,Toast.LENGTH_LONG).show();
        Intent returnMacIntent=new Intent();
        returnMacIntent.putExtra(ADDRESS_MAC,aMac);
        setResult(RESULT_OK,returnMacIntent);
        finish();
    }
}
