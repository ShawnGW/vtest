package mesinfor;

import java.util.ArrayList;
import java.util.HashMap;
import mestools.GetCPYeildFromMes;
import mestools.OutPutSpecialChar;

public class getWaferIdYield {
	public HashMap<String, String>  get(String lot,String waferid) {
		HashMap<String, String> yieldMap=new HashMap<>();
		GetMesInformations getMesInformations=new GetMesInformations();
		String infor=getMesInformations.getInfor(new GetCPYeildFromMes(lot), GetMesInformations.TYPE_CPYIELD).get("INFOR");
		ArrayList<Integer> tokens=new ArrayList<>();
		ArrayList<String> contents=new ArrayList<>();
		OutPutSpecialChar.outPut(infor,tokens,contents);
		for (String content : contents) {
			String process=content.substring(tokens.get(tokens.size()-2)).trim();
			if (!yieldMap.containsKey(process)) {
				String id=content.substring(tokens.get(1),tokens.get(2)).trim();
				String yield=content.substring(tokens.get(9),tokens.get(10)).trim();
				if (id.equals(waferid)) {
					yieldMap.put(process, yield);
				}
			}
		}
		return yieldMap;
	}
}
