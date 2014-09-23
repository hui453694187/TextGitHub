package com.example.util;

import java.util.List;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SendSMSMessage {

	private String phoneNumber;
	private String smsMessage;
	private Activity senderContext;

	private boolean isSend = false, isRecevie = false,
			isRegisterReceiver = false;
	
	/***
	 * 监听 短信是否发送成功
	 */
	BroadcastReceiver SmsSendBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			switch (this.getResultCode()) {
			case Activity.RESULT_OK:
				isSend = true;
				Log.v("msg", "isSend");
				Toast.makeText(senderContext, "短信发送成功", Toast.LENGTH_SHORT)
						.show();
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				break;
			}

		}

	};

	/***
	 * 监听 短信是否被接收
	 */
	BroadcastReceiver smsRecevie = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			isRecevie = true;
			Log.v("msg", "isRecevie");
			Toast.makeText(senderContext, "对方已经接收了短信", Toast.LENGTH_SHORT)
					.show();

		}

	};

	public SendSMSMessage() {

	}

	public SendSMSMessage(Activity senderContext, String phoneNumber,
			String smsMessage) {
		this.senderContext = senderContext;
		this.phoneNumber = phoneNumber;
		this.smsMessage = smsMessage;
	}

	/***
	 * 发送一条短信
	 */
	public void sendSMS() {
		SmsManager smsManager = SmsManager.getDefault();
		Intent i = new Intent("SENT_SMS_ACTION");// 发送后的 广播
		Intent i2 = new Intent("DELIVERED_SMS_ACTION"); // 接收到信息后的广播

		PendingIntent sentIntent = PendingIntent.getBroadcast(senderContext, 0,
				i, 0);
		PendingIntent DeliverIntent = PendingIntent.getBroadcast(senderContext,
				0, i2, 0);

		if (smsMessage.length() >= 70) {
			// 短信字数大于70，自动分条
			List<String> ms = smsManager.divideMessage(smsMessage);

			for (String str : ms) {
				// 短信发送
				smsManager.sendTextMessage(phoneNumber, null, str, sentIntent,
						DeliverIntent);
			}
		} else {
			smsManager.sendTextMessage(phoneNumber, null, smsMessage,
					sentIntent, DeliverIntent);
		}

	}

	/***
	 * 注册一个 监听 短信是否发送成功的 广播接受者
	 */
	public void smsSendRegisterReceiver() {
		senderContext.registerReceiver(SmsSendBroadcastReceiver,
				new IntentFilter("SENT_SMS_ACTION"));

		senderContext.registerReceiver(smsRecevie, new IntentFilter(
				"DELIVERED_SMS_ACTION"));

		Log.v("msg", "注册广播成功");
		isRegisterReceiver = true;
		Toast.makeText(senderContext, "注册广播成功", Toast.LENGTH_SHORT).show();
	}

	public void unRegisterReceiver() {
		if (isRegisterReceiver) {
			senderContext.unregisterReceiver(SmsSendBroadcastReceiver);
			senderContext.unregisterReceiver(smsRecevie);
		}
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

}
