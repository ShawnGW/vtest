package parseMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class GetBinValue {
	public static ArrayList<Object> Get(File file,Integer GrossDie) throws IOException
	{
		ArrayList<Object> Result=new ArrayList<>();
		HashMap<String, String> Bin_Map=new HashMap<>();
		FileReader reader=new FileReader(file);
		BufferedReader bufferedReader=new BufferedReader(reader);
		String Content;
		boolean flag=false;
		StringBuffer SB=new StringBuffer();
		String Endtime=null;
		Integer sum=0;
		HashMap<Integer, Integer> ReTestSummary=new HashMap<>();
		HashMap<Integer, Integer> TestSummary=new HashMap<>();
	//	boolean RetestStartFlag=false;
		Integer SoftBin=0;	
		while((Content=bufferedReader.readLine())!=null)
		{
			if (Content.contains("PRR")) {
				sum++;
				flag=true;
				continue;
			}
			if (flag) {				
				if (Content.contains("HARD_BIN")) {
					SB.append(Content.split(":")[1].trim()+":");
				}
				if (Content.contains("SOFT_BIN:")) {
					SoftBin=Integer.valueOf(Content.split(":")[1].trim());
					SB.append(SoftBin+":");
				}
				if (Content.contains("X_COORD:")) {
					SB.append(Content.split(":")[1].trim()+":");
				}
				if (Content.contains("Y_COORD:")) {
					SB.append(Content.split(":")[1].trim()+"&");					
				}
//				if (Content.contains("PART_ID:")) {
//					if (GrossDie<Integer.valueOf(Content.split(":")[1].trim())) {
//						RetestStartFlag=true;						
//					}
//					flag=false;
//				}
//				if (RetestStartFlag) {
//					if (ReTestSummary.containsKey(SoftBin)) {
//						ReTestSummary.put(SoftBin, ReTestSummary.get(SoftBin)+1);
//					}else {
//						ReTestSummary.put(SoftBin, 1);
//					}
//					RetestStartFlag=false;
//				}
				if (TestSummary.containsKey(SoftBin)) {
					TestSummary.put(SoftBin, TestSummary.get(SoftBin)+1);
				}else {
					TestSummary.put(SoftBin, 1);
				}
			}
			if (Content.contains("FINISH_T:")) {
				String TempEndtime=Content.split("\\(")[1];
				Endtime=TempEndtime.substring(0, TempEndtime.length()-1);
			}
		}
		bufferedReader.close();
		String[] NameTokens2=SB.toString().split("&");
		for (int i = 0; i < NameTokens2.length; i++) {
			String[] Bin_Inf=NameTokens2[i].split(":");
			if (Bin_Map.containsKey(Bin_Inf[2]+":"+Bin_Inf[3])) {
				Integer BinValue=Integer.valueOf(Bin_Inf[1]);
				if (ReTestSummary.containsKey(BinValue)) {
					ReTestSummary.put(BinValue, ReTestSummary.get(BinValue)+1);
				}else {
					ReTestSummary.put(BinValue, 1);
				}
			}
			Bin_Map.put(Bin_Inf[2]+":"+Bin_Inf[3], Bin_Inf[0]+":"+Bin_Inf[1]);
		}
		if (Endtime==null) {
			Endtime=new SimpleDateFormat("d-MMM-yyyy HH:mm:ss ",Locale.ENGLISH).format(file.lastModified());
		}
		Result.add(Bin_Map);
		Result.add(Endtime);
		Result.add(TestSummary);
		Result.add(ReTestSummary);
		return Result;
	}
}
