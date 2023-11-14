package com.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class ClientSocketActivity  extends Activity
{
	private static final String TAG = "ClientTagName";
	public static final int STATE = 0;
	public static final int STATE_NONE = 1;       // we're doing nothing  
	public static final int STATE_LISTEN = 2;     // now listening for incoming connections  
	public static final int STATE_CONNECTING = 3; // now initiating an outgoing connection  
	public static final int STATE_CONNECTED = 4;  // now connected to a remote device  
	public static final int MESSAGE = 1;
	public static final int MESSAGE_WRITE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int CAMERA_OPEN = 3;
	private static final int REQUEST_SCAN = 1;;
	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	private ConnectThread mConnectThread;  
	private ConnectedThread mConnectedThread;
	private BluetoothDevice device;
	private int mState;
	
	//Identify field
	int IdentifyProcess = 0;
	String IDw = "";
	String IDm = "";	
	String Kw = "";
	String Km = "";
	String Km1 = "";
	String R1 = "";
	String R2 = "";
	String A = "";
	String B = "";
	String C = "";
	String K2 = "";
	String M1 = "";
	String S = "";
	String R_ = "";
	
	public Handler mHandler = new Handler(){
		public void handleMessage (Message msg){
		            switch (msg.what) {
		            case MESSAGE_WRITE:
		                byte[] writeBuf = (byte[]) msg.obj;
		                // construct a string from the buffer
		                String writeMessage = new String(writeBuf);
		                Toast toast=Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_SHORT);
		                if(IdentifyProcess == 2){
		                	clientIdentify();
		                }
		                break;
		            case MESSAGE_READ:
		                byte[] readBuf = (byte[]) msg.obj;
		                // construct a string from the valid bytes in the buffer
		                String readMessage = new String(readBuf, 0, msg.arg1);
		                toast=Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT);
		                break;
		            case CAMERA_OPEN:
		            	if(IdentifyProcess == 1){
		            		Intent QRIntent = new Intent(ClientSocketActivity.this, com.zxing.activity.CaptureActivity.class);
		            		Toast.makeText(ClientSocketActivity.this, "scan the wd qrcode", Toast.LENGTH_LONG).show();
		            		startActivityForResult(QRIntent, REQUEST_SCAN);
		            	}
		            	if(IdentifyProcess == 2){
		            		Intent QRIntent = new Intent(ClientSocketActivity.this, com.zxing.activity.CaptureActivity.class);
			                Toast.makeText(ClientSocketActivity.this, "wait wd tell then scan qrcode", Toast.LENGTH_LONG).show();
			            	startActivityForResult(QRIntent, REQUEST_SCAN);
			            }
		            }
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.client_socket);
		
		/*获取设备的IMEI码*/
		TelephonyManager telephonyManager2 = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei=telephonyManager2.getDeviceId();
		IDm = imei;
		
		
		int IDtest = DataConverse.stringToInt(IDm);
		
		String IDtest2 = DataConverse.intToString(IDtest);
		
		
		
		if(Km.equals(""))
		{
		Km = DataConverse.RandomNumber();
		}
		mState = STATE_LISTEN;
		
		if (!mAdapter.isEnabled()) {
			mAdapter.enable();
		}
		/*扫描二维码*/
		Intent QRIntent = new Intent(this, com.zxing.activity.CaptureActivity.class);
		
		startActivityForResult(QRIntent, REQUEST_SCAN);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (requestCode != REQUEST_SCAN) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
		
		String resultString = data.getStringExtra("result");
