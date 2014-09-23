package controls;

import com.example.securitysystem.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class MyEditText extends EditText implements OnFocusChangeListener,
		TextWatcher {

	/**
	 * 删除按钮的引用
	 */
	private Drawable mClearDrawable;
	
	private Paint mPaint; 
	/**
	 * 控件是否有焦点
	 */
	private boolean hasFoucs;

	public MyEditText(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.editTextStyle);
//		mPaint = new Paint();  
//        
//        mPaint.setStyle(Paint.Style.STROKE);  
//        mPaint.setColor(Color.WHITE); 
	}

	public MyEditText(Context context) {
		this(context, null);
	}

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		// 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			// throw new
			// NullPointerException("You can add drawableRight attribute in XML");
			mClearDrawable = getResources().getDrawable(
					R.drawable.column_edit_item_del);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		// 默认设置隐藏图标
		setClearIconVisible(false);
		// 设置焦点改变的监听
		setOnFocusChangeListener(this);
		// 设置输入框里面内容发生改变的监听
		addTextChangedListener(this);
	}

	/**
	 * event.getX() 获取相对应自身左上角的X坐标
	 * event.getY()   获取相对应自身左上角的Y坐标 
	 * getWidth()  获取控件的宽度
	 * getTotalPaddingRight()  获取删除图标左边缘到控件右边缘的距离 
	 * getPaddingRight() 获取删除图标右边缘到控件右边缘的距离 
	 * getWidth() - getTotalPaddingRight() 计算删除图标左边缘到控件左边缘的距离
	 * getWidth() - getPaddingRight() 计算删除图标右边缘到控件左边缘的距离
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mClearDrawable != null & event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				//判断触摸点是否在水平范围内
				boolean isInnerWidth  = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				//获取删除图标的边界，返回一个Rect对象
				Rect rect = mClearDrawable.getBounds();
               int height = rect.height();
               int y = (int) event.getY();
             //计算图标底部到控件底部的距离
               int distance = (getHeight() - height) /2;
             //判断触摸点是否在竖直范围内(可能会有点误差)
               //触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
               boolean isInnerHeight = (y > distance) && (y < (distance + height));
				if (isInnerWidth && isInnerHeight) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	/**
	 * 当输入框里面内容发生变化的时候回调的方法
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	/**
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
      //画底线   
      //canvas.drawLine(0,this.getHeight()-1,  this.getWidth()-1, this.getHeight()-1, mPaint); 
	}

}
