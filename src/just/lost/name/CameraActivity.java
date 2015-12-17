package just.lost.name;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CameraActivity extends Activity {
	/** Called when the activity is first created. */
	Handler handle = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			TextView tv = (TextView) findViewById(R.id.text);
			ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
			switch (msg.what) {
			case 0:
				tv.setText("上传成功。");	
				pb.setVisibility(ProgressBar.GONE);
				break;
			case 1:
				tv.setText("无可用网络。");	
				pb.setVisibility(ProgressBar.GONE);
				break;
			case 2:
				tv.setText("找不到服务器地址");	
				pb.setVisibility(ProgressBar.GONE);
				break;				
			default:
				break;
			}
		}
		
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
System.out.println("准备录制视频");
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		System.out.println("录制完毕");
		startActivityForResult(intent, 1);
		System.out.println("activity成功");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("test", "onActivityResult() requestCode:" + requestCode
				+ ",resultCode:" + resultCode + ",data:" + data);
		if (null != data) {
			Uri uri = data.getData();
			if (uri == null) {
				return;
			} else {
				Cursor c = getContentResolver().query(uri,
						new String[] { MediaStore.MediaColumns.DATA }, null,
						null, null);
				if (c != null && c.moveToFirst()) {
					String filPath = c.getString(0);
					Log.d("test", filPath);
					new Upload(filPath).start();
				}
			}
		}

	}
	public class Upload extends Thread {
		String filpath;

		public Upload(String filpath) {
			super();
			this.filpath = filpath;
		}


		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();	
			ConnectionDetector cd = new ConnectionDetector(CameraActivity.this);
			if(cd.isConnectingToInternet()){
				if(cd.checkURL("http://192.168.1.103:8080/testsever/Videoservlet")){
					uploadFile(filpath);
					handle.sendEmptyMessage(0);
				}else{
					handle.sendEmptyMessage(2);
				}
			}else{
				handle.sendEmptyMessage(1);
			}
		}
		
	}
	
	public void uploadFile(String imageFilePath) {
		String actionUrl = "http://192.168.1.103:8080/testsever/Videoservlet";
		System.out.println("获取地址");
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);

			con.setRequestMethod("POST");

			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			File file = new File(imageFilePath);

			FileInputStream fStream = new FileInputStream(file);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;

			while ((length = fStream.read(buffer)) != -1) {

				ds.write(buffer, 0, length);
			}

			fStream.close();
			ds.flush();

			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}

			ds.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}