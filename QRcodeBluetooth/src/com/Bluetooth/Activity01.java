package com.Bluetooth;

import com.Bluetooth.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Activity01 extends Activity
{
	/* ȡ��Ĭ�ϵ����������� */
	private BluetoothAdapter	_bluetooth				= BluetoothAdapter.getDefaultAdapter();

	/* ��������� */
	private static final int	REQUEST_ENABLE			= 0x1;
	/* �����ܹ������� */
	private static final int	REQUEST_DISCOVERABLE	= 0x2;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}


	/* �������� */
	public void onEnableButtonClicked(View view)
	{
		// �û����������
		//Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		//startActivityForResult(enabler, REQUEST_ENABLE);
		//������
		_bluetooth.enable();
	}


	/* �ر����� */
	public void onDisableButtonClicked(View view)
	{

		_bluetooth.disable();
	}




	/* �ͻ��� */
	public void onOpenClientSocketButtonClicked(View view)
	{

		Intent enabler = new Intent(this, ClientSocketActivity.class);
		startActivity(enabler);
	}


	/* ����� */
	public void onOpenServerSocketButtonClicked(View view)
	{

		Intent enabler = new Intent(this, ServerSocketActivity.class);
		startActivity(enabler);
	}
}

