package parseMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author shawn_Sun;
 * @version 1.0;
 * @category IT;
 * @since 2017.7.2;
 */

public class TelProberMappingParse {
	private static final String[] Negative_Coordinate_X={"ffff:-1","fffe:-2","fffd:-3","fffc:-4","fffb:-5","fffa:-6","fff9:-7","fff8:-8","fff7:-9","fff6:-10"};
	public static String Get(File file,ArrayList<Object> Temp_Final_infor,File outfile)throws IOException, ParserConfigurationException, SAXException
	{
		String Lot_R=(String) Temp_Final_infor.get(0);
		String CustomerCode_R=(String) Temp_Final_infor.get(1);
		String Device_R=(String) Temp_Final_infor.get(2);
		String OP_R=(String) Temp_Final_infor.get(3);
		String Program_R=(String) Temp_Final_infor.get(4);
		String CP_R=(String) Temp_Final_infor.get(5);
		String DataBase_R=(String) Temp_Final_infor.get(6);
		String WaferSize_R=(String) Temp_Final_infor.get(7);
		Integer GPIB_Bin_R=(Integer) Temp_Final_infor.get(8);
		String WaferRead_R=(String) Temp_Final_infor.get(9);
		String WaferSequence_R=(String) Temp_Final_infor.get(10);
		String Nocth_R=(String) Temp_Final_infor.get(11);
		String[] Lot_infor=GetWSInformation.CallserviceForDoc(Lot_R,"CP1").split(":");
		if (Lot_infor[9].equals("NA")) {
			Lot_infor[9]="1";
		}
		ArrayList<String> passBins=new ArrayList<>();
		try {
			String[] PassBinArray=Lot_infor[9].split(",");
			for (int i = 0; i < PassBinArray.length; i++) {
				passBins.add(PassBinArray[i]);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (passBins.size()==0) {
			passBins.add("1");
		}
		@SuppressWarnings("unchecked")
		ArrayList<Integer> PassDie_array_R=(ArrayList<Integer>) Temp_Final_infor.get(12);
		HashMap<String, String> Negative_Values=new HashMap<String,String>();
		HashMap<String, Integer> Bin_Summary=new HashMap<String,Integer>();
		
		for (int i = 0; i < Negative_Coordinate_X.length; i++) {
			Negative_Values.put(Negative_Coordinate_X[i].split(":")[0], Negative_Coordinate_X[i].split(":")[1]);
		}
		Set<String> Negative_describe_set=Negative_Values.keySet();

		FileInputStream fios=new FileInputStream(file);
		byte[] bs = new byte[100000];
		
		ArrayList<Integer> MapRow_Start_indexs=new ArrayList<Integer>();
		int Map_ROW_Start_index=70;
		
		String[][] Mapping=new String[500][500];
		
		String waferid=null;
		byte[] wafer_id_BT=new byte[25];
		String Slot_number=null;
		byte[] Slot_number_BT=new byte[2];
		String Test_Start_Time=null;
		byte[] Test_Start_Time_BT=new byte[12];
		String Test_End_Time=null;
		byte[] Test_End_Time_BT=new byte[12];
		int Row_Sum=0;
		byte Row_Sum_BT = 0;

		fios.read(bs);
		fios.close();
			
		int Total=0;
		for (int i = 0; i < bs.length; i++) {
			if(i<25)
				wafer_id_BT[i]=bs[i];
			if (i>27&&i<30)
				Slot_number_BT[i-28]=bs[i];
			if (i>36&&i<49)
				Test_Start_Time_BT[i-37]=bs[i];
			if (i>48&&i<61)
				Test_End_Time_BT[i-49]=bs[i];
			if (i==61)
				Row_Sum_BT=bs[i];
				Total++;
			if (Integer.toHexString(bs[i]&0xFF).equals("80")) {
				if (bs[i+1]==0&&bs[i+2]==0) {
					break;
				}
			}	
		}
		while(Map_ROW_Start_index<Total)
		{
			MapRow_Start_indexs.add(Map_ROW_Start_index);
			Map_ROW_Start_index+=Integer.parseInt(Integer.toHexString(bs[Map_ROW_Start_index]&0xFF), 16)*2+5;			
		}
		Integer[] MapRow_Start_index_List=(Integer[]) MapRow_Start_indexs.toArray(new Integer[MapRow_Start_indexs.size()]);
		int Max_X=0;
		int Max_Y=0;
		int Min_Y=0;
		int Min_X=0;
		for (int i = 0; i < MapRow_Start_index_List.length; i++) {		
			int index=MapRow_Start_index_List[i];
			int This_Row_Die_Sum=Integer.parseInt(Integer.toHexString(bs[index]&0xFF), 16);
			String First_X=Integer.toHexString(bs[index-3]&0xFF);
			String Second_X=Integer.toHexString(bs[index-4]&0xFF);
			Integer Coordinate_X;
			if (Negative_describe_set.contains(First_X+Second_X)) {
				Coordinate_X=Integer.parseInt(Negative_Values.get(First_X+Second_X))+10;
			}else
			{
				Coordinate_X=Integer.parseInt(First_X+Second_X, 16)+10;
			}
			if (Min_Y==0) {
				Min_Y=Coordinate_X;
			}else
			{
				if (Coordinate_X<Min_Y) {
					Min_Y=Coordinate_X;
				}
			}
			String First_Y=Integer.toHexString(bs[index-1]&0xFF);
			String Second_Y=Integer.toHexString(bs[index-2]&0xFF);
			Integer Coordinate_Y;
			if (Negative_describe_set.contains(First_Y+Second_Y)) {
				Coordinate_Y=Integer.parseInt(Negative_Values.get(First_Y+Second_Y))+10;
			}else
			{
				Coordinate_Y=Integer.parseInt(First_Y+Second_Y, 16)+10;
			}
			if (Max_X<Coordinate_Y) {
				Max_X=Coordinate_Y;
			}
			if (Min_X==0) {
				Min_X=Coordinate_Y;
			}
			else
			{
				if (Coordinate_Y<Min_X) {
					Min_X=Coordinate_Y;
				}
			}
			int next_j=1;
			for(int j=1;j<=This_Row_Die_Sum;j++)
			{
				if(Integer.toHexString(bs[index+next_j+1]&0xFF).equals("e0")||Integer.toHexString(bs[index+next_j+1]&0xFF).equals("c0"))
				{
					Mapping[Coordinate_Y][Coordinate_X]="S";
				}	
				else
					{
						Mapping[Coordinate_Y][Coordinate_X]=String.valueOf(Integer.parseInt(Integer.toHexString(bs[index+next_j]&0xFF), 16));
					}
					String bin_Infor=Mapping[Coordinate_Y][Coordinate_X];
				if (Bin_Summary.containsKey(bin_Infor)) {
						Bin_Summary.put(bin_Infor, Bin_Summary.get(bin_Infor)+1);
					}else
					{
						Bin_Summary.put(bin_Infor, 1);
					}
				
				Coordinate_X+=1;
				if (Max_Y<Coordinate_X) {
					Max_Y=Coordinate_X;
				}
				next_j+=2;
			}
		}
		Slot_number=new String(Slot_number_BT, "ASCII");
		Integer rightNumber=Integer.valueOf(Slot_number);
		waferid=new String(wafer_id_BT, "ASCII");
		if (waferid.trim().equals("")&&file.getName().substring(0, 5).equals("WAFER")) {						
			if (Lot_infor[6].toUpperCase().equals("SLOT")) {
				String Order="NA";
				if (Lot_infor[7].equals("25-1"))
				{
					Order="26";
					rightNumber=26-rightNumber;
				}
				else if (Lot_infor[7].equals("1-25"))
					Order="0";	
				waferid=Slot_Modify.Modify(Lot_R+"-"+Slot_number, Lot_infor[0],Order);								
			}else {
				waferid=Lot_R.split("\\.")[0]+"-"+Slot_number;
			}			
		}		
        StringBuffer temp = new StringBuffer(Test_Start_Time_BT.length * 2);  
        for (int i = 0; i < Test_Start_Time_BT.length; i++) {  
            temp.append((byte) ((Test_Start_Time_BT[i] & 0xf0) >>> 4));  
            temp.append((byte) (Test_Start_Time_BT[i] & 0x0f));  
        }   
        String Tstt=temp.toString();
        
        String year=(Tstt.substring(1,2)+Tstt.substring(3,4));
        String Mouth=Tstt.substring(5,6)+Tstt.substring(7,8);
        String Day=Tstt.substring(9,10)+Tstt.substring(11,12);
        String Hour=Tstt.substring(13,14)+Tstt.substring(15,16);
        String Minute=Tstt.substring(17,18)+Tstt.substring(19,20);
        Test_Start_Time=year+Mouth+Day+Hour+Minute;
        
        StringBuffer temp_Et = new StringBuffer(Test_End_Time_BT.length * 2);  
        for (int i = 0; i < Test_End_Time_BT.length; i++) {  
        	temp_Et.append((byte) ((Test_End_Time_BT[i] & 0xf0) >>> 4));  
        	temp_Et.append((byte) (Test_End_Time_BT[i] & 0x0f));  
        }   
        String Tett=temp_Et.toString();
        String eyear=(Tett.substring(1,2)+Tett.substring(3,4));
        String eMouth=Tett.substring(5,6)+Tett.substring(7,8);
        String eDay=Tett.substring(9,10)+Tett.substring(11,12);
        String eHour=Tett.substring(13,14)+Tett.substring(15,16);
        String eMinute=Tett.substring(17,18)+Tett.substring(19,20);
        Test_End_Time=eyear+eMouth+eDay+eHour+eMinute;
        
        Row_Sum=Integer.parseInt(Integer.toHexString(Row_Sum_BT&0xFF), 16); 
               
		int Total_Pass=0;
		int Total_Fail=0;
		int Total_Die=0;
		ArrayList<String> TestDieArray=new ArrayList<>();
		ArrayList<String> SkipAndMarkArray=new ArrayList<>();
		for (int i = Min_X; i < Max_X+1; i++) {
			for (int j = Min_Y; j < Max_Y+1; j++) {
				if (Mapping[i][j]!=null) {
					if (!Mapping[i][j].equals("0")&&!Mapping[i][j].equals("S"))
						{		
						Total_Die++;
						if (passBins.contains(Mapping[i][j])) {
							Total_Pass++;
						}else
						{
								Total_Fail++;
						}
						String Result_falg="0";
						if (PassDie_array_R.contains(Integer.valueOf(Mapping[i][j]))) {
							Result_falg="1";
						}
						TestDieArray.add(String.format("%4s", j-10)+String.format("%4s", i-10)+String.format("%4s", Mapping[i][j])+String.format("%4s", Mapping[i][j])+String.format("%4s", Result_falg));
						}
					 else
					   {
							if (Mapping[i][j].equals("0")) {
								SkipAndMarkArray.add(String.format("%4s", j-10)+String.format("%4s", i-10)+String.format("%4s", "M")+String.format("%4s", "M")+String.format("%4s", 0));	
							}else {
								SkipAndMarkArray.add(String.format("%4s", j-10)+String.format("%4s", i-10)+String.format("%4s", Mapping[i][j])+String.format("%4s", Mapping[i][j])+String.format("%4s", 0));
							}
					    }
				}
			}
		}
		String InnerCode=GetProberCardByWaferID.CallserviceForDoc(waferid);
		String[] TPP=GetTesterProberInformation.CallserviceForDoc(InnerCode).split(":");		
		PrintWriter out=new PrintWriter(new FileWriter(outfile));
		out.println("Lot ID:"+Lot_R);
		out.println("Customer Code:"+CustomerCode_R);
		out.println("Device Name:"+Device_R);
		out.println("Wafer ID:"+waferid);
		out.println("Operator:"+OP_R);
		out.println("Tester Program:"+Program_R);
		out.println("Tester ID:"+TPP[1]);
		out.println("Prober ID:"+TPP[2]);
		out.println("Prober Card ID:"+TPP[3]);
		out.println("CP Process:"+CP_R);
		out.println("Test Start Time:"+Test_Start_Time);
		out.println("Test End Time:"+Test_End_Time);
		
		Integer Year1=Integer.valueOf(Test_Start_Time.substring(0, 2));
		Integer Mouth1=Integer.valueOf(Test_Start_Time.substring(2,4));
		Integer Day1=Integer.valueOf(Test_Start_Time.substring(4,6));
		Integer Hour1=Integer.valueOf(Test_Start_Time.substring(6, 8));
		Integer Second1=Integer.valueOf(Test_Start_Time.substring(8,10));

		Integer Year2=Integer.valueOf(Test_End_Time.substring(0, 2));
		Integer Mouth2=Integer.valueOf(Test_End_Time.substring(2,4));
		Integer Day2=Integer.valueOf(Test_End_Time.substring(4,6));
		Integer Hour2=Integer.valueOf(Test_End_Time.substring(6, 8));
		Integer Second2=Integer.valueOf(Test_End_Time.substring(8,10));
		Integer Test_Time=-((Year1-Year2)*365*24*3600+(Mouth1-Mouth2)*30*24*3600+(Day1-Day2)*24*3600+(Hour1-Hour2)*3600+(Second1-Second2)*60);
		
		out.println("Gross Die:"+Total_Die);
		out.println("Pass Die:"+Total_Pass);
		out.println("Fail Die:"+Total_Fail);
		out.println("Wafer Yield:"+String.format("%4.2f", (double)Total_Pass*100/Total_Die)+"%");
		out.println("Retest Rate:");
		out.println("DataBase:"+DataBase_R);
		out.println("TestTime:"+String.format("%.2f", (float)(Test_Time/3600)));
		out.println("WaferID_read:"+WaferRead_R);
		out.println("Wafer_Sequence:"+WaferSequence_R);			
		out.println("WF_Size:"+WaferSize_R);
		out.println("Index X(mm):NA");
		out.println("Index Y(mm):NA");
		out.println("Map Cols:"+(Max_Y-Min_Y));
		out.println("Map Rows:"+Row_Sum);
		out.println("GPIB_Bin:"+GPIB_Bin_R);
		out.println("Notch:"+Nocth_R);	
		out.println("Slot:"+Slot_number);
		out.println("RightID:"+rightNumber);
		out.println("MinX:"+(Min_Y-10));
		out.println("MinY:"+(Min_X-10));
		out.println("");
		out.println("");
		out.println("RawData");
		for (String DieInfor : TestDieArray) {
			out.println(DieInfor);
		}
		out.println("DataEnd");
		out.println("SkipAndMarkStart");
		for (String SKipInfor : SkipAndMarkArray) {
			out.println(SKipInfor);
		}
		out.println("SkipAndMarkEnd");
		out.println("");
		out.println("Bin Summary");
		out.println("");
		@SuppressWarnings({ "unchecked", "rawtypes" })
		TreeSet<String> keys=new TreeSet(Bin_Summary.keySet());
		for (String key : keys) {
			if (!key.equals("0")&&!key.equals("S")) {
				out.print("Bin ");
				out.print(key);
				out.print(":");
				out.println(Bin_Summary.get(key));
			}
		}
		out.flush();
		out.close();
		return waferid.trim();
	}
}
