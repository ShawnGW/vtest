package vtestbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyProperties {
	
	public static void modify(LinkedHashMap<String, String> properties,HashMap<Integer, Integer> Bin_summary_Map)
	{
		int passDies=0;
		int failDies=0;
		ArrayList<String> passBinArray=new ArrayList<>();
		try {
			String[] passbins=properties.get("Pass Bins").split(",");		
			for (String passbin : passbins) {
				passBinArray.add(passbin);
			}
		} catch (Exception e) {
			// TODO: handle exception
			passBinArray.add("1");
		}
		
		Set<Integer> binSummarySet=Bin_summary_Map.keySet();
		for (Integer bin : binSummarySet) {
			if (passBinArray.contains(String.valueOf(bin))) {
				passDies+=Bin_summary_Map.get(bin);
			}else {
				failDies+=Bin_summary_Map.get(bin);
			}
		}
		properties.put("Gross Die", String.valueOf(passDies+failDies));
		properties.put("Pass Die", String.valueOf(passDies));
		properties.put("Fail Die", String.valueOf(failDies));
		properties.put("Wafer Yield", String.format("%4.2f", (double)passDies*100/(passDies+failDies))+"%");
	}
	public static void  modify(LinkedHashMap<String, String> properties,String cp) {
		String regex="CP[1-9]{1}:(.*{1,})";
		String regex2="CP[1-9]{1}(.*){1,}:(.*{1,})";
		Pattern pattern=Pattern.compile(regex);
		Pattern pattern2=Pattern.compile(regex2);
		Set<String> propertieSet=properties.keySet();
		for (String propertie : propertieSet) {
			if (propertie.startsWith("@")) {
				String valueSum=properties.get(propertie);
				String[] values=valueSum.split(";");
				String finValue=properties.get(propertie);
				for (String  value: values) {
					Matcher matcher=pattern.matcher(value);
					Matcher matcher2=pattern2.matcher(value);
					if (matcher.find()) {
						if (value.contains(cp)) {
							finValue=matcher.group(1);
						}
					}
					if (matcher2.find()) {
						if (value.contains(cp)) {
							finValue=matcher.group(1);
						}
					}
				}
				properties.put(propertie, finValue);
			}
		}
	}
}
