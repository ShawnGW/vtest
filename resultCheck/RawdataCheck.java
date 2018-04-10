package resultCheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	
	public static void main(String[] args) throws IOException {
		RawdataCheck rawdataCheck=new RawdataCheck();
		HashMap<String, String> log=new HashMap<>();	
		System.out.println(rawdataCheck.check(new File("D:/TempRawdata/FXQVPIWSKOXRHUF.raw"), log));;
		Set<String> logset=log.keySet();
		for (String string : logset) {
			System.out.println(string+" : "+log.get(string));
		}
	}
	
	public boolean check(File rawdata,HashMap<String, String> log) throws IOException
	{
		InitCheckItems checkItems=new InitCheckItems();
		HashMap<String, String> checkedProperties=checkItems.getProperties();
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
		Set<String> logItems=log.keySet();
		for (String item : logItems) {
			if (item.contains("value")) {
				return false;
			}
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
	
	private boolean propertiesCheck(HashMap<String, String> properties,HashMap<String, String> checkedProperties,HashMap<String, String> log)
	{
		log.put("Type properties", "properties check");
		Set<String> checkSet=checkedProperties.keySet();
		for (String checkItem : checkSet) {	
			String finItem=checkItem;
			String propertiesValue=checkedProperties.get(checkItem);
			if (propertiesValue.equals("NA")) {
				if (checkItem.contains("@")) {
					String[] customers=checkItem.split("@");
					ArrayList<String> customerArray=new ArrayList<>();
					for (int i = 1; i < customers.length; i++) {
						customerArray.add(customers[i]);
					}
					if (customerArray.contains(properties.get("Customer Code"))) {
						checkItem=checkItem.split("@")[0];
						if (properties.containsKey(checkItem)) {
							String value=properties.get(checkItem);
							if (!value.equals("NA")) {
								checkedProperties.put(finItem, value);
							}
							else {
								log.put("Properties value", "miss propertie:"+checkItem);
							}
						}else {
							log.put("Properties value", "miss propertie:"+checkItem);
						}
					}else {
						checkedProperties.put(checkItem, "no need checking!");
					}
				}else {
					if (properties.containsKey(checkItem)) {
						String value=properties.get(checkItem);
						if (!value.equals("NA")) {
							checkedProperties.put(checkItem, value);
						}
						else {
							log.put(checkItem, "miss propertie:"+checkItem);
						}
					}else {
						log.put("Properties value", "miss propertie:"+checkItem);
					}
				}
			}else {
				if (checkItem.contains("@")) {
					String[] customers=checkItem.split("@");
					ArrayList<String> customerArray=new ArrayList<>();
					for (int i = 1; i < customers.length; i++) {
						customerArray.add(customers[i]);
					}
					if (customerArray.contains(properties.get("Customer Code"))) 
					{
						if (propertiesValue.contains("&")) {
							Integer sum=Integer.valueOf(properties.get(checkItem));
							String[] parts=propertiesValue.split("&");
							Integer total=0;
							for (String part : parts) {
								Integer value=Integer.valueOf(properties.get(part));
								total+=value;
							}
							int diff=total-sum;
							if (diff==0) {
								checkedProperties.put(checkItem, String.valueOf(total));
							}else {
								log.put("Properties value", checkItem+"  check fail!");
							}
						}else {
							if (properties.get(checkItem).equals(properties.get(propertiesValue))) {
								checkedProperties.put(checkItem, properties.get(propertiesValue));
							}else {
								log.put("Properties value", checkItem+" is not equals "+propertiesValue);
							}
						}
					}else {
						checkedProperties.put(checkItem, "no need checking!");
					}
				}else {
					if (propertiesValue.contains("&")) {
						Integer sum=Integer.valueOf(properties.get(checkItem));
						String[] parts=propertiesValue.split("&");
						Integer total=0;
						for (String part : parts) {
							Integer value=Integer.valueOf(properties.get(part));
							total+=value;
						}
						int diff=total-sum;
						if (diff==0) {
							checkedProperties.put(checkItem, String.valueOf(total));
						}else {
							log.put("Properties value", checkItem+"  check fail!");
						}
					}else {					
							try {
								if (properties.get(checkItem).equals(properties.get(propertiesValue))) {
									checkedProperties.put(checkItem, properties.get(propertiesValue));
								}else {
									log.put("Properties value", checkItem+" is not equals "+propertiesValue);
								}
							} catch (Exception e) {
								// TODO: handle exception
								log.put("Properties value", checkItem+" is not equals "+propertiesValue);
							}						
					}
				}			
			}
		}
		Collection<String> collection=checkedProperties.values();			
		for (String flag : collection) {
			if (flag.equals("NA")) {	
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
