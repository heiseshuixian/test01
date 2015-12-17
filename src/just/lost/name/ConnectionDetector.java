package just.lost.name;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * @author ���� maqinghua
 * @version ����ʱ��:Jul 25, 2012 4:44:30 PM 
 * ��˵�� �������Ӽ����
 */
public class ConnectionDetector {

	private Context context;

	public ConnectionDetector(Context context) {
		this.context = context;
	}

	/**
	 * @author ���� maqinghua
	 * @version ����ʱ��:Jul 25, 2012 4:44:30 PM 
	 * ����˵�� ����Ƿ�����������
	 */
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			Toast.makeText(context, "�޿�������", 2000).show();
			return false;
		}
	}
	
	/**
	 * @author ���� maqinghua
	 * @version ����ʱ��:Jul 25, 2012 4:44:30 PM 
	 * ����˵�� ���ָ��ip��ַ�Ƿ���Ч/�Ƿ�������
	 */	
	public boolean checkURL(String url){
		boolean result = false;
		try {
			HttpURLConnection conn=(HttpURLConnection)new URL(url).openConnection();
			conn.setConnectTimeout(60000);
			int code = conn.getResponseCode();
			if(code!=200){
				result=false;
				Toast.makeText(context, "����������", 2000).show();
			   }else{
				result=true;
			   }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
