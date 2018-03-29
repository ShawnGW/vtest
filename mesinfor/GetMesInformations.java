package mesinfor;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import mestools.GetLotConfigFromMes;
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
	public static void main(String[] args) {
		GetMesInformations getMesInformations =new GetMesInformations();
		getMesInformations.getSlotAndSequence("FA75-5476");
	}
	public HashMap<String, String> getSlotAndSequence(String customerLot)
	{
		HashMap<String, String> config=new HashMap<>();
		config.put("readType", "OCR");
		config.put("sequence", "1-25");
		config.put("gpib", "0");
		GetMesInformations getMesInformations=new GetMesInformations();
		HashMap<String, String> resultMap=getMesInformations.getInfor(new GetLotConfigFromMes(customerLot), GetMesInformations.TYPE_CONFIG);
		Set<String> resultSet=resultMap.keySet();
		for (String properties : resultSet) {
			if (properties.equals("[FlexibleItem_ProcessSpecAttributes:Wafer_Sequence]")) {
				config.put("sequence",resultMap.get(properties).trim());
			}
			if (properties.equals("[FlexibleItem_ProcessSpecAttributes:WaferID_read]")) {
				config.put("readType",resultMap.get(properties).trim());
			}
			if (properties.equals("[FlexibleItem_ProcessSpecAttributes:GPIB_Bin]")) {
				config.put("gpib",resultMap.get(properties).trim());
			}
		}
		return config;
	}
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
