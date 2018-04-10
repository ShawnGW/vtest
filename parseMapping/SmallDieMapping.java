package parseMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SmallDieMapping {
	public static void main(String[] args) throws IOException {
		GetMapInfor(new File("D:/DATA/AP17405/AP17405-23-H11.RMP"));
	}
	public static ArrayList<Object> GetMapInfor(File file) throws IOException
	{
		ArrayList<Object> Result_RMP=new ArrayList<>();
		HashMap<Integer, Integer> Bin_Summary=new HashMap<>();
		HashMap<String, Integer> TestDie_Map=new HashMap<>();
		HashMap<String, String> SkipAndMarkDie_Map=new HashMap<>();
		FileInputStream fins=new FileInputStream(file);
		byte[] bs=new byte[1000000];
		fins.read(bs);
		fins.close();
		Integer Rows=Integer.parseInt(Integer.toHexString(bs[0]&0xFF), 16);
			
		Integer Map_ROW_Start_index=2;
		ArrayList<Integer> MapRow_Start_indexs=new ArrayList<Integer>();
		
		int Total=0;
		for (int i = 0; i < bs.length; i++) {			
			if (Integer.toHexString(bs[i]&0xFF).equals("80")) {
				if (bs[i+1]==0&&bs[i+2]==0&&bs[i+3]==0&&bs[i+4]==0&&bs[i+5]==0&&bs[i+6]==0) {
					break;
				}
			}	
			Total=i-3;
		}
		while(Map_ROW_Start_index<Total)
		{
			String FirstIndex=Integer.toHexString(bs[Map_ROW_Start_index+5]&0xFF);
			String SecondIndex=Integer.toHexString(bs[Map_ROW_Start_index+4]&0xFF);
			if (SecondIndex.length()==1) {
				SecondIndex="0"+SecondIndex;
			}
			String Sum_Hex=FirstIndex+SecondIndex;
			Integer Sum=Integer.parseInt(Sum_Hex, 16);
			MapRow_Start_indexs.add(Map_ROW_Start_index);
			Map_ROW_Start_index+=Sum*5+6;			
		}
		
		Integer Min_X=0;
		Integer Min_Y=0;
		Integer Max_Col=0;	
		boolean Min_X_Falg=true;
		boolean Min_Y_Flag=true;
		boolean Max_Col_Flag=true;
		for (Integer index : MapRow_Start_indexs) {
			String Coordinate_Y_First=Integer.toHexString(bs[index+1]&0xFF);
			String Coordinate_Y_Second=Integer.toHexString(bs[index]&0xFF);
			if (Coordinate_Y_Second.length()==1) {
				Coordinate_Y_Second="0"+Coordinate_Y_Second;
			}
			String Coordinate_X_First=Integer.toHexString(bs[index+3]&0xFF);
			String Coordinate_X_Second=Integer.toHexString(bs[index+2]&0xFF);
			if (Coordinate_X_Second.length()==1) {
				Coordinate_X_Second="0"+Coordinate_X_Second;
			}
			String Coordinate_X_Str=Coordinate_X_First+Coordinate_X_Second;
			Integer Coordinate_X=Integer.parseInt(Coordinate_X_Str, 16);
			
			String Coordinate_Y_Str=Coordinate_Y_First+Coordinate_Y_Second;
			Integer Coordinate_Y=Integer.parseInt(Coordinate_Y_Str, 16);
			
			if (Min_X_Falg) {
				Min_X=Coordinate_X;
				Min_X_Falg=false;
			}
			if (Min_X>Coordinate_X) {
				Min_X=Coordinate_X;
			}
			if (Min_Y_Flag) {
				Min_Y=Coordinate_Y;
				Min_Y_Flag=false;
			}
			if (Min_Y>Coordinate_Y) {
				Min_Y=Coordinate_Y;
			}
			String FirstIndex=Integer.toHexString(bs[index+5]&0xFF);
			String SecondIndex=Integer.toHexString(bs[index+4]&0xFF);
			if (SecondIndex.length()==1) {
				SecondIndex="0"+SecondIndex;
			}
			String Sum_Hex=FirstIndex+SecondIndex;
			Integer Sum=Integer.parseInt(Sum_Hex, 16);
			if (Max_Col_Flag) {
				Max_Col=Sum;
				Max_Col_Flag=false;
			}
			if (Sum>Max_Col) {
				Max_Col=Sum;
			}
			Integer DieStartIndex=index+6;
			for (int i = 0; i < Sum; i++) {
				String Value=Integer.toHexString(bs[DieStartIndex]&0xFF);			
				if (Value.equals("0")) {
					SkipAndMarkDie_Map.put(Coordinate_Y+":"+Coordinate_X, "M");
				}else {
					Integer NumberValue=Integer.parseInt(Value, 16);					
					if (Bin_Summary.containsKey(NumberValue)) {
						Bin_Summary.put(NumberValue, Bin_Summary.get(NumberValue)+1);
					}else {
						Bin_Summary.put(NumberValue, 1);
					}
					TestDie_Map.put(Coordinate_Y+":"+Coordinate_X, NumberValue);
				}
				DieStartIndex+=5;
				Coordinate_Y++;
			}	
		}
		Result_RMP.add(Rows);
		Result_RMP.add(Max_Col);
		Result_RMP.add(Min_X);
		Result_RMP.add(Min_Y);
		Result_RMP.add(TestDie_Map);
		Result_RMP.add(SkipAndMarkDie_Map);
		Result_RMP.add(Bin_Summary);
		
		return Result_RMP;
//		Integer Rows_R=(Integer) Result_RMP.get(0);
//		Integer Cols_R=(Integer) Result_RMP.get(1);
//		Integer Min_X_R=(Integer) Result_RMP.get(2);
//		Integer Min_Y_R=(Integer) Result_RMP.get(3);
//		@SuppressWarnings("unchecked")
//		HashMap<Integer, Integer> Bin_Summary_R=(HashMap<Integer, Integer>) Result_RMP.get(6);
//		@SuppressWarnings("unchecked")
//		HashMap<String, Integer> TestDie_Map_R=(HashMap<String, Integer>) Result_RMP.get(4);
//		@SuppressWarnings("unchecked")
//		HashMap<String, String> SkipAndMarkDie_Map_R=(HashMap<String, String>) Result_RMP.get(5);
	}
}	
