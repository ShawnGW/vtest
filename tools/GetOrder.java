package tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GetOrder {
	public static ArrayList<File> Order(File[] Filelist)
	{
		ArrayList<File> files=new ArrayList<>();
		ArrayList<Integer> File_Number=new ArrayList<>();
		HashMap<Integer, File> Number_File_Map=new HashMap<>();
		for (int i = 0; i < Filelist.length; i++) {
			String[] NameTokens=Filelist[i].getName().split("_");
			Integer Length=NameTokens.length;
			String RP=NameTokens[Length-4].substring(2, 3);
			String Test_Start_Time_R= (NameTokens[Length-2]+NameTokens[Length-1].substring(0, 6));
			Integer Year=Integer.valueOf(Test_Start_Time_R.substring(2, 4));
			Integer Mouth=Integer.valueOf(Test_Start_Time_R.substring(4,6));
			Integer Day=Integer.valueOf(Test_Start_Time_R.substring(6,8));
			Integer Hour=Integer.valueOf(Test_Start_Time_R.substring(8, 10));
			Integer Minute=Integer.valueOf(Test_Start_Time_R.substring(10,12));
			Integer Second=Integer.valueOf(Test_Start_Time_R.substring(12,14));
			Integer Time=Year*365*24*60*10+Mouth*30*24*10*10+Day*24*10*10+Hour*10*10+Minute*10+Second;
			Integer Fianl_time=Integer.valueOf(RP+Time.toString());
			Number_File_Map.put(Fianl_time, Filelist[i]);
		}
		Integer[] NUmbers=Number_File_Map.keySet().toArray(new Integer[File_Number.size()]);
		sort(NUmbers);		
		for (int i = 0; i < NUmbers.length; i++) {
			files.add(Number_File_Map.get(NUmbers[i]));
		}
		return files;
	}
	public static void sort(Integer data[]) {  
        for (int i = 0; i < data.length -1; i++) {  
            for (int j = 0; j < data.length - i - 1; j++) {  
                if (data[j] > data[j + 1]) {  
                    int temp = data[j];  
                    data[j] = data[j + 1];  
                    data[j + 1] = temp;  
                }  
  
            }  
        }  
    } 
	public static void main(String[] args) {
		File[] filelist=new File("D:/DATA/1").listFiles();
		ArrayList<File> files=GetOrder.Order(filelist);
		for (File file : files) {
			System.out.println(file.getName());
		}
	}
}
