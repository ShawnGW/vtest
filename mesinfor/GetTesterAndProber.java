package mesinfor;

import java.util.ArrayList;
import java.util.HashMap;
import mestools.GetTesterProberAndCardFromMes;
import mestools.OutPutSpecialChar;

public class GetTesterAndProber {
	public HashMap<String, String> Get(String lot,String cp)
	{
		HashMap<String, String> resultMap=new HashMap<>();
		resultMap.put("Tester ID", "NA");
		resultMap.put("Prober ID", "NA");
		resultMap.put("Prober Card ID", "NA");
		ArrayList<Integer> tokens=new ArrayList<>();
		ArrayList<String> contents=new ArrayList<>();
		GetMesInformations getMesInformations=new GetMesInformations();
		java.util.HashMap<String,String> result= getMesInformations.getInfor(new GetTesterProberAndCardFromMes(lot), GetMesInformations.TYPE_TESTERANDPROBER);
		OutPutSpecialChar.outPut(result.get("INFOR"),tokens,contents);
		String patternChar=cp+"T0";		
		for (String content : contents) {
			if (content.contains(patternChar)&&content.contains("LotTrackIn")) {
				String tester =content.substring(tokens.get(4), tokens.get(5)).trim();
				String prober=content.substring(tokens.get(5), tokens.get(6)).trim();
				String proberCard=content.substring(tokens.get(6), tokens.get(7)).trim();
				if (!tester.equals("")) {
					resultMap.put("Tester ID", tester);
				}
				if (!prober.equals("")) {
					resultMap.put("Prober ID", prober);
				}
				if (!proberCard.equals("")) {
					resultMap.put("Prober Card ID", proberCard);
				}
			}
		}	
		return resultMap;
	}
}
