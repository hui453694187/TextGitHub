package com.example.securitysystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frame.MenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MaineActivity extends SlidingFragmentActivity {

	private TextView statInfoTv, statInfoTv2;
	private ImageView statImage;

	private Boolean isStartService = false;

	private ListView gonNenLV;
	private List<Map<String, Object>> listData;

	private Button startSecuritBut;

	private SharedPreferences spf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ��ʼ�� �໬�˵�
		initSlidingMenu(savedInstanceState);

		listViewData();

		init();

		event();

		// this.getSlidingMenu().showMenu(); ��ʾ�໬�˵�

	}

	private void listViewData() {
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("imag", R.drawable.sim1);
		map1.put("text1", "SIM������֪ͨ");
		map1.put("text2", "�ֻ��������Ͷ�����������ϵ�˺���");

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("imag", R.drawable.yijianqingchu);
		map2.put("text1", "ɾ������");
		map2.put("text2", "ɾ�������ֻ��绰����ͨѶ��¼���̲��ż���˽����");

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("imag", R.drawable.baojing1);
		map3.put("text1", "������׷��");
		map3.put("text2", "����ָ������ֻ�������������ʾ����׷���ֻ�λ��");
		
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("imag", R.drawable.location);
		map4.put("text1", "�ֻ���λ");
		map4.put("text2", "���ݱ����ֻ���ǰ���룬��ȫ�룬��λ�ֻ���ǰλ�ã�");

		listData.add(map1);
		listData.add(map2);
		listData.add(map3);
		listData.add(map4);

	}

	private void init() {
		// TODO Auto-generated method stub
		gonNenLV = (ListView) this.findViewById(R.id.listView_gongnen);
		SimpleAdapter simPad = new SimpleAdapter(this, listData,
				R.layout.gonnen_listview_layout, new String[] { "imag",
						"text1", "text2" }, new int[] { R.id.image_gongnen,
						R.id.text1, R.id.text2 });
		gonNenLV.setAdapter(simPad);

		startSecuritBut = (Button) this.findViewById(R.id.start_But);
		statImage = (ImageView) this.findViewById(R.id.stat_image);
		statInfoTv = (TextView) this.findViewById(R.id.stat_infoTV);
		statInfoTv2 = (TextView) this.findViewById(R.id.stat_infoTV2);

		// �������� ����״̬
		spf = this.getSharedPreferences("password.xml", this.MODE_PRIVATE);

		isStartService = spf.getBoolean("isStartService", false);
		if (!isStartService) {

			statImage.setImageResource(R.drawable.traffic_error);
			statInfoTv.setText("δ������������");
			// statInfoTv.setTextColor(000000);
			statInfoTv.setTextColor(Color.RED);
			statInfoTv2.setText("���������������ֻ�����ʱ�����һ��ֻ�");
			startSecuritBut.setText("�����ֻ�����");

		} else {

			statImage.setImageResource(R.drawable.duigou);
			statInfoTv.setText("���������ѿ���");
			statInfoTv.setTextColor(Color.GREEN);
			statInfoTv2.setText("�����ֻ����ڱ���״̬");
			startSecuritBut.setText("�ر��ֻ�����");

		}

	}

	private void event() {

		startSecuritBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO ����һ��service ���ֻ����ڷ���״̬
				if (!isStartService) {

					statImage.setImageResource(R.drawable.duigou);
					statInfoTv.setText("���������ѿ���");
					statInfoTv.setTextColor(Color.GREEN);
					statInfoTv2.setText("�����ֻ����ڱ���״̬");
					startSecuritBut.setText("�ر��ֻ�����");
					isStartService = true;

				} else {

					statImage.setImageResource(R.drawable.traffic_error);
					statInfoTv.setText("δ������������");
					// statInfoTv.setTextColor(000000);
					statInfoTv.setTextColor(Color.RED);
					statInfoTv2.setText("���������������ֻ�����ʱ�����һ��ֻ�");
					startSecuritBut.setText("�����ֻ�����");
					isStartService = false;

				}

				Editor editor = spf.edit();
				// ���� �����ȡ״̬
				editor.putBoolean("isStartService", isStartService);
				editor.commit();

			}
		});

		gonNenLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// ��ת������Ķ�Ӧ�Ĺ��ܽ���
				switch (arg2) {
				case 0:
					MaineActivity.this.startActivity(new Intent().setClass(
							MaineActivity.this, SIMChangeNotifiActivity.class));
					break;
				case 1:
					MaineActivity.this.startActivity(new Intent().setClass(
							MaineActivity.this, DeleteDataActivity.class));
					break;
				case 2:
					MaineActivity.this.startActivity(new Intent().setClass(
							MaineActivity.this, Alert_Music_Activity.class));
					break;
				case 3:
					MaineActivity.this.startActivity(new Intent().setClass(
							MaineActivity.this, LocaltionActivity.class));
					break;
				default:
					Toast.makeText(MaineActivity.this, "default",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void initSlidingMenu(Bundle savedInstanceState) {
		// ��������ͼ����
		// tContentView(R.layout.contentfragment);
		// getSupportFragmentManager().beginTransaction().replace(R.id.content,
		// new ContentFragment()).commit();

		// ���û����˵���ͼ����
		this.setBehindContentView(R.layout.emit_framelayout);

		this.getSupportFragmentManager().beginTransaction()
				.replace(R.id.emit_frame, new MenuFragment(this)).commit();
		SlidingMenu slidingMenu = this.getSlidingMenu();
		// ���û����˵�������ֵ

		// �˵��ŵ�λ��
		slidingMenu.setMode(SlidingMenu.RIGHT);
		// ���û����˵�����ģʽ��ֵ
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// ���û�����Ӱ�Ŀ��
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		// ���û�����Ӱ��ͼ����Դ
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		// ���û����˵���ͼ�Ŀ��
		// double
		// offset=this.getWindowManager().getDefaultDisplay().getWidth()*0.2;
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// R.dimen.slidingmenu_offset
		// �ڹ���ʱ���ű���
		slidingMenu.setBehindScrollScale(0f);
		// ���ý��뽥��Ч����ֵ
		slidingMenu.setFadeDegree(0.35f);
		// //���ö���
		// slidingMenu.setBehindCanvasTransformer(mTransformer);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.maine, menu);
		return true;
	}

}
