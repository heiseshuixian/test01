package SlidingMenuActivity;

import com.jeremyfeinstein.slidingmenu.lib.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 划出的菜单栏 - 用来显示界面中的列表的。
 */
public class SampleListFragment extends ListFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		SampleAdapter adapter = new SampleAdapter(getActivity());
		
		
		adapter.add(new SampleItem("我的投稿", android.R.drawable.ic_menu_view));
		adapter.add(new SampleItem("我的任务", android.R.drawable.ic_menu_search));
		adapter.add(new SampleItem("我的文件", android.R.drawable.ic_menu_always_landscape_portrait));
		adapter.add(new SampleItem("我的收藏", android.R.drawable.ic_menu_compass));
		adapter.add(new SampleItem("我的礼品", android.R.drawable.ic_menu_more));
		adapter.add(new SampleItem("我日尼玛", android.R.drawable.ic_menu_save));
		
		setListAdapter(adapter);
	}
	
	public class SampleAdapter extends ArrayAdapter<SampleItem> {
		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}
	}
	
	private class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}
}
