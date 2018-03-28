package mesinfor;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import mestools.GetRandomChar;
import mestools.MesInterface;
import properties.GetStreamFromMes;

public class GetMesInformations {
	private LinkedHashMap<String, String> result=new LinkedHashMap<>();
	public  static final String TYPE_CONFIG="CONFIG";
	public  static final String TYPE_CPYIELD="CPYIELD";
	public  static final String TYPE_BINDEFINE="BINDEFINE";
	public  static final String TYPE_TESTERANDPROBER="TESTERANDPROBER";
	public  static final String TYPE_INNERLOT="INNERLOT";
	
	public HashMap<String, String> getInfor(MesInterface mesInterface,String type)
	{
		try {			
			BufferedReader bufferedReader=GetStreamFromMes.getStream(mesInterface.getURL());
			StringBuffer SB=new StringBuffer();
			String content=null;
			while((content=bufferedReader.readLine())!=null)
			{
				SB.append(content);
			}
			String[] informations2=SB.toString().split("\\|");
			result.put("MESTYPE", type);
			if (type.equals("CONFIG")) {
				for (int i = 0; i < informations2.length; i++) {
					if (!informations2[i].trim().equals("")) {
						if (result.containsKey(getTitle(informations2[i]))) {
							String value=getValue(informations2[i]);
							if (!value.equals("NA")) {
								result.put(getTitle(informations2[i])+"_"+GetRandomChar.getRandomChar(4), value);
							}
						}else {
							result.put(getTitle(informations2[i]), getValue(informations2[i]));
						}
					}
				}
			}else
			{
				for (int i = 0; i < informations2.length; i++) {
					if (!informations2[i].trim().equals("")) {
						result.put("INFOR", informations2[i]);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	private String getTitle(String component)
	{
		return component.split("=")[0];
	}
	private String getValue(String component)
	{
		String[] contents=component.split("=");
		if (contents.length==1) {
			return "NA";
		}else {
			return contents[1];
		}		
	}
}
