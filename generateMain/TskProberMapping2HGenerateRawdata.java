package generateMain;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import mesinfor.GetMesInformations;
import mestools.GetRandomChar;
import rawdata.GenerateRawdata;
import rawdata.Rawdata;
import resultCheck.RawdataCheck;
import tools.DeleteFile;
import vtestbeans.InitTskBean;

public class TskProberMapping2HGenerateRawdata {
	private final File dataSourceUF=new File("/prod/prober/TSK/UF200/mapup");
	private final File dataSourceUFMapdown=new File("/prod/prober/TSK/UF200/mapdown");
	public static void main(String[] args) throws IOException {
		TskProberMapping2HGenerateRawdata tsk=new TskProberMapping2HGenerateRawdata();
		tsk.generate();            
	}
	public void generate()
	{
		if (!dealMappings()) {
			return;
		}
		File[] lots=dataSourceUF.listFiles();
		if (lots.length>0) {		
			GetMesInformations getMesInformations=new GetMesInformations();				
			for (File lot : lots) {
				if (lot.isDirectory()&&lot.listFiles().length>0) {
					String customerLot=lot.getName();	
					lot=prvDealFile(customerLot, lot);
					HashMap<String, String> customerLotConfig=getMesInformations.getSlotAndSequence(customerLot);
					File[] mappings=lot.listFiles();
					for (File mapping : mappings) {
						try {
							HashMap<String, String> resultMap=new HashMap<>();
							generateRawdata(customerLot, mapping, customerLotConfig, resultMap, 0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					DeleteFile.Delete(lot);
				}else {
					DeleteFile.Delete(lot);
				}
			}
		}
	}
	private File prvDealFile(String customerLot,File lot)
	{
		File tempMapup=new File("/TempMapUp");
		if (tempMapup.exists()) {
			File[] randomLots=tempMapup.listFiles();
			for (File randomLot : randomLots) {
				if (randomLot.isDirectory()) {
					if (randomLot.listFiles().length>0) {
						File[] mappings=randomLot.listFiles();
						for (File mapping : mappings) {
							long now=System.currentTimeMillis();
							long time=mapping.lastModified();
							double diff=(now-time)/1000/60/60;
							if (diff>1) {
								mapping.delete();
							}
						}
					}else {
						randomLot.delete();
					}
				}else {
					randomLot.delete();
				}
			}
		}
		
		File mapDownDirectory=new File(dataSourceUFMapdown.getPath()+"/"+customerLot);
		if (!mapDownDirectory.exists()) {
			mapDownDirectory.mkdirs();
		}
		
		File tempMapupLot=new File("/TempMapUp/"+GetRandomChar.getRandomChar(10));
		if (!tempMapupLot.exists()) {
			tempMapupLot.mkdirs();
		}
		
		File[] mappings=lot.listFiles();
		for (File mapping : mappings)
		{
			if (mapping.isFile()) {
				File mapDownFile=new File(mapDownDirectory.getPath()+"/"+mapping.getName());
				if (mapDownFile.exists()) {
					mapDownFile.delete();
				}
				File tempMapUpFile=new File(tempMapupLot.getPath()+"/"+mapping.getName());
				if (!tempMapUpFile.exists()) {
					tempMapUpFile.delete();
				}
				
				try {
					FileUtils.copyFile(mapping, mapDownFile);
					FileUtils.copyFile(mapping, tempMapUpFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				DeleteFile.Delete(mapping);
			}
		}
		DeleteFile.Delete(lot);
		return tempMapup;
	}
	private  boolean dealMappings()
	{
		if (!dataSourceUFMapdown.exists()) {
			dataSourceUFMapdown.mkdirs();
		}
		if (!dataSourceUF.exists()) {
			return false;
		}
		return true;
	}
	private void generateRawdata(String customerLot,File mapping,HashMap<String, String> customerLotConfig,HashMap<String, String> resultMap,int times)
	{
		try {
			times++;
			if (times<4) {
				resultMap=new HashMap<>();
				Rawdata rawdata=new InitTskBean(customerLot, mapping,customerLotConfig,resultMap);
				GenerateRawdata generateRawdata=new GenerateRawdata(rawdata);
				File  vtestRawdata=generateRawdata.generate();
				checkRawdata(vtestRawdata, resultMap,customerLot, mapping, customerLotConfig,times);
			}else {
				
			}			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	private void checkRawdata(File vtestRawdata,HashMap<String, String> resultMap,String customerLot,File mapping,HashMap<String, String> customerLotConfig,int times)
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
				dealFile(vtestRawdata, customerCode, device, lotNum, cp, waferId);
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
						generateRawdata(customerLot, mapping, customerLotConfig, resultMap,times);						
				}else {					
					dealFile(vtestRawdata, customerCode, device, lotNum, cp, waferId,log);									
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void dealFile(File vtestRawdata,String customerCode,String device,String lotNum,String cp,String waferId)
	{
		
		try {
			File resultDirectory=new File("/TestReport/RawData/"+customerCode+"/"+device+"/"+lotNum+"/"+cp);
			if (!resultDirectory.exists()) {
				resultDirectory.mkdirs();
			}
			File resultRawdataTemp=new File("/TestReport/RawData/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+waferId+".raw");
			if (resultRawdataTemp.exists()) {
				resultRawdataTemp.delete();
			}
			System.out.println(resultRawdataTemp.getPath());
			FileUtils.copyFile(vtestRawdata, resultRawdataTemp);
			FileUtils.forceDelete(vtestRawdata);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void dealFile(File vtestRawdata,String customerCode,String device,String lotNum,String cp,String waferId,HashMap<String, String> log)
	{
		try {
			dealFile(vtestRawdata, customerCode, device, lotNum, cp, waferId);
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