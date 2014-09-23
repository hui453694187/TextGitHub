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
	 * ɾ����ť������
	 */
	private Drawable mClearDrawable;
	
	private Paint mPaint; 
	/**
	 * �ؼ��Ƿ��н���
	 */
	private boolean hasFoucs;

	public MyEditText(Context context, AttributeSet attrs) {
		// ���ﹹ�췽��Ҳ����Ҫ����������ܶ����Բ�����XML���涨��
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
		// ��ȡEditText��DrawableRight,����û���������Ǿ�ʹ��Ĭ�ϵ�ͼƬ
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			// throw new
			// NullPointerException("You can add drawableRight attribute in XML");
			mClearDrawable = getResources().getDrawable(
					R.drawable.column_edit_item_del);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		// Ĭ����������ͼ��
		setClearIconVisible(false);
		// ���ý���ı�ļ���
		setOnFocusChangeListener(this);
		// ����������������ݷ����ı�ļ���
		addTextChangedListener(this);
	}

	/**
	 * event.getX() ��ȡ���Ӧ�������Ͻǵ�X����
	 * event.getY()   ��ȡ���Ӧ�������Ͻǵ�Y���� 
	 * getWidth()  ��ȡ�ؼ��Ŀ��
	 * getTotalPaddingRight()  ��ȡɾ��ͼ�����Ե���ؼ��ұ�Ե�ľ��� 
	 * getPaddingRight() ��ȡɾ��ͼ���ұ�Ե���ؼ��ұ�Ե�ľ��� 
	 * getWidth() - getTotalPaddingRight() ����ɾ��ͼ�����Ե���ؼ����Ե�ľ���
	 * getWidth() - getPaddingRight() ����ɾ��ͼ���ұ�Ե���ؼ����Ե�ľ���
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mClearDrawable != null & event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {
				//�жϴ������Ƿ���ˮƽ��Χ��
				boolean isInnerWidth  = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				//��ȡɾ��ͼ��ı߽磬����һ��Rect����
				Rect rect = mClearDrawable.getBounds();
               int height = rect.height();
               int y = (int) event.getY();
             //����ͼ��ײ����ؼ��ײ��ľ���
               int distance = (getHeight() - height) /2;
             //�жϴ������Ƿ�����ֱ��Χ��(���ܻ��е����)
               //���������������distance����distance+ͼ������ĸ߶ȣ�֮�ڣ�����Ϊ����ɾ��ͼ��
               boolean isInnerHeight = (y > distance) && (y < (distance + height));
				if (isInnerWidth && isInnerHeight) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * �������ͼ�����ʾ�����أ�����setCompoundDrawablesΪEditText������ȥ
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
	 * ��������������ݷ����仯��ʱ��ص��ķ���
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
	 * ��ClearEditText���㷢���仯��ʱ���ж������ַ��������������ͼ�����ʾ������
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
      //������   
      //canvas.drawLine(0,this.getHeight()-1,  this.getWidth()-1, this.getHeight()-1, mPaint); 
	}

}
