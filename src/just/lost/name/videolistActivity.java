package just.lost.name;


import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;




public class videolistActivity extends BaseActivity {
	
	private GridView gridview;    //图片展示器
	private ViewNewSelectAdapter adapter;//图片适配器
	private ArrayList<ViewNewSelectBean> list; //数据集
	private ImageView img_back;
	private int width;//图片的宽度
	
	
	
	
	private class ViewNewSelectAdapter extends BaseAdapter {
		private Context context;
		/** 数据集 */
		private ArrayList<ViewNewSelectBean> list;
		/** 图片宽 */
		private int width;

		public ViewNewSelectAdapter(ArrayList<ViewNewSelectBean> list, int width, Context context) {
			this.list = list;
			this.width = width;
			this.context = context;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			final ViewNewSelectBean bean = list.get(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.item_video_new_select_gridview, null);
				viewHolder.txt = (TextView) convertView.findViewById(R.id.item_video_new_select_txt_time);
				viewHolder.img = (ImageView) convertView.findViewById(R.id.item_video_new_select_img);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			LayoutParams layoutParams = (LayoutParams) viewHolder.img.getLayoutParams();
			layoutParams.width = width;
			layoutParams.height = width;
			viewHolder.img.setLayoutParams(layoutParams);
			// 获取图片
			Bitmap bitmap = getVideoThumbnail(bean.getPath(), width, width, MediaStore.Images.Thumbnails.MICRO_KIND);
			if (bitmap != null) {
				// 设置图片
				viewHolder.img.setImageBitmap(bitmap);
			}
			// 设置时长
			viewHolder.txt.setText(String.format("时长：%1$s s", bean.getDuration() / 1000));

			return convertView;
		}
}
	@Override
	protected int getContentViewId() {
		
	return R.layout.videolist;
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void findViews() {
		img_back = (ImageView) findViewById(R.id.video_new_img_back);
		gridview = (GridView) findViewById(R.id.video_new_select_gridview);

		width = (getWindowManager().getDefaultDisplay().getWidth() - DisplayUtil.dip2px(videolistActivity.this, 60)) / 3;
	}
	@Override
	protected void init() {
		list = new ArrayList<ViewNewSelectBean>();
		adapter = new ViewNewSelectAdapter(list, width, videolistActivity.this);
		gridview.setAdapter(adapter);

		getList();
		
	}
	@Override
	protected void widgetListener() {
		img_back.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				finish();
			}
		});
		img_back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("serializable", list.get(position));
				Intent intent = new Intent(videolistActivity.this, VideoNewCutActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				adapter.notifyDataSetChanged();
			}
		};
	};
	
	private void getList() {
		new Thread(new Runnable() {

			public void run() {
				// 若为图片则为MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				ContentResolver cr = getContentResolver();
				Cursor cursor = cr.query(originalUri, null, null, null, null);
				if (cursor == null) {
					return;
				}
				while (cursor.moveToNext()) {
					ViewNewSelectBean bean = new ViewNewSelectBean();
					bean.set_id(cursor.getLong(cursor.getColumnIndex("_ID")));
					bean.setName(cursor.getString(cursor.getColumnIndex("_display_name")));// 视频名字
					bean.setPath(cursor.getString(cursor.getColumnIndex("_data")));// 路径
					bean.setWidth(cursor.getInt(cursor.getColumnIndex("width")));// 视频宽
					bean.setHeight(cursor.getInt(cursor.getColumnIndex("height")));// 视频高
					bean.setDuration(cursor.getLong(cursor.getColumnIndex("duration")));// 时长
					list.add(bean);

				}

				Message message = handler.obtainMessage();
				message.what = 1;
				handler.sendMessage(message);

				// /data/data/com.android.providers.media/databases/external.db
				// {5dd10730} 数据库位置
			}
		}).start();
	}
	private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	private class ViewHolder {
		private ImageView img;
		private TextView txt;
	}

}

