package just.lost.name;

import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class VideoActivity extends BaseActivity  {

	/**视频控件*/
	private VideoView videoview;
	/**传过来的路径*/
	private String path;
	/**传过来的网址路径*/
	private  Uri uri;
	/**播放按钮*/
	private ImageView img_start;
	/**容器*/
	private RelativeLayout relative;
	
	@Override
	protected int getContentViewId() {
		return R.layout.video;
	}

	@Override
	protected void findViews() {
		videoview = (VideoView) findViewById(R.id.videoView);
		img_start = (ImageView) findViewById(R.id.img_start);
		relative = (RelativeLayout) findViewById(R.id.relative);
	}
	
	@Override
	protected void initGetData() {
		super.initGetData();
		if (getIntent().getExtras()!=null) {
			//path = getIntent().getExtras().getString("path");
		 uri = Uri.parse("http://192.168.1.103:8080/testsever/img/img_1.mp4");
		}
	}

	@Override
	protected void init() {
		
		//videoview.setVideoPath(path);
		videoview.setVideoURI(uri);
		videoview.setMediaController(new MediaController(VideoActivity.this));
		videoview.requestFocus();  
	}

	@Override
	protected void widgetListener() {
		relative.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (videoview.isPlaying()) {
					videoview.pause();
					img_start.setVisibility(View.VISIBLE);
				}else{
					videoview.start();
					img_start.setVisibility(View.GONE);
				}
			}
		});
	}
}
