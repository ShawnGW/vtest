package Tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import parseRawdata.parseRawdata;

public class TextReportModel12 extends FTPRealseModel implements Text_Report {

	@Override
	public void Write_text(String CustomerCode, String Device, String Lot, String CP, File DataSorce,String FileName)
			throws IOException {
		// TODO Auto-generated method stub
		TextReportModel12 model12=new TextReportModel12();
		File[] Filelist=DataSorce.listFiles();
		for (int k = 0; k < Filelist.length; k++) {		
			parseRawdata parseRawdata=new parseRawdata(Filelist[k]);
			LinkedHashMap<String, String> properties=parseRawdata.getProperties();
			
			String Wafer_ID_R=properties.get("Wafer ID");
			String[][] MapCell_R=parseRawdata.getAllDiesDimensionalArray();
			String Flat_R=null;
			String notch=properties.get("Notch");
			if (notch.equals(":0-Degree")) {
				Flat_R="UP";
			}else if (notch.equals(":90-Degree")) {
				Flat_R="RIGHT";
			}else if (notch.equals(":180-Degree")) {
				Flat_R="DOWN";
			}else {
				Flat_R="LEFT";
			}
			String Wafer_Load_Time_R=properties.get("Test End Time");
			Integer PassDie_R=Integer.parseInt(properties.get("Pass Die"));	
			Integer RightID_R=Integer.valueOf(properties.get("RightID"));
			Integer SlotID_R=Integer.valueOf(properties.get("Slot"));
			String waferSize_R=properties.get("WF_Size");
			Integer TestDie_MinX_R=Integer.valueOf(properties.get("TestDieleft"));
			Integer TestDie_MinY_R=Integer.valueOf(properties.get("TestDieup"));
			Integer TestDie_MaxX_R=Integer.valueOf(properties.get("TestDieright"));
			Integer TestDie_MaxY_R=Integer.valueOf(properties.get("TestDiedown"));
			String FinalID=RightID_R.toString();
			if(FinalID.length()==1)
				FinalID="0"+FinalID;
			String VERSION="NA";
			HashMap<String, String> NameMap=model12.InitMap(Lot, FinalID, CP, Wafer_Load_Time_R, Device, Wafer_ID_R, VERSION);
			Set<String> keyset=NameMap.keySet();
			String FinalName=FileName;
			for (String key : keyset) {
				if (FinalName.contains(key)) {
					FinalName=FinalName.replace(key, NameMap.get(key));
				}
			}
			File Result_Text=new File("/TestReport/CustReport/"+CustomerCode+"/"+Device+"/"+Lot+"/"+CP+"/"+FinalName);
			PrintWriter out=null;
			try {
				out=new PrintWriter(new FileWriter(Result_Text));
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			out.print("Test House:VTEST"+"\r\n");
			out.print("Customer Name:NSY"+"\r\n");
			out.print("Device Name:"+Device+"\r\n");
			out.print("Wafer Size:"+waferSize_R+".0Inch\r\n");
			out.print("LOT:"+Lot+"\r\n");
			out.print("SLOT:"+SlotID_R+"\r\n");
			out.print("Good die:"+PassDie_R+"\r\n");
			out.print("Orientation:"+Flat_R.substring(0, 1)+Flat_R.substring(1,Flat_R.length()).toLowerCase()+"\r\n");
			for ( int i = TestDie_MinY_R-3; i <TestDie_MaxY_R+7; i++) {
				for (int j = TestDie_MinX_R-3; j < TestDie_MaxX_R+3; j++) {
						try {
							out.print(MapCell_Modify2.Modify(MapCell_R[i][j]));
						} catch (Exception e) {
							// TODO: handle exception
							out.print(MapCell_Modify2.Modify(null));
						}
				}
				out.print("\r\n");
			}
			out.flush();
			out.close();
			FTP_Release(CustomerCode, Device, Lot, CP, Result_Text);
		}
	}
}
