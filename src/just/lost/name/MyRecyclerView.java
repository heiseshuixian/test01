package just.lost.name;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.AttributeSet;
import android.view.View;

public class MyRecyclerView extends RecyclerView implements OnScrollListener {
	/**
	 * 记录当前第一个View
	 */
	private View mCurrentView;

	private OnItemScrollChangeListener mItemScrollChangeListener;

	public void setOnItemScrollChangeListener(OnItemScrollChangeListener mItemScrollChangeListener) {
		this.mItemScrollChangeListener = mItemScrollChangeListener;
	}

	public interface OnItemScrollChangeListener {
		void onChange(View view, int position);
		void onChangeState(int state);
	}

	public MyRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	public void onScrollStateChanged(int arg0) {
		if (mItemScrollChangeListener != null) {
			mItemScrollChangeListener.onChangeState(arg0);
		}
	}

	/**
	 * 
	 * 滚动时，判断当前第一个View是否发生变化，发生才回调
	 */
	public void onScrolled(int arg0, int arg1) {
		mCurrentView = getChildAt(0);
		if (mItemScrollChangeListener != null && mCurrentView != null) {
			mItemScrollChangeListener.onChange(mCurrentView, getChildPosition(mCurrentView));
		}

	}

}