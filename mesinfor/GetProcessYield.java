package mesinfor;

import java.util.ArrayList;
import mestools.OutPutSpecialChar;

public class GetProcessYield {
	public static String getYield(String infor,String cp) {
		String yield="NA";
		ArrayList<Integer> tokens=new ArrayList<>();
		ArrayList<String> contents=new ArrayList<>();
		OutPutSpecialChar.outPut(infor,tokens,contents);
		for (String content : contents) {
			if (content.contains(cp+"T0")) {
				yield=content.substring(tokens.get(9), tokens.get(10));
			}
		}
		return yield;
	}
}
