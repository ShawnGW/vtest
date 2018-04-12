package Tools;

public class ModifyTime {
	public static String Modify(String Time) {
		StringBuffer final_time=new StringBuffer();
		String Year="20"+Time.substring(0,2);
		String Mouth=Time.substring(2,4);
		String Day=Time.substring(4,6);
		String Hour=Time.substring(6,8);
		String Minute=Time.substring(8,10);
		final_time.append(Year+"-");
		final_time.append(Mouth);
		final_time.append("-");
		final_time.append(Day);
		final_time.append(" ");
		final_time.append(Hour);
		final_time.append(":");
		final_time.append(Minute);
		final_time.append(":00");
		return final_time.toString();
	}
	public static String Modify2(String Time) {
		StringBuffer final_time=new StringBuffer();
		String Year="20"+Time.substring(0,2);
		String Mouth=Time.substring(2,4);
		String Day=Time.substring(4,6);
		String Hour=Time.substring(6,8);
		String Minute=Time.substring(8,10);
		final_time.append(Year+"/");
		final_time.append(Mouth);
		final_time.append("/");
		final_time.append(Day);
		final_time.append(" ");
		final_time.append(Hour);
		final_time.append(":");
		final_time.append(Minute);
		final_time.append(":00");
		return final_time.toString();
	}
}
