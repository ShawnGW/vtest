package mesinfor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeSet;

import mestools.GetLotConfigFromMes;
import parseMapping.TskProberMappingParse;
import properties.GetRawdataProperties;

public class test {
	public static void main(String[] args) throws IOException {
		GetMesInformations getMesInformations=new GetMesInformations();
		GetRawdataProperties getRawdataProperties=new GetRawdataProperties();
		HashMap<String, String> resultMap=getMesInformations.getInfor(new GetLotConfigFromMes("VNPMB44023"), GetMesInformations.TYPE_CONFIG);
		HashMap<String, String> configMap=getRawdataProperties.getConfigs();
		LinkedHashMap<String, String> properties=getRawdataProperties.getProperties();
		Set<String> configSet=configMap.keySet();
		for (String config : configSet) {
			if (resultMap.containsKey(config)) {
				properties.put(configMap.get(config), resultMap.get(config));
			}else
			{
				if (properties.get(configMap.get(config)).equals(config)) {
					properties.put(configMap.get(config), "NA");
				}
			}
		}
		
		/***/
		
		HashMap<Integer, Integer> Bin_summary_Map=new HashMap<>();
		HashMap<String, String> skipAndMarkDieMap=new HashMap<>();
		HashMap<String, String> DieMap=new HashMap<>();
		TskProberMappingParse tskProberMappingParse=new TskProberMappingParse();
		HashMap<String, String> resultMap2=tskProberMappingParse.Get(new File("D:/BFSCSV/001.HV7GVPG"),0,Bin_summary_Map,DieMap,skipAndMarkDieMap);
		Set<String> keyset=new TreeSet<>(resultMap2.keySet());
		for (String key : keyset) {
			properties.put(key, resultMap2.get(key));
		}
		
		
		/***/
		Set<String> propertiesSet=properties.keySet();
		for (String propertie : propertiesSet) {
			System.out.print(propertie+" : ");
			System.out.println(properties.get(propertie));
		}
	}
}