//		final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		switch(IdentifyProcess){
		case 0:
			long createqrcodebefore = System.currentTimeMillis();
			String[] ary1 = resultString.split("'");
			String addressString = ary1[0];
			
			IDw = ary1[1];

			final BluetoothDevice device = mAdapter.getRemoteDevice(addressString);
			
			long createqrcodeafter = System.currentTimeMillis();
    		long createqrcodetime = createqrcodeafter - createqrcodebefore;
    		Toast toast=Toast.makeText(getApplicationContext(), "DQRT is："+Long.toString(createqrcodetime)+"ms", Toast.LENGTH_SHORT); 
    		toast.show();
    		Log.d(TAG, "DQRT is："+Long.toString(createqrcodetime)+"ms");
			
		
			new Thread() {
				public void run() {
					/* 连接 */
					connect(device);
				};
			}.start();
			IdentifyProcess++;//1
			
			break;
		case 1:
			long createqrcodebefore2 = System.currentTimeMillis();
			String[] ary2 = resultString.split("'");
			Kw = ary2[0];
			R1 = ary2[1];
			R2 = ary2[2];
			
			//calculate
			A = DataConverse.getA(Kw, Km, R1);
			B = DataConverse.getB(IDw, IDm, R2);
			C = DataConverse.getC(Kw, Km, R1, R2);
			
			
			String ABC = A;
			ABC += "'";
			ABC += B;
			ABC += "'";
			ABC += C;
			long createqrcodeafter2 = System.currentTimeMillis();
    		long createqrcodetime2 = createqrcodeafter2 - createqrcodebefore2;
    		toast=Toast.makeText(getApplicationContext(), "FDQRTA is："+Long.toString(createqrcodetime2)+"ms", Toast.LENGTH_SHORT); 
    		toast.show();
    		Log.d(TAG, "FDQRTA is："+Long.toString(createqrcodetime2)+"ms");
			
			
			mConnectedThread.write(ABC.getBytes());
			IdentifyProcess++;//2
			break;
		case 2:
			long createqrcodebefore3 = System.currentTimeMillis();
			String[] ary3 = resultString.split("'");
			String digest = ary3[0];
			
			
			S = DataConverse.intToString(DataConverse.stringToInt(Kw) | DataConverse.stringToInt(Km));
			R_ = DataConverse.intToString(DataConverse.stringToInt(R1) | DataConverse.stringToInt(R2));
			
    		M1 += IDw;
    		M1 += "'";
    		M1 += S;
    		M1 += "'";
    		M1 += R_;
    		M1 += "'";
    		M1 += IDm;
    		
    		String digest2 = new SHA1().getDigestOfString(M1.getBytes());
    		
    		if(digest.equals(digest2)){
    			IdentifyProcess++;//3
    			Toast.makeText(ClientSocketActivity.this, "Identify success", Toast.LENGTH_LONG).show();
    			sendMessage("confirm success");
    			finish();
    		}
    		else
    		{
    			Toast.makeText(ClientSocketActivity.this, "Identify fail", Toast.LENGTH_LONG).show();
                sendMessage("confirm fail");
    			shutdownServer();
    		}
    		long createqrcodeafter3 = System.currentTimeMillis();
    		long createqrcodetime3 = createqrcodeafter3 - createqrcodebefore3;
    		toast=Toast.makeText(getApplicationContext(), "SDQRTA is："+Long.toString(createqrcodetime3)+"ms", Toast.LENGTH_SHORT); 
    		toast.show();
    		Log.d(TAG, "SDQRTA is："+Long.toString(createqrcodetime3)+"ms");
		}
	}
	
	public void onButtonClicked(View view) {
		shutdownServer();
	}
	
    public synchronized void shutdownServer() {
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        setState(STATE_NONE);
        
        finish();
    }
	
    private synchronized void setState(int state) {
    	mState = state;
    	Log.d(TAG, "mState is " + mState + " now");
        /*mHandler.obtainMessage(STATE,mState)
        .sendToTarget();*/
    }
    
    public synchronized int getState() {
        return mState;
    }
    
    private void connectionFailed() {
        setState(STATE_LISTEN);
    }
    
	
	public synchronized void connect(BluetoothDevice device) {  
		//if (D) Log.d(TAG, "connect to: " + device);  
		
		 // Cancel any thread attempting to make a connection  
		if (mState == STATE_CONNECTING) {  
			if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}  
		}  
		
		// Cancel any thread currently running a connection  
		if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}  
		 
		// Start the thread to connect with the given device  
		mConnectThread = new ConnectThread(device);  
		mConnectThread.start();  
		setState(STATE_CONNECTING);  
		
		}  
	
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {

            Log.d(TAG, "ConnectThread start");
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                /*// Start the service over to restart listening mode
                BluetoothChatService.this.start();
                return;*/
            }

            // Reset the ConnectThread because we're done
            synchronized (ClientSocketActivity.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
	public synchronized void connected(BluetoothSocket socket) {
		// if (D) Log.d(TAG, "connected");

	    // Cancel the thread that completed the connection
	    if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

	    // Cancel any thread currently running a connection
	    if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

	    // Start the thread to manage the connection and perform transmissions*/
	    	
	    mConnectedThread = new ConnectedThread(socket);
	    mConnectedThread.start();

	         /*// Send the name of the connected device back to the UI Activity
	         Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
	         Bundle bundle = new Bundle();
	         bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
	         msg.setData(bundle);
	         mHandler.sendMessage(msg);*/

	    setState(STATE_CONNECTED);
	    clientIdentify();
	}
    
	private void clientIdentify(){
		mHandler.sendEmptyMessage(CAMERA_OPEN);			
	}
		
    private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

    	public ConnectedThread(BluetoothSocket socket) {
             Log.d(TAG, "create ConnectedThread");
             mmSocket = socket;
             InputStream tmpIn = null;
             OutputStream tmpOut = null;

             // Get the BluetoothSocket input and output streams
             try {
                 tmpIn = socket.getInputStream();
                 tmpOut = socket.getOutputStream();
             } catch (IOException e) {
                 Log.e(TAG, "temp sockets not created", e);
             }

             mmInStream = tmpIn;
             mmOutStream = tmpOut;
         }

         public void run() {
             byte[] buffer = new byte[1024];
             int bytes;

             // Keep listening to the InputStream while connected
             while (true) {
                 try {
                     // Read from the InputStream
                     bytes = mmInStream.read(buffer);

                     // Send the obtained bytes to the UI Activity
                     mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                             .sendToTarget();
                 } catch (IOException e) {
                     Log.e(TAG, "disconnected", e);
                     //connectionLost();
                     break;
                 }
             }
         }
         
         public void write(byte[] buffer) {
             try {
                 mmOutStream.write(buffer);

                 // Share the sent message back to the UI Activity
                 mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                         .sendToTarget();
             } catch (IOException e) {
                 Log.e(TAG, "Exception during write", e);
             }
         }
         
         public void cancel() {
             try {
                 mmSocket.close();
             } catch (IOException e) {
                 Log.e(TAG, "close() of connect socket failed", e);
             }
         }
     }
    
     private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
    	ConnectedThread r;
    	synchronized (this){
    		if (getState() != STATE_CONNECTED) {
    			Toast.makeText(this, "Can't write", Toast.LENGTH_SHORT).show();
    			return;
    		}
    		r = mConnectedThread;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mConnectedThread.write(send);

        }
     }
     private String valuetransfer(String Km,String Km1)
     {
    	 Km = Km1;
    	 return Km;
     }
     
}