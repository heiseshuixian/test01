package just.lost.name;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

public class VideodownActivity extends Activity {
    /** Called when the activity is first created. */
	private Button btn_play;
	private Button btn_pause;
	private Button btn_back;
	private SeekBar sb_progress;
	private VideoView vv_player;
	private boolean flag = true;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			sb_progress.setProgress(msg.getData().getInt("current", 0)/1000);
		};
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplay);
        loadView();
        loadVideo();
        addListener();
    }
   //��������view��ͼ
    public void loadView(){
    	//���صײ�����button�ؼ�
    	btn_play = (Button)findViewById(R.id.btn_play);
    	btn_pause = (Button)findViewById(R.id.btn_pause);
    	btn_back = (Button)findViewById(R.id.btn_back);
    	//���ؽ������ؼ�
    	sb_progress = (SeekBar)findViewById(R.id.sb_progress);
    	//����videoview�ؼ�
    	vv_player = (VideoView)findViewById(R.id.vv_player);
    }
    //���пؼ��¼�����
    
    public void loadVideo(){
    	Uri uri = Uri.parse("http://192.168.1.104:8080/testsever/img/img_3.mp4");
		vv_player.setVideoURI(uri);
		//���ñ�����Ƶ��Դ·��
		//vv_player.setVideoPath("/sdcard/video/test.mp4");
		vv_player.setMediaController(new MediaController(VideodownActivity.this));
		//���ý���
		vv_player.requestFocus();
    }
    public void addListener(){

    	//play�ؼ��¼���������
    	btn_play.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vv_player.start();
				sb_progress.setMax(vv_player.getDuration()/1000);
				//����һ���߳�����ͬ��seekbar����                                             
				new Thread(){
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();
						while (flag) {
							Message msg = handler.obtainMessage();
							msg.getData().putInt("current", vv_player.getCurrentPosition());
							handler.sendMessage(msg);
							try {
								sleep(1000);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}.start();
			}
		});
    	//pause�ؼ��¼���������
		btn_pause.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vv_player.pause();
			}
		});
		
	     btn_back.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	                Intent intent = new Intent();
	                intent.setClass(VideodownActivity.this, Test01Activity.class);
	                startActivity(intent);
	                finish();
	            }
	        });
    }
}