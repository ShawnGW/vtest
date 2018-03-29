package resultCheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class RawdataCheck {
	private HashMap<Integer, Integer> actualSummaryMap=new HashMap<>();
	private HashMap<Integer, Integer> theroySummaryMap=new HashMap<>();
	private ArrayList<String> markAndSkipDieMap=new ArrayList<>();
	private ArrayList<String> testDiesArray=new ArrayList<>();
	private HashMap<String, String> properties=new HashMap<>();

	public boolean check(File rawdata,HashMap<String, Boolean> checkedProperties,HashMap<String, String> log)
	{
		boolean generateFlag=initInfors(rawdata,log);
		if (!generateFlag) {
			return false;
		}
		boolean propertieFlag=propertiesCheck(properties, checkedProperties,log);
		if (!propertieFlag) {
			return false;
		}
		boolean testDieFlag=testDieCheck(testDiesArray, actualSummaryMap,log);
		if (!testDieFlag) {
			return false;
		}
		boolean markAndSkipFlag=markAndSkipDieCheck(markAndSkipDieMap,log);
		if (!markAndSkipFlag) {
			return false;
		}
		boolean binSummaryFlag=binSummaryCheck(theroySummaryMap, actualSummaryMap,log);
		if (!binSummaryFlag) {
			return false;
		}
		return true;
	}
	private boolean initInfors(File Rawdata,HashMap<String, String> log)
	{
		log.put("Type init", "generate infor");
		try {
			boolean testDieStartFlag=false;
			boolean markAndSkipFlag=false;
			boolean startFlag=true;
			boolean binSummaryFlag=false;
			FileReader reader=new FileReader(Rawdata);
			BufferedReader bufferedReader=new BufferedReader(reader);
			String content;
			while((content=bufferedReader.readLine())!=null)
			{
				if (content.contains("Bin Summary")) {
					binSummaryFlag=true;
					continue;
				}
				if (content.contains("SkipAndMarkStart")) {
					markAndSkipFlag=true;
					continue;
				}
				if (content.contains("SkipAndMarkEnd")) {
					markAndSkipFlag=false;
					continue;
				}
				if (content.contains("DataEnd")) {
					testDieStartFlag=false;
					continue;
				}
				if (content.contains("RawData")) {
					startFlag=false;
					testDieStartFlag=true;
					continue;
				}
				if (startFlag) {
					String[] infors=content.split(":");
					String key=infors[0];
					String value=infors[1];
					properties.put(key, value);
				}
				if (testDieStartFlag) {
					testDiesArray.add(content);				
				}
				if (markAndSkipFlag) {
					markAndSkipDieMap.add(content);
				}
				if (binSummaryFlag) {
					if (content.contains("Bin")) {
						String[] infor=content.split(":");
						Integer bin=Integer.valueOf(infor[0].substring(3).trim());
						Integer value=Integer.valueOf(infor[1]);
						theroySummaryMap.put(bin, value);
					}
				}
				
			}
			bufferedReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			log.put("Generate value", e.getMessage());
			return false;
		}
		return true;
	}	
	private boolean propertiesCheck(HashMap<String, String> properties,HashMap<String, Boolean> checkedProperties,HashMap<String, String> log)
	{
		log.put("Type properties", "properties check");
		Set<String> checkSet=checkedProperties.keySet();
		for (String checkItem : checkSet) {
			if (properties.containsKey(checkItem)) {
				if (!properties.get(checkItem).equals("NA")) {
					checkedProperties.put(checkItem, true);
				}
				else {
					log.put(checkItem, "miss propertie "+checkItem);
				}
			}else {
				log.put("Properties value", "miss propertie "+checkItem);
			}
		}
		Collection<Boolean> collection=checkedProperties.values();			
		for (Boolean flag : collection) {
			if (!flag) {			
				return false;
			}
		}	
		return true;
	}
	private boolean binSummaryCheck(HashMap<Integer, Integer> theroySummaryMap,HashMap<Integer, Integer> actualSummaryMap,HashMap<String, String> log)
	{
		log.put("Type binSummary", "binSummary check");
		if (theroySummaryMap.keySet().size()!=actualSummaryMap.keySet().size()) {
			log.put("Bin value", "number not Match");
			return false;
		}
		Set<Integer> actualSet=actualSummaryMap.keySet();
		for (Integer bin : actualSet) {
			if (!theroySummaryMap.containsKey(bin)) {
				log.put("Bin value", "actual contains bin:"+bin+" but Bin Summary not contains!");
				return false;
			}else {
				Integer diff=actualSummaryMap.get(bin)-theroySummaryMap.get(bin);
				if (diff!=0) {
					log.put("Bin value", "actual contains bin:"+bin+" but Bin Summary number not match with it!");
					return false;
				}
			}
		}
		return true;
	}
	private boolean markAndSkipDieCheck(ArrayList<String> markAndSkipDieMap,HashMap<String, String> log)
	{
		log.put("Type mark and Skip Die", "Mark And Skip Die Check");
		if (markAndSkipDieMap.size()==0) {
			return true;
		}
		for (String die : markAndSkipDieMap) {
			if (die.length()!=20) {
				log.put("Mark value", "there are diffrent formart (maybe coordinate or value is wrong)");
				return false;
			}
		}
		return true;
	}
	private boolean testDieCheck(ArrayList<String> testDiesArray,HashMap<Integer, Integer> binSummaryMap,HashMap<String, String> log)
	{
		log.put("Type test die", "Test Die Check");
		if (testDiesArray.size()==0) {
			return false;
		}
		for (String die : testDiesArray) {
			if (die.length()!=20) {
				log.put("Test Die value", "there are diffrent formart (maybe coordinate or value is wrong)");
				return false;
			}else {
				try {
					Integer value=Integer.valueOf(die.substring(12, 16).trim());
					if (binSummaryMap.containsKey(value)) {
						binSummaryMap.put(value, binSummaryMap.get(value)+1);
					}else {
						binSummaryMap.put(value, 1);
					}
				} catch (Exception e) {
					// TODO: handle exception
					log.put("Test Die value", "unkown exception!");
					log.put("Test Die value", e.getMessage());
					return false;
				}				
			}
		}
		return true;
	}
}
