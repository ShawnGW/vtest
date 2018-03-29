package properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GetRawdataProperties {
	public  HashMap<String, String> getConfigs() throws IOException {
		HashMap<String, String> properties=new HashMap<>();
		InputStream inputStream=this.getClass().getResourceAsStream("Rawdata.properties");
		InputStreamReader reader=new InputStreamReader(inputStream, "utf8");
		BufferedReader bufferedReader=new BufferedReader(reader);
		String content;
		while((content=bufferedReader.readLine())!=null)
		{
			String[] map=content.trim().split("&");
			if (map.length>1) {
				properties.put(map[1], map[0]);
			}
		}
		return properties;		
	}
	public  LinkedHashMap<String, String> getProperties() throws IOException {
		LinkedHashMap<String, String> properties=new LinkedHashMap<>();
		InputStream inputStream=this.getClass().getResourceAsStream("Rawdata.properties");
		InputStreamReader reader=new InputStreamReader(inputStream, "utf8");
		BufferedReader bufferedReader=new BufferedReader(reader);
		String content;
		while((content=bufferedReader.readLine())!=null)
		{
			String[] map=content.trim().split("&");
			if (map.length>1) {
				properties.put(map[0], map.length>2?map[2]:map[1]);
			}else {
				properties.put(map[0], "NA");
			}
		}
		return properties;		
	}
}
