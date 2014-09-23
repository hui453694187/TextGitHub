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
		// 初始化 侧滑菜单
		initSlidingMenu(savedInstanceState);

		listViewData();

		init();

		event();

		// this.getSlidingMenu().showMenu(); 显示侧滑菜单

	}

	private void listViewData() {
		listData = new ArrayList<Map<String, Object>>();

		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("imag", R.drawable.sim1);
		map1.put("text1", "SIM卡更换通知");
		map1.put("text2", "手机换卡后发送短信至紧急联系人号码");

		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("imag", R.drawable.yijianqingchu);
		map2.put("text1", "删除数据");
		map2.put("text2", "删除被盗手机电话本，通讯记录，短彩信及隐私数据");

		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("imag", R.drawable.baojing1);
		map3.put("text1", "报警音追踪");
		map3.put("text2", "发送指令到被盗手机，开启报警提示音！追踪手机位置");
		
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("imag", R.drawable.location);
		map4.put("text1", "手机定位");
		map4.put("text2", "根据被盗手机当前号码，安全码，定位手机当前位置！");

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

		// 用来保存 服务状态
		spf = this.getSharedPreferences("password.xml", this.MODE_PRIVATE);

		isStartService = spf.getBoolean("isStartService", false);
		if (!isStartService) {

			statImage.setImageResource(R.drawable.traffic_error);
			statInfoTv.setText("未开启防盗保护");
			// statInfoTv.setTextColor(000000);
			statInfoTv.setTextColor(Color.RED);
			statInfoTv2.setText("开启防盗保护，手机被盗时帮你找回手机");
			startSecuritBut.setText("开启手机防盗");

		} else {

			statImage.setImageResource(R.drawable.duigou);
			statInfoTv.setText("防盗保护已开启");
			statInfoTv.setTextColor(Color.GREEN);
			statInfoTv2.setText("您的手机处于保护状态");
			startSecuritBut.setText("关闭手机防盗");

		}

	}

	private void event() {

		startSecuritBut.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO 开启一个service 让手机处于防盗状态
				if (!isStartService) {

					statImage.setImageResource(R.drawable.duigou);
					statInfoTv.setText("防盗保护已开启");
					statInfoTv.setTextColor(Color.GREEN);
					statInfoTv2.setText("您的手机处于保护状态");
					startSecuritBut.setText("关闭手机防盗");
					isStartService = true;

				} else {

					statImage.setImageResource(R.drawable.traffic_error);
					statInfoTv.setText("未开启防盗保护");
					// statInfoTv.setTextColor(000000);
					statInfoTv.setTextColor(Color.RED);
					statInfoTv2.setText("开启防盗保护，手机被盗时帮你找回手机");
					startSecuritBut.setText("开启手机防盗");
					isStartService = false;

				}

				Editor editor = spf.edit();
				// 保存 服务存取状态
				editor.putBoolean("isStartService", isStartService);
				editor.commit();

			}
		});

		gonNenLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 跳转到点击的对应的功能界面
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
		// 设置主视图界面
		// tContentView(R.layout.contentfragment);
		// getSupportFragmentManager().beginTransaction().replace(R.id.content,
		// new ContentFragment()).commit();

		// 设置滑动菜单视图界面
		this.setBehindContentView(R.layout.emit_framelayout);

		this.getSupportFragmentManager().beginTransaction()
				.replace(R.id.emit_frame, new MenuFragment(this)).commit();
		SlidingMenu slidingMenu = this.getSlidingMenu();
		// 设置滑动菜单的属性值

		// 菜单放的位置
		slidingMenu.setMode(SlidingMenu.RIGHT);
		// 设置滑动菜单触摸模式的值
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		// 设置滑动阴影的宽度
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动阴影的图像资源
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		// double
		// offset=this.getWindowManager().getDefaultDisplay().getWidth()*0.2;
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// R.dimen.slidingmenu_offset
		// 在滚动时缩放比例
		slidingMenu.setBehindScrollScale(0f);
		// 设置渐入渐出效果的值
		slidingMenu.setFadeDegree(0.35f);
		// //设置动画
		// slidingMenu.setBehindCanvasTransformer(mTransformer);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.maine, menu);
		return true;
	}

}
