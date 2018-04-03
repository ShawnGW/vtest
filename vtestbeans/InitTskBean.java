package vtestbeans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import mesinfor.GetInnerLot;
import mesinfor.GetMesInformations;
import mesinfor.GetProcessYield;
import mesinfor.GetTesterAndProber;
import mesinfor.getWaferIdYield;
import mestools.GetLotConfigFromMes;
import parseMapping.TskProberMappingParse;
import properties.GetRawdataProperties;
import rawdata.Rawdata;

public class InitTskBean implements Rawdata{

	private LinkedHashMap<String, String> properties=new LinkedHashMap<>();
	private HashMap<String, String> DieMap=new HashMap<>();
	private HashMap<String, String> skipAndMarkDieMap=new HashMap<>();
	private HashMap<Integer, Integer> Bin_summary_Map=new HashMap<>();
		
	public InitTskBean(String customerLot,File mapping,HashMap<String, String> customerLotConfig,HashMap<String, String> basicsInfor) throws IOException {
		GetTesterAndProber getTesterAndProber=new GetTesterAndProber();
		GetMesInformations getMesInformations=new GetMesInformations();
		GetRawdataProperties getRawdataProperties=new GetRawdataProperties();
		TskProberMappingParse tskProberMappingParse=new TskProberMappingParse();
		getWaferIdYield getWaferIdYield=new getWaferIdYield();
		
		//get inner lot ; get tsk mapping information ;
//		HashMap<String, String> customerLotConfig=getMesInformations.getSlotAndSequence(customerLot);
		int gpibBin=Integer.valueOf(customerLotConfig.get("gpib"));		
		HashMap<String, String> tskMappingResult=tskProberMappingParse.Get(mapping,gpibBin,Bin_summary_Map,DieMap,skipAndMarkDieMap);
		String waferid=tskMappingResult.get("Wafer ID");
		waferid=SlotModify.modify(customerLotConfig, waferid);
		String innerlot=GetInnerLot.get(waferid);	
		
		//init properties by mes config
		String cp=tskMappingResult.get("CP Process");
		HashMap<String, String> resultMap=getMesInformations.getInfor(new GetLotConfigFromMes(innerlot), GetMesInformations.TYPE_CONFIG);
		HashMap<String, String> configMap=getRawdataProperties.getConfigs();
		 properties=getRawdataProperties.getProperties();
		Set<String> configSet=configMap.keySet();
		for (String config : configSet) {
			if (config.endsWith("*")) {
				config=config.substring(0, config.length()-1);
				for (String propertie : configSet) {
					if (propertie.contains(config)) {
						if (resultMap.get(config).contains(cp+"T0")) {
							String[] content=resultMap.get(config).split(":");
							properties.put(configMap.get(config+"*"), content.length>1?content[1]:content[0]);
						}
					}
				}
				config=config+"*";
				if (properties.get(configMap.get(config)).equals(config)) {
					properties.put(configMap.get(config), "NA");
				}
			}else {
				if (resultMap.containsKey(config)) {
					properties.put(configMap.get(config), resultMap.get(config));
				}else
				{
					if (properties.get(configMap.get(config)).equals(config)) {
						properties.put(configMap.get(config), "NA");
					}
				}
			}
		}
		
		//modify some properties by CP
		ModifyProperties.modify(properties,cp);
		
		//init properties by mapping infor
		Set<String> keyset=tskMappingResult.keySet();
		for (String key : keyset) {
			String value=tskMappingResult.get(key);
			if (!value.equals("NA")) {
				properties.put(key, value);	
			}		
		}
		//modify wafer id
		properties.put("Wafer ID", waferid);
		
		// get tester,prober,probercard; init properties;
		HashMap<String, String> testerAndProber=getTesterAndProber.Get(innerlot, cp,true);
		Set<String> testersMap=testerAndProber.keySet();
		for (String key : testersMap) {
			String value=testerAndProber.get(key);
			if (!value.equals("NA")) {
				properties.put(key, value);
			}
		}
		
		//modify process yield;
		String processYield=GetProcessYield.getYield(properties.get("Process Yield"), cp);
		properties.put("Process Yield", processYield);
		
		//init each cp process
		String lot=properties.get("Lot ID");		
		HashMap<String, String> cpYieldMap=getWaferIdYield.get(lot, waferid);
		Set<String> yieldMap=cpYieldMap.keySet();
		StringBuffer SB=new StringBuffer();
		for (String process : yieldMap) {
			SB.append(process+"&"+cpYieldMap.get(process)+"%;");
		}
		String cpYields=SB.toString();
		if (cpYields.equals("")) {
			cpYields=properties.get("CP Process")+"&"+properties.get("Wafer Yield")+";";
		}
		properties.put("CP Yields", cpYields);
		
		//init right id base sequnce and slot
		if (properties.get("Wafer_Sequence").equals("25-1")) {
			properties.put("RightID", String.valueOf(26-Integer.valueOf(properties.get("Slot"))));
		}else {
			properties.put("RightID", properties.get("Slot"));
		}
		
		basicsInfor.put("customerCode", properties.get("Customer Code"));
		basicsInfor.put("device", properties.get("Device Name"));
		basicsInfor.put("lot", properties.get("Lot ID"));
		basicsInfor.put("waferId", properties.get("Wafer ID"));
		basicsInfor.put("cp", properties.get("CP Process"));
	}
	@Override
	public LinkedHashMap<String, String> getProperties() {
		// TODO Auto-generated method stub
		return properties;
	}
	@Override
	public HashMap<String, String> getTestDieMap() {
		// TODO Auto-generated method stub
		return DieMap;
	}
	@Override
	public HashMap<String, String> getMarkAndSkipDieMap() {
		// TODO Auto-generated method stub
		return skipAndMarkDieMap;
	}
	@Override
	public TreeMap<Integer, Integer> getbinSummary() {
		// TODO Auto-generated method stub
		return new TreeMap<>(Bin_summary_Map);
	}

}
