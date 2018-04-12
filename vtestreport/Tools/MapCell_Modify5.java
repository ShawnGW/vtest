package Tools;

public class MapCell_Modify5 {
	public static String Modify(String Value) {
		if (Value==null) {	
			return ".";
		}
		if (Value.equals("S")||Value.equals("M")) {
			return ".";
		}
		if (Value.equals("1"))
			return "A";
		else
			return "X";
	}
}
