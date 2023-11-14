package com.Bluetooth;

import com.zxing.encoding.EncodingHandler;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.view.WindowManager;

public class ServerSocketActivity extends ListActivity
{
	private static final String TAG = "SeverTagName";
	/* 一些常量，代表服务器的名称 */
	public static final String PROTOCOL_SCHEME_L2CAP = "btl2cap";
	public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";
	public static final String PROTOCOL_SCHEME_BT_OBEX = "btgoep";
	public static final String PROTOCOL_SCHEME_TCP_OBEX = "tcpobex";
	public static final int STATE = 0;
	public static final int STATE_NONE = 1;       // we're doing nothing  
	public static final int STATE_LISTEN = 2;     // now listening for incoming connections  
	public static final int STATE_CONNECTING = 3; // now initiating an outgoing connection  
	public static final int STATE_CONNECTED = 4;  // now connected to a remote device  
	public static final int MESSAGE = 1;
	public static final int MESSAGE_WRITE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int QRCODE_PRODUCE = 3;
	private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66"); 
    private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    
    //identify field
    int IdentifyProcess = 0;
	
	String IDm = "";	
	String Kw = "";
	String Km = "";
	String Kw1 = "";
	String R1 = "";
	String R2 = "";
	String A = "";
	String B = "";
	String C = "";
	String K2 = "";
	String M1 = "";
	String S = "";
	String R_ = "";
	String IDw = "";
	
	
	private ImageView qrImgImageView;
	private final Handler mHandler = new Handler(){
		public void handleMessage (Message msg){
			switch (msg.what) 
			{
                case STATE:
                	switch (msg.arg1) 
                	{
                	case STATE_CONNECTED:
                		Toast.makeText(ServerSocketActivity.this, "connected", Toast.LENGTH_LONG).show();
                		Log.d(TAG, "state connected");
                		break;
                	case STATE_CONNECTING:
                		Log.d(TAG, "state connecting");
                		break;
                	case STATE_LISTEN:
                		Toast.makeText(ServerSocketActivity.this, "listening", Toast.LENGTH_LONG).show();
                		Log.d(TAG, "state listen");
                	case STATE_NONE:
                		Log.d(TAG, "state none or state listen");
                		break;
                	}
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                Toast toast=Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_SHORT);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                if(IdentifyProcess == 1){
                	String[] ary = readMessage.split("'");
                	A = ary[0];
                	B = ary[1];
                	C = ary[2];
                	severIdentify();
                }
                else if(IdentifyProcess == 2){
                	severIdentify();
                }
                break;
            case QRCODE_PRODUCE:
            	Log.d(TAG, "QR prodeuce, identify = " + IdentifyProcess);
            	long createqrcodebefore = System.currentTimeMillis();
            	String codedInfo = (String) msg.obj;
        		try{
             		Bitmap qrCodeBitmap = EncodingHandler.createQRCode(codedInfo, 300);
        	    	qrImgImageView.setImageBitmap(qrCodeBitmap);
        		}catch (WriterException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		Toast.makeText(ServerSocketActivity.this, "MT please scan the QRcode now", Toast.LENGTH_LONG).show();

        		if(IdentifyProcess == 1){
        			long createqrcodeafter = System.currentTimeMillis();
            		long createqrcodetime = createqrcodeafter - createqrcodebefore;
            		toast=Toast.makeText(getApplicationContext(), "FCQRTA is："+Long.toString(createqrcodetime)+"ms", Toast.LENGTH_SHORT); 
            		toast.show();
            		Log.d(TAG, "FCQRTA is："+Long.toString(createqrcodetime)+"ms");
            		break;
                	
                }
                else if(IdentifyProcess == 2){
                	long createqrcodeafter = System.currentTimeMillis();
            		long createqrcodetime = createqrcodeafter - createqrcodebefore;
            		toast=Toast.makeText(getApplicationContext(), "SCQRTA is："+Long.toString(createqrcodetime)+"ms", Toast.LENGTH_SHORT); 
            		toast.show();
            		Log.d(TAG, "SCQRTA is："+Long.toString(createqrcodetime)+"ms");
            		break;
                	
                }
        		
            
            }
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.server_socket);
		if (!mAdapter.isEnabled()) {
			finish();
			return;
		}
		
		/*获取设备的IMEI码*/
		TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String imei=telephonyManager.getDeviceId();
		IDw = imei;
		if(Kw.equals(""))
		{
		Kw = DataConverse.RandomNumber();
		}
		
		/*得到二维码信息 Name,Address,UUID*/
		String codedInfo = mAdapter.getAddress();
		codedInfo += "'";
		codedInfo += IDw;
		
		/*生成二维码*/
		long createqrcodebefore = System.currentTimeMillis();
		try{
			qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);
     		Bitmap qrCodeBitmap = EncodingHandler.createQRCode(codedInfo, 300);
	    	qrImgImageView.setImageBitmap(qrCodeBitmap);
		}catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long createqrcodeafter = System.currentTimeMillis();
		long createqrcodetime = createqrcodeafter - createqrcodebefore;
		Toast toast=Toast.makeText(getApplicationContext(), "CQRT is："+Long.toString(createqrcodetime)+"ms", Toast.LENGTH_SHORT); 
		toast.show();  

