package generateMain;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import mesinfor.WaferIdBinSummaryWrite;
import rawdata.GenerateRawdata;
import rawdata.Rawdata;
import resultCheck.RawdataCheck;
import tools.DeleteFile;
import tools.GetOrder;
import tools.GetShieldDeviceFromStdf;
import vtestbeans.InitStdfMappingBean;

public class StdfMappingGenerateRawdata {
	private  final File dataSourceStdf=new File("/TesterData/STDF_Text");
	public static void main(String[] args) {
		StdfMappingGenerateRawdata stdfMappingGenerateRawdata=new StdfMappingGenerateRawdata();
		stdfMappingGenerateRawdata.generate();
	}
	public void generate() {
		if (!dealMappings()) {
			return;
		}
		File[] customerCodeFiles=dataSourceStdf.listFiles();
		for (File customerCodeFile : customerCodeFiles) {
			if (directoryCheck(customerCodeFile)) {
				String customerCode=customerCodeFile.getName();
				File[] deviceFiles=customerCodeFile.listFiles();
				for (File deviceFile : deviceFiles) {
					if (directoryCheck(deviceFile)) {
						String device=deviceFile.getName();
						File[] lotFiles=deviceFile.listFiles();
						for (File lotFile : lotFiles) {
							if (directoryCheck(lotFile)) {
								String lot=lotFile.getName();
								File[] cpFiles=lotFile.listFiles();
								for (File cpFile : cpFiles) {
									if (directoryCheck(cpFile)) {
										String cp=cpFile.getName();
										File[] waferIdFiles=cpFile.listFiles();
										for (File waferIdFile : waferIdFiles) {
											if (directoryCheck(waferIdFile)) {
												String waferId=waferIdFile.getName();
												File[] waferIdTexts=waferIdFile.listFiles();
												ArrayList<File> waferIdOrderList=GetOrder.Order(waferIdTexts);
												HashMap<String, String> inforsResultMap=getWaferIdInfors(waferIdOrderList.get(0));
												inforsResultMap.put("customerCode", customerCode);
												inforsResultMap.put("device", device);
												inforsResultMap.put("lot", lot);
												inforsResultMap.put("cp", cp);
												inforsResultMap.put("waferId", waferId);												
												generateRawdata(customerCode, waferIdOrderList, inforsResultMap, 0);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
	}
	private  boolean dealMappings()
	{
		if (!dataSourceStdf.exists()) {
			return false;
		}
		return true;
	}
	private HashMap<String, String> getWaferIdInfors(File file)
	{	
		HashMap<String, String> inforsResultMap=new HashMap<>();
		Integer TesterID=6;
		boolean flag=true;
		String[] nameTokens=file.getName().split("_");
		Integer Length=nameTokens.length;
		for (int j2 = 0; j2 < nameTokens.length; j2++) {
			if (nameTokens[j2].contains("TT")&&nameTokens[j2].length()==6) {
				if (flag) {
					TesterID=j2;
					flag=false;
				}
			}
		}
		String Tester=nameTokens[TesterID];
		String Prober=nameTokens[TesterID+1];
		String Test_Start_Time=nameTokens[Length-2]+nameTokens[Length-1].substring(0,6);
		String Operater=nameTokens[Length-3];
		StringBuffer ProberCardID=new StringBuffer(nameTokens[TesterID+2]);
		for (int j2 = TesterID+3; j2< Length-5; j2++) {
			ProberCardID.append("_");
			ProberCardID.append(nameTokens[j2]);
		}
		String ProberCard=ProberCardID.toString();
		inforsResultMap.put("tester",Tester);
		inforsResultMap.put("prober",Prober);
		inforsResultMap.put("proberCard",ProberCard);
		inforsResultMap.put("op",Operater);
		inforsResultMap.put("testStartTime",Test_Start_Time);
		return inforsResultMap;
	}
	private boolean directoryCheck(File file)
	{
		if (file.isDirectory()) {
			if (file.listFiles().length>0) {
				return true;
			}else {
				DeleteFile.Delete(file);
				return false;
			}
		}else {
			DeleteFile.Delete(file);
			return false;
		}
		
	}
	private void generateRawdata(String customerLot,ArrayList<File> mapping,HashMap<String, String> customerLotConfig,int times)
	{
		try {
			times++;
			if (times<4) {
				HashMap<String, String> resultMap=new HashMap<>();
				Rawdata rawdata=new InitStdfMappingBean(mapping, customerLotConfig, resultMap);
				TreeMap<Integer, Integer> binSummary=rawdata.getbinSummary();
				GenerateRawdata generateRawdata=new GenerateRawdata(rawdata);
				File  vtestRawdata=generateRawdata.generate();
				checkRawdata(vtestRawdata, resultMap,customerLot, mapping, customerLotConfig,times,binSummary);
			}else {
				for (File file : mapping) {
					File seriousErrorMapping =new File("/SeriousEorrMapping/"+customerLot+"/"+file.getName());
					if (!seriousErrorMapping.getParentFile().exists()) {
						seriousErrorMapping.getParentFile().mkdirs();
					}
					FileUtils.copyFile(file, seriousErrorMapping);
				}
			}			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	private void checkRawdata(File vtestRawdata,HashMap<String, String> resultMap,String customerLot,ArrayList<File> mapping,HashMap<String, String> customerLotConfig,int times,TreeMap<Integer, Integer> binSummary)
	{
		try {
			HashMap<String, String> log=new HashMap<>();
			RawdataCheck rawdataCheck=new RawdataCheck();
			boolean checkFLag=rawdataCheck.check(vtestRawdata, log);
			String customerCode=resultMap.get("customerCode");
			String device=resultMap.get("device");
			String lotNum=resultMap.get("lot");
			String cp=resultMap.get("cp");
			String waferId=resultMap.get("waferId");

			if (checkFLag) {	
				dealFile(vtestRawdata, customerCode, device, lotNum, cp, waferId,binSummary);
			}else {
				Set<String> checkItems=log.keySet();
				boolean errorDegree=false;
				for (String chekItem : checkItems) {
					chekItem=chekItem.trim();
					if (chekItem.contains("value")&&!chekItem.equals("Properties value")) {
						errorDegree=true;
					}				
				}
				if (errorDegree) {
						FileUtils.forceDelete(vtestRawdata);
						generateRawdata(customerLot, mapping, customerLotConfig,times);						
				}else {					
					dealFile(vtestRawdata, customerCode, device, lotNum, cp, waferId,binSummary,log);									
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void dealFile(File vtestRawdata,String customerCode,String device,String lotNum,String cp,String waferId,TreeMap<Integer, Integer> binSummary)
	{	
		try {
			File waferIdLog=new File("/TestReport/CustReport/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+waferId+"-error.log");
			if (waferIdLog.exists()) {
				waferIdLog.delete();
			}			
			boolean specialCustomer=false;
			HashMap<String, ArrayList<String>> shieldMap=GetShieldDeviceFromStdf.get();
			if (shieldMap.containsKey(customerCode)) {
				if (shieldMap.get(customerCode).contains("ALL")) {
					specialCustomer=true;
				}else if(shieldMap.get(customerCode).contains(device)){
					specialCustomer=true;
				}
			}
			if (specialCustomer) {
				FileUtils.forceDelete(vtestRawdata);
				return;
			}		
			File resultDirectory=new File("/TestReport/RawData/"+customerCode+"/"+device+"/"+lotNum+"/"+cp);
			if (!resultDirectory.exists()) {
				resultDirectory.mkdirs();
			}
			File resultRawdataTemp=new File("/TestReport/RawData/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+waferId+".raw");
			if (resultRawdataTemp.exists()) {
				resultRawdataTemp.delete();
			}
			HashMap<String, String> resultMap=new HashMap<>();
			resultMap.put("lot", lotNum);
			resultMap.put("waferId", waferId);
			resultMap.put("cp", cp);			
			WaferIdBinSummaryWrite write=new WaferIdBinSummaryWrite();
			write.write(resultMap, binSummary);
			FileUtils.copyFile(vtestRawdata, resultRawdataTemp);
			FileUtils.forceDelete(vtestRawdata);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void dealFile(File vtestRawdata,String customerCode,String device,String lotNum,String cp,String waferId,TreeMap<Integer, Integer> binSummary,HashMap<String, String> log)
	{
		try {
			dealFile(vtestRawdata, customerCode, device, lotNum, cp, waferId,binSummary);
			File waferIdLog=new File("/TestReport/CustReport/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+waferId+"-error.log");
			if (!waferIdLog.getParentFile().exists()) {
				waferIdLog.getParentFile().mkdirs();
			}
			PrintWriter out=new PrintWriter(waferIdLog);
			Set<String> checkItems=log.keySet();
			for (String chekItem : checkItems) {
				if (!chekItem.contains("Type")) {
					out.print(log.get(chekItem));
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
