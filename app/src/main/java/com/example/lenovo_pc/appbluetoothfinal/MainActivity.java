package com.example.lenovo_pc.appbluetoothfinal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {

    boolean conec=false;
    Button btnConecctionB,btnLed1B,btnLed2B,btnLed3B;
    BluetoothAdapter mBlueAdapter=null;
    BluetoothDevice mBlueDevice=null;
    BluetoothSocket mBlueSocket=null;
    UUID M_UUID =UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static final int SOLICITUDE_ACTIV =1;
    private static final int SOLICITUDE_CONEC =2;
    private static final int MESSAGE_READ =3;
    ConnectedThread connectedThread;
    android.os.Handler mHandler;
    StringBuilder dataBlue = new StringBuilder();
    private static String MAC=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConecctionB =(Button)findViewById(R.id.btnConecction);
        btnLed1B =(Button)findViewById(R.id.btnLed1);
        btnLed2B =(Button)findViewById(R.id.btnLed2);
        btnLed3B =(Button)findViewById(R.id.btnLed3);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBlueAdapter==null){  Toast.makeText(getApplicationContext(), "No found BlueT", Toast.LENGTH_LONG).show();}

        else if (!mBlueAdapter.isEnabled()){
            Intent activeBluetoothIntent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
         startActivityForResult(activeBluetoothIntent,SOLICITUDE_ACTIV);}
        btnConecctionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (conec) {
                    try {
                        mBlueSocket.close();
                        conec = false;
                        // btnConecctionB.append("asdas");
                        btnConecctionB.setText("Click CONNECT");
                        Toast.makeText(getApplicationContext(), " BlueT was disconnected", Toast.LENGTH_LONG).show();
                    } catch (IOException error) {
                        Toast.makeText(getApplicationContext(), "errorListener BT:" + error, Toast.LENGTH_LONG).show();
                    }


                } else {
                    Intent seeList = new Intent(MainActivity.this, DevicesList.class);
                    startActivityForResult(seeList, SOLICITUDE_CONEC);
                }

            }
            });



        btnLed1B.setOnClickListener(new View.OnClickListener() {
                 public void  onClick(View view){
                     if (conec){ connectedThread.sendData("led1");}
                     else {Toast.makeText(getApplicationContext(), "Blue wasnot conect!!", Toast.LENGTH_LONG).show();}


            }
        });

mHandler = new android.os.Handler(){
    @Override
    public void handleMessage(Message msg) {
        if (msg.what==MESSAGE_READ){
            String reciv =(String)msg.obj;
            dataBlue.append(reciv);

            int lastdata =dataBlue.indexOf("}");

            if(lastdata>0){String datacompleted= dataBlue.substring(0,lastdata);
        int sizedata = datacompleted.length();

  if (dataBlue.charAt(0)=='{') {
    String datafinish =dataBlue.substring(1,sizedata);
    Log.d("Recive final",datafinish);
      if (datafinish.contains("l1on")){btnLed1B.setText("LED1 On");  Log.d("led1","led on");}
      else if (datafinish.contains("l1off")){btnLed1B.setText("Led1 off");  Log.d("led1","off");}
  }



            }

        }
    }
    };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case SOLICITUDE_ACTIV:
                if(resultCode== Activity.RESULT_OK){Toast.makeText(getApplicationContext(), "Blue was activated", Toast.LENGTH_LONG).show();}
                else { Toast.makeText(getApplicationContext(), "Blue wasnot activated", Toast.LENGTH_LONG).show(); finish();}
                break;

            case SOLICITUDE_CONEC:

                if(resultCode==Activity.RESULT_OK){

                    MAC=data.getExtras().getString(DevicesList.ADDRESS_MAC);
                    Toast.makeText(getApplicationContext(), "MAC Selected:"+MAC, Toast.LENGTH_LONG).show();
                   mBlueDevice=mBlueAdapter.getRemoteDevice(MAC);

                try { mBlueSocket = mBlueDevice.createRfcommSocketToServiceRecord(M_UUID);
                    mBlueSocket.connect();
                       conec=true;
                    Toast.makeText(getApplicationContext(), "device was Connected", Toast.LENGTH_LONG).show();
                    connectedThread = new ConnectedThread(mBlueSocket);
                    connectedThread.start();
                    btnConecctionB.setText("Click Disc");
                }catch (IOException error){
                    conec=false;
                    Toast.makeText(getApplicationContext(),"device was not connected:"+error, Toast.LENGTH_LONG).show();}
                }

                else {Toast.makeText(getApplicationContext(), "i cant get MAC from Intent", Toast.LENGTH_LONG).show();}




        }
    }

///////////////
private class ConnectedThread extends Thread {

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConnectedThread(BluetoothSocket socket) {
        mBlueSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()


        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                String databt = new String(buffer,0,bytes);
               //  Send the obtained bytes to the UI activity
               mHandler.obtainMessage(MESSAGE_READ, bytes, -1, databt).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void sendData(String mData) {                                        //public void write(byte[] bytes) {
        byte [] msgBuffer=mData.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) { }
    }

}




    ////////////

}
