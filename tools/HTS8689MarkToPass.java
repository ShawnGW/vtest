package tools;

import java.util.HashMap;
import java.util.Set;

public class HTS8689MarkToPass {
	public static Boolean dieCheck(Integer coordinateX,Integer coordinateY,String[][] map,Integer row)
	{
		boolean upCheck=false;
		boolean downCheck=false;
		for (int i = coordinateX-row; i < coordinateX+row; i++) {
			if (i<coordinateX) {
				try {
					if (map[i][coordinateY]!=null) {
						downCheck=true;
						i=coordinateX;
					};
				} catch (Exception e) {
					// TODO: handle exception
				}
			}else  {
				try {
					if (map[i][coordinateY]!=null) {
						upCheck=true;
						break;
					};
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			
		}
		if (downCheck&&upCheck) {
			return true;
		}else {
			return false;
		}
	}
	public static HashMap<String, String> modifyMap(HashMap<String, String> Final_Bin_Map)
	{
		HashMap<String, String> Final_Bin_Map_Modify=new HashMap<String, String>();
		String[][] Map=new String[800][800];
		Set<String> keyset=Final_Bin_Map.keySet();
		Integer MaxX=0;
		Integer MaxY=0;
		for (String key : keyset) {
			Integer coordinateY=Integer.valueOf(key.split(":")[0]);
			Integer coordinateX=Integer.valueOf(key.split(":")[1]);
			if(coordinateY>MaxY)
			{
				MaxY=coordinateY;
			}
			if(coordinateX>MaxX)
			{
				MaxX=coordinateX;
			}
			Map[coordinateX][coordinateY]="R";
		}
		for (int i = 0; i < MaxX+1; i++) {
			for (int j = 0; j < MaxY+1; j++) {
				if (Map[i][j]==null&&dieCheck(i, j, Map, MaxX)) {
					Map[i][j]="1";
				}
			}
		}
		for (int i = 0; i < MaxX+1; i++) {
			for (int j = 0; j < MaxY+1; j++) {
				if (Map[i][j]!=null) {
					if (Final_Bin_Map.containsKey(j+":"+i)) {
						Final_Bin_Map_Modify.put(j+":"+i, Final_Bin_Map.get(j+":"+i));
					}else {
						Final_Bin_Map_Modify.put(j+":"+i, "1:1");
					}
				}
			}
		}
		return Final_Bin_Map_Modify;
	}
}
