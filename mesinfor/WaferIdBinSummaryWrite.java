package mesinfor;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import mestools.WaferidInforIntoMes;

public class WaferIdBinSummaryWrite {
	public void write(HashMap<String, String> resultMap,TreeMap<Integer, Integer> binSummary)
	{
		String lotNum=resultMap.get("lot");
		String cp=resultMap.get("cp");
		String waferId=resultMap.get("waferId");
		
		StringBuffer SB=new StringBuffer();
		Set<Integer> set=binSummary.keySet();
		for (Integer bin : set) {
			SB.append("|Bin"+bin+":"+binSummary.get(bin));
		}
		String summary=SB.toString();
		WaferidInforIntoMes waferidInforIntoMes=new WaferidInforIntoMes();
		waferidInforIntoMes.write(lotNum, waferId, cp, summary);	
	}
}
