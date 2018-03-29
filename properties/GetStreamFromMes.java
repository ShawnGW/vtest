package properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStreamFromMes {
	public static BufferedReader getStream(String URL) throws IOException
	{
		InputStream inputStream=null;
		URL url=null;
		HttpURLConnection urlConnection=null;
		url=new URL(MesProperties.SERVICE_URL+URL);
		urlConnection=(HttpURLConnection)url.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setRequestProperty("HOST", MesProperties.SERVICE_HOST);
		urlConnection.setReadTimeout(10000);
		urlConnection.setConnectTimeout(10000);
		initUrlConnecttion(urlConnection, 0);
		inputStream=urlConnection.getInputStream();
		InputStreamReader IsReader=new InputStreamReader(inputStream,"utf8");
		BufferedReader bufferedReader=new BufferedReader(IsReader);	
		return bufferedReader;
	}
	private static void initUrlConnecttion(HttpURLConnection urlConnection,int times)
	{
		try {
			urlConnection.connect();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			times++;
			if (times<6) {
				initUrlConnecttion(urlConnection, times);
			}
		}
	}
}