		/* 开始监听 */
		mAcceptThread = new AcceptThread();
		mState = STATE_LISTEN;
		mAcceptThread.start();
	}
	protected void onDestroy() {
		super.onDestroy();
		shutdownServer();
	}
	
    private void setState(int state) {
    	mState = state;
        mHandler.obtainMessage(STATE,mState)
        .sendToTarget();
    }
    
    public synchronized int getState() {
        return mState;
    }
	protected void finalize() throws Throwable {
		super.finalize();
		shutdownServer();
	}
	
	/* 停止服务器 */
    public synchronized void shutdownServer() {
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
        
        finish();
    }
    
	public void onButtonClicked(View view) {
		shutdownServer();
	}
	
	
    private void connectionFailed() {
    	setState(STATE_LISTEN);
    }
    
    private void connectionLost() {
        setState(STATE_LISTEN);
    }
	
	private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
            	//开启监听
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(TAG, MY_UUID);
                
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            
            Log.d(TAG, "AcceptThread start");

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                Log.d(TAG, "state != connected");
            	try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                    Log.d(TAG, "accept() successful");
                } catch (IOException e) {
                    Log.d(TAG, "accept() failed");
                    break;
                }
            
            
            
                // If a connection was accepted
                if (socket != null) {
                	synchronized (ServerSocketActivity.this) {
                    	Log.d(TAG,"enter serversocketactivity");
                		switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                        	
                        	connected(socket);
                            break;
                        case STATE_NONE:
                        	Log.d(TAG,"state none");
                        	
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                        	
                        	try {
                        		socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            	connectionFailed();

                            }
                            break;
                        }
                    }
                }
            }
            //if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            //if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }
     
    public synchronized void connected(BluetoothSocket socket) {
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        /*// Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);*/

        setState(STATE_CONNECTED);
        severIdentify();
        
    }
    
    private void severIdentify(){
    	String Ek1 = "";
    	String M = "";
    	
    	switch(IdentifyProcess){
    	case 0:
    		
    		R1 = DataConverse.RandomNumber();
    		R2 = DataConverse.RandomNumber();
    		Ek1 += Kw;
    		Ek1 += "'";
    		Ek1 += R1;
    		Ek1 += "'";
    		Ek1 += R2;
    		mHandler.obtainMessage(QRCODE_PRODUCE, Ek1)
    		.sendToTarget();
    		IdentifyProcess++;
    		
    		break;
    		
    	case 1:
    		long createqrcodebefore2 = System.currentTimeMillis();
    		Km = DataConverse.getKmFromA(A, Kw, R1);
    		IDm = DataConverse.getIDmFromB(B, IDw, R2);
    		String C_ = DataConverse.getC(Kw, Km, R1, R2);
    		
    		if(C_.equals(C)){
    			K2 = DataConverse.RandomNumber();
    			S = DataConverse.intToString(DataConverse.stringToInt(Kw) | DataConverse.stringToInt(Km));
    			R_ = DataConverse.intToString(DataConverse.stringToInt(R1) | DataConverse.stringToInt(R2));
    			//New Add sentence
    			Kw1 = DataConverse.getKw1(Kw, R1);
        		M1 += IDw;
        		M1 += "'";
        		M1 += S;
        		M1 += "'";
        		M1 += R_;
        		M1 += "'";
        		M1 += IDm;
        		
        		String digest = new SHA1().getDigestOfString(M1.getBytes());
        		
        		M += digest;
        		M += "'";
        		M += K2;
        		mHandler.obtainMessage(QRCODE_PRODUCE, M)
        		.sendToTarget();
        		IdentifyProcess++;
    		}
    		else{
    			
    		}
    		long createqrcodeafter2 = System.currentTimeMillis();
    		long createqrcodetime2 = createqrcodeafter2 - createqrcodebefore2;
    		Toast toast=Toast.makeText(getApplicationContext(), "CCCMACT is："+Long.toString(createqrcodetime2)+"ms", Toast.LENGTH_SHORT); 
    		toast.show();
    		Log.d(TAG, "CCCMACT is："+Long.toString(createqrcodetime2)+"ms");
    		break;
    	case 2:
    		new AlertDialog.Builder(ServerSocketActivity.this)   
    		.setTitle("Confirm")  
    		.setMessage("If MT has passed the authentication？")  
    		.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int whichButton) {
                    Toast.makeText(ServerSocketActivity.this, "Identify success", Toast.LENGTH_LONG).show();
                    setState(STATE_CONNECTED);

    			}
    		})  
    		.setNegativeButton("No", new DialogInterface.OnClickListener() { 
                public void onClick(DialogInterface dialog, int whichButton) { 
                    Toast.makeText(ServerSocketActivity.this, "Identify fail", Toast.LENGTH_LONG).show();
                    shutdownServer();
                    } 
                    })  
    		.show();
    		break;
    	case 3:
    		new AlertDialog.Builder(ServerSocketActivity.this)
    		.setMessage("MT authentication failure")
    		.show();
    		IdentifyProcess++;
    		
    	}
    	
    }
    
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
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
                    connectionLost();
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
            mHandler.obtainMessage(MESSAGE_WRITE, send).sendToTarget();
        }
    }
    private String valuetransfer(String Kw1,String Kw)
    {
    	Kw = Kw1;
    	return Kw;
    }

}


