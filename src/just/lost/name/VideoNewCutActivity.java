package just.lost.name;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import just.lost.name.MyRecyclerView.OnItemScrollChangeListener;
import just.lost.name.VideoNewCutAdapter.MyItemClickListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class VideoNewCutActivity extends BaseActivity{
	/**返回*/
	private ImageView img_back;
	/**确认*/
	private TextView txt_enter;
	/** 视频bean */
	private ViewNewSelectBean bean;
	/** 横向listview */
	private MyRecyclerView recyclerView;
	/** 封面图按钮 */
	private ImageView img_bg;
	/** 阴影色块left */
	private ImageView img_left;
	/** 阴影色块right */
	private ImageView img_right;
	/** 显示时间 */
	private TextView txt_time;
	/** 封面容器 */
	private RelativeLayout relative;
	/** 进度条 */
	private RelativeLayout relative1;
	/** 视频播放 */
	private VideoView videoView;
	/** 数据集 */
	private ArrayList<String> list;
	/** 列表适配器 */
	private VideoNewCutAdapter adapter;
	/** 屏幕宽度 */
	private int width;
	/** 临时保存文件路径 */
	private String savePath;
	/** 最少多少秒 */
	public static final int MIN_TIME = 5000;
	/** 最大多少秒 */
	public static final int MAX_TIME = 15000;
	/** 屏幕中1像素占有多少毫秒 */
	private float picture = 0;
	/** 多少秒一帧 */
	private float second_Z;

	/** 是否中断线性 */
	private boolean isThread = false;

	/** 左边拖动按钮 */
	private Button txt_left;
	/** 右边拖动按钮 */
	private Button txt_right;

	/** 按下时X抽坐标 */
	private float DownX;

	/** 拖动条容器 */
	private LayoutParams layoutParams_progress;
	/** 阴影背景容器 */
	private LayoutParams layoutParams_yin;
	/** 拖动条的宽度 */
	private int width_progress = 0;
	/** 拖动条的间距 */
	private int Margin_progress = 0;
	/** 阴影框的宽度 */
	private int width1_progress = 0;

	/** 不能超过右边多少 */
	private int right_margin = 0;
	/** 所有图片长度 */
	private int img_widthAll = 0;
	/** 最少保留的多少秒长度 */
	private int last_length = 0;
	/** 左边啦了多少 */
	private int left_lenth = 0;
	/** 滚动的长度 */
	private int Scroll_lenth = 0;
	/** 路径 */
	private String Ppath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoTest/Image/";
	@Override
	protected int getContentViewId() {
		return R.layout.activity_video_new_cut;
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void findViews() {
		img_back = (ImageView) findViewById(R.id.video_new_img_back);
		txt_enter = (TextView) findViewById(R.id.video_new_txt_enter);
		recyclerView = (MyRecyclerView) findViewById(R.id.recyclerview_horizontal);
		videoView = (VideoView) findViewById(R.id.video_new_cut_videoview);
		img_bg = (ImageView) findViewById(R.id.video_new_cut_img_bg);
		img_left = (ImageView) findViewById(R.id.video_new_cut_img_left);
		img_right = (ImageView) findViewById(R.id.video_new_cut_img_right);
		relative = (RelativeLayout) findViewById(R.id.video_new_cut_relative);
		txt_time = (TextView) findViewById(R.id.video_new_cut_txt_time);
		relative1 = (RelativeLayout) findViewById(R.id.video_new_cut_relative1);

		txt_left = (Button) findViewById(R.id.video_new_cut_txt_left);
		txt_right = (Button) findViewById(R.id.video_new_cut_txt_right);

		width = getWindowManager().getDefaultDisplay().getWidth();

		LayoutParams layoutParams = (LayoutParams) relative.getLayoutParams();
		layoutParams.width = width;
		layoutParams.height = width;
		relative.setLayoutParams(layoutParams);

		// 创建一个线性布局管理器
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		// 设置布局管理器
		recyclerView.setLayoutManager(layoutManager);

		list = new ArrayList<String>();
		adapter = new VideoNewCutAdapter(list);
	}
	@Override
	protected void initGetData() {
		super.initGetData();
		if (getIntent().getExtras() != null) {
			bean = (ViewNewSelectBean) getIntent().getExtras().getSerializable("serializable");
		}
	}
	@Override
	protected void init() {
		// 创建文件夹
				File file = new File(Ppath);
				if (!file.exists()) {
					file.mkdir();
				}
				
				recyclerView.setAdapter(adapter);

				videoView.setVideoPath(bean.getPath());
				videoView.requestFocus();

				/** 一个屏幕1像素是多少毫秒 13.88888 */
				picture = (float) MAX_TIME / (float) width;
				/** 1.66666 */
				second_Z = (float) MAX_TIME / 1000f / ((float) width / (float) DisplayUtil.dip2px(VideoNewCutActivity.this, 60));

				getBitmapsFromVideo(bean.getPath(), (int) bean.getDuration());
	}
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {

				adapter.notifyItemInserted(msg.arg1);

				if (msg.arg1 == 0) {
					sendVideo(DisplayUtil.dip2px(VideoNewCutActivity.this, 60));
				}

			} else if (msg.what == 2) {

				img_widthAll = (int) (msg.arg1 * 1000 / picture);

				last_length = (int) (MIN_TIME / picture);

				if (img_widthAll < width) {
					right_margin = width - img_widthAll;
					LayoutParams layoutParams_right = (LayoutParams) img_right.getLayoutParams();
					layoutParams_right.width = width - img_widthAll;
					img_right.setLayoutParams(layoutParams_right);

					layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
					layoutParams_progress.width = img_widthAll;
					layoutParams_progress.rightMargin = width - img_widthAll;
					relative1.setLayoutParams(layoutParams_progress);

					txt_time.setText(msg.arg1 + ".0 s");
				} else {
					img_widthAll = width;
					layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
					layoutParams_progress.width = width;
					relative1.setLayoutParams(layoutParams_progress);

					txt_time.setText((MAX_TIME / 1000) + ".0 s");
				}
			} 
		};
	};
	
	public void getBitmapsFromVideo(final String dataPath, final int lenth) {
		new Thread(new Runnable() {

			public void run() {
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
				retriever.setDataSource(dataPath);
				// 取得视频的长度(单位为秒)
				int seconds = lenth / 1000;

				Message message = handler.obtainMessage();
				message.what = 2;
				message.arg1 = seconds;
				handler.sendMessage(message);
				Bitmap bitmap;
				// 得到每一秒时刻的bitmap比如第一秒,第二秒
				int index = 0;
				for (float f = second_Z; f <= (float) seconds; f += second_Z) {

					if (isThread) {
						return;
					}
					bitmap = retriever.getFrameAtTime((long) (f * 1000 * 1000), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

					String path = Ppath + System.currentTimeMillis() + ".jpg";
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(path);
						bitmap.compress(CompressFormat.JPEG, 80, fos);
						fos.close();

						list.add(path);
						Message message1 = handler.obtainMessage();
						message1.what = 1;
						message1.arg1 = index;
						handler.sendMessage(message1);
						index++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	@Override
	protected void widgetListener() {
		adapter.setOnClickListener(new MyItemClickListener() {
			/** 列表点击事件 */
			public void onItemClick(View view, int position) {
				sendVideo((position + 1) * view.getWidth());
			}
		});
		/** 返回 */
		img_back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
		/** 完成 */
		txt_enter.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				handler.post(runnable3);				
			}
		});
		/** 播放 */
		relative.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (videoView.isPlaying()) {
					img_bg.setVisibility(View.VISIBLE);
					videoView.pause();

					handler.removeCallbacks(runnable);

				} else {
					videoView.setVisibility(View.VISIBLE);
					img_bg.setVisibility(View.GONE);
					videoView.start();

					layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
					// 会误差 200-800毫秒
					handler.postDelayed(runnable, (long) (layoutParams_progress.width * picture) + 500);
				}
			}
		});
		/** 左边拖动按钮 */
		txt_left.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					DownX = event.getRawX();

					layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
					layoutParams_yin = (LayoutParams) img_left.getLayoutParams();

					width_progress = layoutParams_progress.width;
					Margin_progress = layoutParams_progress.leftMargin;
					width1_progress = layoutParams_yin.width;

					break;
				case MotionEvent.ACTION_MOVE:

					LeftMoveLayout(event.getRawX() - DownX, event.getRawX());

					break;
				case MotionEvent.ACTION_UP:

					sendVideo();

					layoutParams_progress = null;
					layoutParams_yin = null;

					break;
				default:
					break;
				}
				return false;
			}
		});

		/** 右边拖动按钮 */
		txt_right.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					DownX = event.getRawX();

					layoutParams_progress = (LayoutParams) relative1.getLayoutParams();
					layoutParams_yin = (LayoutParams) img_right.getLayoutParams();

					width_progress = layoutParams_progress.width;
					Margin_progress = layoutParams_progress.rightMargin;
					width1_progress = layoutParams_yin.width;

					break;
				case MotionEvent.ACTION_MOVE:

					RightMoveLayout(DownX - event.getRawX());

					break;
				case MotionEvent.ACTION_UP:
					layoutParams_progress = null;
					layoutParams_yin = null;
					break;

				default:
					break;
				}
				return false;
			}
		});
		/** 视频播放完回调 */
		videoView.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				img_bg.setVisibility(View.VISIBLE);
				handler.removeCallbacks(runnable);
			}
		});

		/** 滚动监听 */
		recyclerView.setOnItemScrollChangeListener(new OnItemScrollChangeListener() {

			public void onChange(View view, int position) {
				Scroll_lenth = position * view.getWidth() - view.getLeft();

				if (Scroll_lenth <= 0) {
					Scroll_lenth = 0;
				}

//				 sendVideo();//打开注释就是边滑动变更新视图
			}

			public void onChangeState(int state) {
				if (state == 0) {// 静止情况时候才调用
					sendVideo();
				}
			}
		});
		
		
	}
	private Runnable runnable3 = new Runnable() {

		public void run() {
			try {
				layoutParams_progress = (LayoutParams) relative1.getLayoutParams();

				savePath = Ppath + System.currentTimeMillis() + ".mp4";

				FUckTest.startTrim(new File(bean.getPath()), new File(savePath), (long) ((Scroll_lenth + left_lenth) * picture), (long) ((Scroll_lenth
						+ left_lenth + layoutParams_progress.width) * picture));
				
				Intent it = new Intent(VideoNewCutActivity.this,VideoActivity.class);
				it.putExtra("path", savePath);
				startActivity(it);
				
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	};
	/**
	 * 向右边啦
	 * 
	 * @version 1.0
	 * @createTime 2015年6月18日,上午9:44:32
	 * @updateTime 2015年6月18日,上午9:44:32
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param MoveX
	 */
	private void LeftMoveLayout(float MoveX, float X) {
		if (layoutParams_progress != null && layoutParams_yin != null) {
			if (Margin_progress + (int) MoveX > 0 && width_progress - (int) MoveX > last_length) {
				layoutParams_progress.width = width_progress - (int) MoveX;
				layoutParams_progress.leftMargin = Margin_progress + (int) MoveX;
				layoutParams_yin.width = width1_progress + (int) MoveX;

				relative1.setLayoutParams(layoutParams_progress);
				img_left.setLayoutParams(layoutParams_yin);

				txt_time.setText((float) (Math.round((layoutParams_progress.width * picture / 1000) * 10)) / 10 + " s");

				left_lenth = layoutParams_yin.width;
			}
		}
	}

	/**
	 * 向左边拉
	 *
	 * @version 1.0
	 * @createTime 2015年6月18日,上午9:45:16
	 * @updateTime 2015年6月18日,上午9:45:16
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param MoveX
	 */
	private void RightMoveLayout(float MoveX) {
		if (layoutParams_progress != null && layoutParams_yin != null) {
			if (Margin_progress + (int) MoveX > right_margin && width_progress - (int) MoveX > last_length) {
				layoutParams_progress.width = width_progress - (int) MoveX;
				layoutParams_progress.rightMargin = Margin_progress + (int) MoveX;
				layoutParams_yin.width = width1_progress + (int) MoveX;

				txt_time.setText((float) (Math.round((layoutParams_progress.width * picture / 1000) * 10)) / 10 + " s");

				relative1.setLayoutParams(layoutParams_progress);
				img_right.setLayoutParams(layoutParams_yin);
			}
		}
	}

	private Runnable runnable = new Runnable() {

		public void run() {
			if (!img_bg.isShown()) {
				img_bg.setVisibility(View.VISIBLE);
			}
			if (videoView.isPlaying()) {
				videoView.pause();
			}
		}
	};

	/**
	 * 移动起始播放位置
	 *
	 * @version 1.0
	 * @createTime 2015年6月18日,下午2:34:31
	 * @updateTime 2015年6月18日,下午2:34:31
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param proSlide
	 */
	private void sendVideo() {
		if (!videoView.isShown()) {
			videoView.setVisibility(View.VISIBLE);
		}
		if (videoView.isPlaying()) {
			videoView.pause();
		}
		if (!img_bg.isShown()) {
			img_bg.setVisibility(View.VISIBLE);
		}

		handler.removeCallbacks(runnable);

		videoView.seekTo((int) ((Scroll_lenth + left_lenth) * picture));
	}

	/**
	 * 移动起始播放位置
	 *
	 * @version 1.0
	 * @createTime 2015年6月18日,下午6:09:50
	 * @updateTime 2015年6月18日,下午6:09:50
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 * @param lenth
	 *                长度
	 */
	private void sendVideo(int lenth) {
		if (!videoView.isShown()) {
			videoView.setVisibility(View.VISIBLE);
		}
		if (videoView.isPlaying()) {
			videoView.pause();
		}
		if (!img_bg.isShown()) {
			img_bg.setVisibility(View.VISIBLE);
		}

		handler.removeCallbacks(runnable);

		videoView.seekTo((int) (lenth * picture));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isThread = true;
		deleteFile();
	}

	/**
	 * 退出时删除临时文件
	 *
	 * @version 1.0
	 * @createTime 2015年6月17日,下午1:58:52
	 * @updateTime 2015年6月17日,下午1:58:52
	 * @createAuthor WangYuWen
	 * @updateAuthor WangYuWen
	 * @updateInfo (此处输入修改内容,若无修改可不写.)
	 *
	 */
	private void deleteFile() {
		for (int i = 0; i < list.size(); i++) {
			File file = new File(list.get(i));
			if (file.exists()) {
				file.delete();
			}
		}
	}


}
