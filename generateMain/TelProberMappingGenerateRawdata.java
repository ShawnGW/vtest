package generateMain;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import mesinfor.WaferIdBinSummaryWrite;
import mestools.GetRandomChar;
import parseMapping.TelOpusProberLotDaParse;
import parseMapping.TelOpusProberLotDatParse;
import parseMapping.TelOpusProberMappingDaParse;
import rawdata.GenerateRawdata;
import rawdata.Rawdata;
import resultCheck.RawdataCheck;
import tools.DeleteFile;
import tools.GetShieldDeviceFromTel;
import vtestbeans.InitTelNormalDieBean;
import vtestbeans.InitTelSmallDieDieBean;

public class TelProberMappingGenerateRawdata {
	private final File dataSourceP=new File("/prod/prober/TEL/P12/mapup");
	private final File dataSourcePMapdown=new File("/prod/prober/TEL/P12/mapdown");
	boolean smallDie=false;
	public static void main(String[] args) throws IOException {
		TelProberMappingGenerateRawdata tel=new TelProberMappingGenerateRawdata();
		tel.generate();
	}
	public void generate() throws IOException
	{
		if (!dealMappings()) {
			return;
		}
		File[] lots=dataSourceP.listFiles();
		if (lots.length>0) {					
			for (File lot : lots) {
				if (lot.isDirectory()&&lot.listFiles().length>0) {
					String customerLot=lot.getName();	
					lot=prvDealFile(customerLot, lot);
					File[] mappings=lot.listFiles();					
					File WAFCONT=null;
					File FORM=null;
					ArrayList<File> waferIdRmpArray=new ArrayList<>();
					ArrayList<File> waferIdDaArray=new ArrayList<>();
					ArrayList<File> lotDaArray=new ArrayList<>();
					
					ArrayList<File> waferIdDatArray=new ArrayList<>();
					ArrayList<File> lotDatArray=new ArrayList<>();
					initDatalog(mappings, WAFCONT, FORM, waferIdRmpArray, waferIdDaArray, lotDaArray, waferIdDatArray, lotDatArray);
					if (smallDie) {
						for (File lotDa : lotDaArray) {
							String lotDaName=lotDa.getName();
							HashMap<String, String> daResultMap=TelOpusProberLotDaParse.get(lotDa);
							String customerCode=null;
							String device=null;
							String cp=daResultMap.get("cp");
							String lotDaSuffix=lotDaName.substring(lotDaName.indexOf(".")-1, lotDaName.indexOf("."));
							for (File waferIdDa : waferIdDaArray) {
								String waferIdDaName=waferIdDa.getName();
								String waferIdDaSuffix=waferIdDaName.substring(waferIdDaName.indexOf(".")-1, waferIdDaName.indexOf("."));
								String waferid=waferIdDaName.substring(0,waferIdDaName.indexOf(".")-1);
								if (waferIdDaSuffix.equals(lotDaSuffix)) {
									String patternName=waferid+waferIdDaSuffix;
									File rmpFile=null;
									for (File waferIdRmp : waferIdRmpArray) {
										if (waferIdRmp.getName().contains(patternName)) {
											rmpFile=waferIdRmp;
										}
									}
									if (rmpFile==null) {
										continue;
									}						
									try {
										HashMap<String, String> waferIdDaResultMap=TelOpusProberMappingDaParse.GetResult(waferIdDa);
										HashMap<String, String> resultMap=new HashMap<>();
										generateRawdata(customerLot, rmpFile, daResultMap, waferIdDaResultMap,resultMap, 0);								
										if (customerCode==null) {
											customerCode=resultMap.get("customerCode");
										}
										if (device==null) {
											device=resultMap.get("device");
										}
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}
//									File proberMappingBackUpDirectory=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+customerLot+"/"+cp+"/LOT/");
//									if (!proberMappingBackUpDirectory.exists()) {
//										proberMappingBackUpDirectory.mkdir();
//									}
								}

							}
							
						}
					}else
					{
						for (File lotDat : lotDatArray) {
							String lotDatName=lotDat.getName();
							HashMap<String, String> datResultMap=TelOpusProberLotDatParse.Get(lotDat);
							String customerCode=null;
							String device=null;
							String cp=datResultMap.get("cp");
							String lotDatSuffix=lotDatName.substring(lotDatName.indexOf(".")-1, lotDatName.indexOf("."));
							for (File waferIdDat : waferIdDatArray)
							{								
								String waferIdDatName=waferIdDat.getName();
								String waferIdDatSuffix=waferIdDatName.substring(waferIdDatName.indexOf(".")-1, waferIdDatName.indexOf("."));
								if (waferIdDatSuffix.equals(lotDatSuffix)) {
									try {	
										HashMap<String, String> resultMap=new HashMap<>();
										generateRawdata(customerLot, waferIdDat, datResultMap, resultMap, 0);									
										if (customerCode==null) {
											customerCode=resultMap.get("customerCode");
										}
										if (device==null) {
											device=resultMap.get("device");
										}
									} catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}									 
								}
								
							}
							File proberMappingBackUpDirectory=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+customerLot+"/"+cp+"/LOT/");
							if (!proberMappingBackUpDirectory.exists()) {
								proberMappingBackUpDirectory.mkdir();
							}
							String mappingName=lotDat.getName();
							mappingName=mappingName+"_"+(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
							File proberMappingBackFile=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+customerLot+"/"+cp+"/LOT/"+mappingName);
							FileUtils.copyFile(lotDat, proberMappingBackFile);							
							if (WAFCONT!=null) {
								File WAFCONTBackUpDirectory=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+customerLot+"/"+cp+"/WAFCONT/");
								if (!WAFCONTBackUpDirectory.exists()) {
									WAFCONTBackUpDirectory.mkdir();
								}
								String WAFCONTName=WAFCONT.getName();
								WAFCONTName=WAFCONTName+"_"+(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
								File WAFCONTBackFile=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+customerLot+"/"+cp+"/WAFCONT/"+mappingName);
								FileUtils.copyFile(WAFCONT, WAFCONTBackFile);	
							}
						}						
					}
					DeleteFile.Delete(lot);
				}else {
					DeleteFile.Delete(lot);
				}
			}
		}
	}
	private void generateRawdata(String customerLot,File mapping,HashMap<String, String> customerLotConfig,HashMap<String, String> waferIdDaResultMap,HashMap<String, String> resultMap,int times)
	{
		try {
			times++;
			if (times<4) {				
				Rawdata rawdata=new InitTelSmallDieDieBean(mapping, customerLotConfig, waferIdDaResultMap, resultMap);
				TreeMap<Integer, Integer> binSummary=rawdata.getbinSummary();
				GenerateRawdata generateRawdata=new GenerateRawdata(rawdata);
				File  vtestRawdata=generateRawdata.generate();
				checkRawdata(vtestRawdata, resultMap,customerLot, mapping, customerLotConfig,times,binSummary);	
			}else {
				File seriousErrorMapping =new File("/SeriousEorrMapping/"+customerLot+"/"+mapping.getName());
				if (!seriousErrorMapping.getParentFile().exists()) {
					seriousErrorMapping.getParentFile().mkdirs();
				}
				FileUtils.copyFile(mapping, seriousErrorMapping);
			}			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	private void generateRawdata(String customerLot,File mapping,HashMap<String, String> customerLotConfig,HashMap<String, String> resultMap,int times)
	{
		try {
			times++;
			if (times<4) {				
				Rawdata rawdata=new InitTelNormalDieBean(mapping,customerLotConfig,resultMap);
				TreeMap<Integer, Integer> binSummary=rawdata.getbinSummary();
				GenerateRawdata generateRawdata=new GenerateRawdata(rawdata);
				File  vtestRawdata=generateRawdata.generate();
				checkRawdata(vtestRawdata, resultMap,customerLot, mapping, customerLotConfig,times,binSummary);	
			}else {
				File seriousErrorMapping =new File("/SeriousEorrMapping/"+customerLot+"/"+mapping.getName());
				if (!seriousErrorMapping.getParentFile().exists()) {
					seriousErrorMapping.getParentFile().mkdirs();
				}
				FileUtils.copyFile(mapping, seriousErrorMapping);
			}			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	private void checkRawdata(File vtestRawdata,HashMap<String, String> resultMap,String customerLot,File mapping,HashMap<String, String> customerLotConfig,int times,TreeMap<Integer, Integer> binSummary)
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
				dealFile(mapping,vtestRawdata, customerCode, device, lotNum, cp, waferId,binSummary);
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
					dealFile(mapping,vtestRawdata, customerCode, device, lotNum, cp, waferId,binSummary,log);									
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	private void initDatalog(File[] mappings,File WAFCONT,File FORM,ArrayList<File> waferIdRmpArray,ArrayList<File> waferIdDaArray,ArrayList<File> lotDaArray,ArrayList<File> waferIdDatArray,ArrayList<File> lotDatArray)
	{
		for (File mapping : mappings) {
			String mappingName=mapping.getName();
			if (mappingName.equals("WAFCONT.DAT")) {
				WAFCONT=mapping;
				continue;
			}
			if (mappingName.contains("LOT")&&mappingName.endsWith(".DA")) {
				lotDaArray.add(mapping);
				continue;
			}
			if (mappingName.equals("FORM.DA")) {
				FORM=mapping;
				continue;
			}
			if (mappingName.endsWith(".RMP")) {
				waferIdRmpArray.add(mapping);
				continue;
			}
			if (mappingName.endsWith(".DA")) {
				waferIdDaArray.add(mapping);
				continue;
			}
			if (mappingName.endsWith(".DAT")&&mappingName.contains("LOT")) {
				lotDatArray.add(mapping);
				continue;
			}
			if (mappingName.endsWith(".DAT")) {
				waferIdDatArray.add(mapping);
			}					
		}
	}
	private File prvDealFile(String customerLot,File lot)
	{
		smallDie=false;
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
		
		File mapDownDirectory=new File(dataSourcePMapdown.getPath()+"/"+customerLot);
		if (!mapDownDirectory.exists()) {
			mapDownDirectory.mkdirs();
		}
		
		File tempMapupLot=new File("/TempMapUp/"+GetRandomChar.getRandomChar(15));
		if (!tempMapupLot.exists()) {
			tempMapupLot.mkdirs();
		}
		
		File[] mappings=lot.listFiles();
		ArrayList<File> mappingArray=new ArrayList<>();
		ArrayList<String> mappingNamesArray=new ArrayList<>();
		for (File mapping : mappings) {
			String fileName=mapping.getName();
			if (fileName.equals("FORM.DA")||fileName.equals("LOT1.DA")||fileName.equals("LOT2.DA")) {
				smallDie=true;
				continue;
			}else if (fileName.endsWith(".RMP")) {
				mappingArray.add(mapping);
				mappingNamesArray.add(fileName);
				continue;
			}else if (fileName.endsWith(".DA")) {
				mappingArray.add(mapping);
				mappingNamesArray.add(fileName);
				continue;
			}else if (fileName.endsWith(".DAT")&&!fileName.contains("WAFCONT")&&!fileName.contains("LOT")) {
				mappingArray.add(mapping);
				mappingNamesArray.add(fileName);
				continue;
			}
		}
		for (File mapping : mappingArray) {
			String mappingName=mapping.getName();
			String waferId=mappingName.substring(0, mappingName.lastIndexOf(".")-1);
			Integer time=Integer.valueOf(mappingName.substring(mappingName.lastIndexOf(".")-1, mappingName.lastIndexOf(".")));
			String suffix=mappingName.substring(mappingName.lastIndexOf("."));
			
			String retestFileName=waferId+(time+1)+suffix;
			if (!mappingNamesArray.contains(retestFileName)) {
				String backFileName=waferId+"1"+suffix;
				File mapDownFile=new File(mapDownDirectory.getPath()+"/"+backFileName);
				if (mapDownFile.exists()) {
					mapDownFile.delete();
				}
				try {
					FileUtils.copyFile(mapping, mapDownFile);					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
		boolean lot2BackupFlag=false;
		for (File mapping : mappings)
		{
			if (mapping.isFile()) {
				File tempMapUpFile=new File(tempMapupLot.getPath()+"/"+mapping.getName());
				if (!tempMapUpFile.exists()) {
					tempMapUpFile.delete();
				}
				try {
					FileUtils.copyFile(mapping, tempMapUpFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (smallDie) {
					if (mapping.getName().equals("LOT2.DA")) {
						lot2BackupFlag=true;
						File mapDownFile=new File(mapDownDirectory.getPath()+"/LOT1.DA");
						if (mapDownFile.exists()) {
							mapDownFile.delete();
						}
						try {
							FileUtils.copyFile(mapping, mapDownFile);					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if (mapping.getName().equals("LOT1.DA")) {
						if (!lot2BackupFlag) {
							File mapDownFile=new File(mapDownDirectory.getPath()+"/LOT1.DA");
							if (mapDownFile.exists()) {
								mapDownFile.delete();
							}
							try {
								FileUtils.copyFile(mapping, mapDownFile);					
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}else if (mapping.getName().equals("FORM.DA")) {
						File mapDownFile=new File(mapDownDirectory.getPath()+"/FORM.DA");
						if (mapDownFile.exists()) {
							mapDownFile.delete();
						}
						try {
							FileUtils.copyFile(mapping, mapDownFile);					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if (mapping.getName().equals("WAFCONT.DAT")) {
						File mapDownFile=new File(mapDownDirectory.getPath()+"/WAFCONT.DAT");
						if (mapDownFile.exists()) {
							mapDownFile.delete();
						}
						try {
							FileUtils.copyFile(mapping, mapDownFile);					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
					/***
					 * 
					if (mapping.getName().equals("LOT2.DAT"))
					{
						lot2BackupFlag=true;
						File mapDownFile=new File(mapDownDirectory.getPath()+"/LOT1.DAT");
						if (mapDownFile.exists()) {
							mapDownFile.delete();
						}
						try {
							FileUtils.copyFile(mapping, mapDownFile);					
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if (mapping.getName().equals("LOT1.DAT")) {
						if (!lot2BackupFlag) {
							File mapDownFile=new File(mapDownDirectory.getPath()+"/LOT1.DAT");
							if (mapDownFile.exists()) {
								mapDownFile.delete();
							}
							try {
								FileUtils.copyFile(mapping, mapDownFile);					
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					 * ****/				
			}else {
				DeleteFile.Delete(mapping);
			}
		}
		//DeleteFile.Delete(lot);
		return tempMapupLot;
	}
	private  boolean dealMappings()
	{
		if (!dataSourcePMapdown.exists()) {
			dataSourcePMapdown.mkdirs();
		}
		if (!dataSourceP.exists()) {
			return false;
		}
		return true;
	}
	private void dealFile(File mapping,File vtestRawdata,String customerCode,String device,String lotNum,String cp,String waferId,TreeMap<Integer, Integer> binSummary)
	{	
		try {
			File waferIdLog=new File("/TestReport/CustReport/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+waferId+"-error.log");
			if (waferIdLog.exists()) {
				waferIdLog.delete();
			}
			
			File proberMappingBackUpDirectory=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+lotNum+"/"+cp);
			if (!proberMappingBackUpDirectory.exists()) {
				proberMappingBackUpDirectory.mkdir();
			}
			String mappingName=mapping.getName();
			mappingName=mappingName+"_"+(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			File proberMappingBackFile=new File("/prod/mapbackup/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+mappingName);
			FileUtils.copyFile(mapping, proberMappingBackFile);
			
			
			boolean specialCustomer=false;
			HashMap<String, ArrayList<String>> shieldMap=GetShieldDeviceFromTel.get();
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
			resultMap.put("customerCode", customerCode);
			resultMap.put("device", device);
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
	private void dealFile(File mapping,File vtestRawdata,String customerCode,String device,String lotNum,String cp,String waferId,TreeMap<Integer, Integer> binSummary,HashMap<String, String> log)
	{
		try {
			dealFile(mapping,vtestRawdata, customerCode, device, lotNum, cp, waferId,binSummary);
			File waferIdLog=new File("/TestReport/CustReport/"+customerCode+"/"+device+"/"+lotNum+"/"+cp+"/"+waferId+"-error.log");
			if (!waferIdLog.getParentFile().exists()) {
				waferIdLog.getParentFile().mkdirs();
			}
			PrintWriter out=new PrintWriter(waferIdLog);
			Set<String> checkItems=log.keySet();
			for (String chekItem : checkItems) {
				if (!chekItem.contains("Type")) {
					out.print(log.get(chekItem)+"\r\n");
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
