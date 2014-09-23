package com.example.frame;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.securitysystem.R;
import com.example.util.SendSMSMessage;

import controls.ClearEditText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuFragment extends Fragment {

	private View view, diaologView, passModifiView;
	private ClearEditText safeNumberEdt, oldPasswordEdt, newPasswordEdt1,
			newPasswordEdt2;
	private TextView simLinear, safeLinera, passLinear;
	private ImageView simImage;

	private final int RESULT_CODE = 0;
	private boolean isListenSIM=false;
	
	private Button addNumber, saveNumber, modifiPassOkBut,
			modifiPassCansoleBut;
	private Activity context;

	private SendSMSMessage sendSms;
	
	private Builder dialog, passModiDialog;
	private AlertDialog alerDialog,passAlertDialog;

	private SharedPreferences spfSaveNumber, Spfpass;

	public MenuFragment() {
	}

	public MenuFragment(Activity context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.setting_layout, null);

		simLinear = (TextView) view.findViewById(R.id.sim_TV);
		safeLinera = (TextView) view.findViewById(R.id.safeNumber_linear);
		passLinear = (TextView) view.findViewById(R.id.password_linear);

		simImage=(ImageView)view.findViewById(R.id.sim_Image);
		
		sendSms=new SendSMSMessage(context,"",
				"���,����ֻ��Ѿ���������Ϊ��ȫ���룬��Э�������ֻ����ⶪʧʱ�һ��ֻ�");
		
		spfSaveNumber = context.getSharedPreferences("saveNumber",
				Context.MODE_PRIVATE);
		Spfpass = context.getSharedPreferences("password.xml",
				Context.MODE_PRIVATE);
		
		isListenSIM=Spfpass.getBoolean("isListenSIM", false);

		if(isListenSIM)
			simImage.setImageResource(R.drawable.toggle_open);
		else
			simImage.setImageResource(R.drawable.guan);
		
		
		event();


		return view;
	}

	private void event() {
		simImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(!isListenSIM){
					simImage.setImageResource(R.drawable.toggle_open);
					isListenSIM=true;
				}else{
					simImage.setImageResource(R.drawable.guan);
					isListenSIM=false;
				}
				
				Spfpass.edit().putBoolean("isListenSIM",isListenSIM).commit();
				
				
				
			}
		});
		
		
		simLinear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(context, "sim", 1).show();
			}
		});

		safeLinera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog = new Builder(context);
				diaologView = LayoutInflater.from(context).inflate(
						R.layout.modifi_sefa_number_dialog, null);
				addNumber = (Button) diaologView
						.findViewById(R.id.add_safe_numberBut);
				saveNumber = (Button) diaologView
						.findViewById(R.id.save_numberBut);
				safeNumberEdt = (ClearEditText) diaologView
						.findViewById(R.id.safe_numberEdt_modifi);

				dialog.setView(diaologView);

				addNumber.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {

						Intent i = new Intent(); // ��ת��ͨѶ¼����
						i.setAction(Intent.ACTION_PICK);
						i.setData(ContactsContract.Contacts.CONTENT_URI);
						MenuFragment.this.startActivityForResult(i, 0);


					}
				});

				saveNumber.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO ���浽 XML �ļ� �� ����֪ͨ����
						System.out.println("save onClick"); 
						
						String safeNumber = safeNumberEdt.getText().toString().trim();
						safeNumber.replace(" ","");
						safeNumber.replace("-","");
						// ƥ��һ��������ʽ
						Pattern pat = Pattern.compile("^[1][3-8]+\\d{9}");
						boolean isPhoneNumber = pat.matcher(safeNumber)
								.find();
						System.out.println(safeNumber+"--:"+isPhoneNumber);
						if (isPhoneNumber) {
							Toast.makeText(context, "true", 1).show();
							System.out.println("true");
							if (spfSaveNumber.edit()
									.putString("safeNumber", safeNumber)
									.commit()) {
								sendSms.setPhoneNumber(safeNumber);
								
								//ע��㲥
								sendSms.smsSendRegisterReceiver();
								//���Ͷ���
								sendSms.sendSMS();
								
								
								Toast.makeText(context,
										"����ɹ����������Ͷ��ŵ�������ϵ��֪ͨ�����óɹ�", 1).show();
								alerDialog.dismiss();

							}else{
								Toast.makeText(context, "����ʧ��", 1).show();
								System.out.println("����ʧ��");
							}
							
						} else {
							Toast.makeText(context, "������Ĳ����ֻ�����", 1).show();
							System.out.println("���ֻ�����");
						}
					}
				});

				alerDialog = dialog.show();

			}
		});

		// �޸�����
		passLinear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				passModiDialog = new Builder(context);
				passModifiView = LayoutInflater.from(context).inflate(
						R.layout.modifi_password_dialog, null);
				passModiDialog.setView(passModifiView);
				oldPasswordEdt = (ClearEditText) passModifiView
						.findViewById(R.id.Old_passwork_Modifi);
				newPasswordEdt1 = (ClearEditText) passModifiView
						.findViewById(R.id.New1_passwork_Modifi);
				newPasswordEdt2 = (ClearEditText) passModifiView
						.findViewById(R.id.New2_passwork_Modifi);

				modifiPassOkBut = (Button) passModifiView
						.findViewById(R.id.passwork_ModifiOkBut);
				modifiPassCansoleBut = (Button) passModifiView
						.findViewById(R.id.passwork_ModifiCansolBut);
				passAlertDialog = passModiDialog.show();
				modifiPassEven();
				
			}
		});

	}

	private void modifiPassEven() {
		modifiPassOkBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String password = Spfpass.getString("password", null);
				String oldPassword = oldPasswordEdt.getText().toString();
				String newPassWord = newPasswordEdt1.getText().toString();
				String newPassWord2 = newPasswordEdt2.getText().toString();
				Editor passEdit = Spfpass.edit();
				if (newPassWord.equals(newPassWord2)) {
					if (password == null) {
						saveNewPassWord(passEdit, newPassWord);
					} else if (password.equals(oldPassword)) {
						saveNewPassWord(passEdit, newPassWord);
					}else{
						Toast.makeText(context, "ԭ�������", 1).show();
					}
				} else {
					Toast.makeText(context, "�������벻��ͬ", 1).show();
				}
			}

			private void saveNewPassWord(Editor passEdit, String passWord) {
				boolean result=false;
				if (passWord.trim().length() > 0) {
					result=passEdit.putString("password", passWord).commit();
				}
				if(result){
					Toast.makeText(context, "�޸ĳɹ�", 1).show();
				}else{
					Toast.makeText(context, "�޸�ʧ��", 1).show();
				}
				
				passAlertDialog.dismiss();
				
			}

		});
		modifiPassCansoleBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				passAlertDialog.dismiss();
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		System.out.println("onActivityResult:" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			System.out.println("resultCode:" + resultCode);
			ContentResolver reContentResolverol = context.getContentResolver();
			Uri uri = data.getData();
			@SuppressWarnings("deprecation")
			Cursor cursor = context.managedQuery(uri, null, null, null, null);
			cursor.moveToFirst();
			String userName = cursor
					.getString( // ��ȡ��ϵ�˵�����
					cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			// ��ȡ��ϵ�˵�ΨһId
			String userId = cursor.getString(cursor
					.getColumnIndex(BaseColumns._ID));
			Cursor cursorPhone = reContentResolverol.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ userId, null, null);
			String phoneNumber = "";
			cursorPhone.moveToFirst();
			phoneNumber = cursorPhone
					.getString(cursorPhone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			// text.setText(phoneNumber+" ("+username+")");

System.out.println("number:" + phoneNumber);
			safeNumberEdt.setText(phoneNumber);

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		sendSms.unRegisterReceiver();
		
	}
	
	
	
	
	

}
