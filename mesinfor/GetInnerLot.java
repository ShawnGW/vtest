package mesinfor;

import java.util.ArrayList;
import mestools.GetInnerLotFromMes;
import mestools.OutPutSpecialChar;

public class GetInnerLot {
	public static String get(String waferId) {
		String innerLot="NA";
		GetMesInformations getMesInformations=new GetMesInformations();
		ArrayList<Integer> tokens=new ArrayList<>();
		ArrayList<String> contents=new ArrayList<>();
		String infor=getMesInformations.getInfor(new GetInnerLotFromMes(waferId), GetMesInformations.TYPE_INNERLOT).get("INFOR");
		OutPutSpecialChar.outPut(infor,tokens,contents);
		for (String content : contents) {
			Integer status=Integer.valueOf(content.substring(tokens.get(tokens.size()-2)).trim());
			if (status>0) {
				innerLot=content.substring(0, tokens.get(1));
				break;
			}
		}
		return innerLot;
	}
}
