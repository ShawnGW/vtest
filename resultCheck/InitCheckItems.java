package resultCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class InitCheckItems {
	public static void main(String[] args) throws IOException {
		InitCheckItems initCheckItems=new InitCheckItems();
		initCheckItems.getProperties();
	}
	public HashMap<String, String>  getProperties() throws IOException {
		HashMap<String, String> propertiesCheckMap=new HashMap<>();
		InputStream iStream=this.getClass().getResourceAsStream("CheckItem.properties");
		InputStreamReader reader=new InputStreamReader(iStream, "utf8");
		BufferedReader bufferedReader=new BufferedReader(reader);
		String content;
		while((content=bufferedReader.readLine())!=null)
		{
			String[] infor=content.split(":");
			propertiesCheckMap.put(infor[0].trim(), infor.length>1?infor[1].trim():"NA");
		}
		bufferedReader.close();
		return propertiesCheckMap;
	}
}
