package com.vtest.it.FTPTrans;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.net.ftp.FTPClient;

import com.vtest.it.tools.Compress;
import com.vtest.it.tools.Compress_Files;
import com.vtest.it.tools.DeleteFile;
import com.vtest.it.tools.FilesCopy;
import com.vtest.it.tools.GetLotStatus;
import com.vtest.it.tools.ParseXml;

/**
 * this program is used to FTP transfer(Vtest_semic.ltd) 
 * @author shawn.sun
 * @category IT
 * @version 2.0
 * @since 2018.3.12
 */

public class VtestFtp extends Thread{
	private final File dataSource=new File("/home/shawn/Public/FtpConfig");
	private final File ftpTempDirectory=new File("/home/ftpTemp");
	private final File ReportDirectory=new File("/home/UploadReport/TestReportRelease/");
	private final File LogFile=new File("/home/shawn/Log");
	private final String proberPath="/prod/prober/TSK/UF200/mapdown/";
	private final String dataSourcePath="/TesterData/";
	private final String dataSourceDealPath="/home/UploadReport/DatalogRelease/";
	private final File SPECIALLOT=new File("/home/ManMap/SpecialLot");
	public static void main(String[] args) {
		VtestFtp vtestFtp=new VtestFtp();
		vtestFtp.start();
	}
	public void run()
	{
		if (!checkFile()) {
			return;
		}
		ArrayList<String> specialLots=initSpecialLot();
		PrintWriter printWriter=getPrintWriter();
		HashMap<String, ArrayList<File>> xmlMap=getXmls();
		Set<String> customerCodeSet=xmlMap.keySet();
		for (String customerCode : customerCodeSet) {
			try {
				ArrayList<File> xmlArray=xmlMap.get(customerCode);
				int length=xmlArray.size();
				for (int i = 0; i < length; i++) {
					try {
						boolean fileDeleteFlag=false;
						if (i==length-1) {
							fileDeleteFlag=true;
						}
						File xml=xmlArray.get(i);
						printWriter.print("*************************************************************\r\n");
						printWriter.print("*"+String.format("%28s", customerCode)+String.format("%31s", " ")+"*\r\n");
						printWriter.print("*************************************************************\r\n");
						
						File[] Report_Devices=new File(ReportDirectory.getPath()+"/"+customerCode).listFiles();
						HashMap<String, Object> result=null;
						ParseXml parseXml=new ParseXml();
						result=	parseXml.Parse(xml);
						String flagCheck=(String) result.get("CheckTesterData");
						String zipType=(String)result.get("ZipType");
						@SuppressWarnings("unchecked")
						ArrayList<String> CPProcess=(ArrayList<String>) result.get("CPProcess");
						@SuppressWarnings("unchecked")
						ArrayList<String> fileInfor=(ArrayList<String>) result.get("FileInfor");
						HashMap<String, ArrayList<Object>> FtpFilesMapper=getFtpFileMapper(fileInfor);						
						Set<String> NameSet=FtpFilesMapper.keySet();
												
						@SuppressWarnings("unchecked")
						HashMap<String, String> FtpInfor=(HashMap<String, String>) result.get("FtpInfor");
						@SuppressWarnings("unchecked")
						HashMap<String, HashMap<String, String>> modifyMap=(HashMap<String, HashMap<String, String>>) result.get("ModifyMapper");
						modifyMap=initModify(modifyMap);
						
						String host=FtpInfor.get("IP");
						String port=FtpInfor.get("port");
						String username=FtpInfor.get("username");
						String password=FtpInfor.get("password");
						String moudle=FtpInfor.get("moudle");
						
						for (File Device : Report_Devices) 
						{
							String deviceName=Device.getName();					
							if (Device.isDirectory()&&Device.listFiles().length>0) 
							{
								File[] Lots=Device.listFiles();
								for (File Lot : Lots)
								{
									String lotName=Lot.getName();
									boolean releaseFlag=false;
									try {
										releaseFlag=GetLotStatus.GetStatusFinal(lotName);
									} catch (Exception e) {
										// TODO: handle exception
									}
									String releaseFlag2=customerCode+":"+deviceName+":"+lotName;
									if (releaseFlag||specialLots.contains(releaseFlag2))
									{
										System.out.println(deviceName+":"+lotName+":release");
										if (Lot.isDirectory()&&Lot.listFiles().length>0)
										{
											FTPClient client=new FTPClient();
											initFtp(client, host, port, username, password, moudle,0);
											initTempFtpDirectory(printWriter);
											LinkedHashMap<String, File> uploadMap=new LinkedHashMap<>();
											File[] CPS=Lot.listFiles();
											ArrayList<File> Final_CPProcess=getCps(CPProcess, CPS,printWriter);
											for (File CPFile : Final_CPProcess)
											{
												File CP=CPFile;
												String CPName=CPFile.getName();
												if (CP.isDirectory()&&CP.listFiles().length>0)
												{													
													System.out.println(deviceName+":"+lotName+":"+CPName+"  uploading...");
													printWriter.print(deviceName+":"+lotName+":"+CPName+"   "+new Date()+"\r\n");
													try {
														HashMap<String, String> TypeWrapper=initTyper(customerCode, deviceName, lotName, CPName);
														File cusReport=null;
														HashMap<String, File[]> FileMapper=initData(cusReport, printWriter, CP, customerCode, deviceName, lotName, CPName, flagCheck);
														for (String FileName : NameSet)
														{
															try {																
																ArrayList<Object> config=FtpFilesMapper.get(FileName);						
																boolean zipFlag      =(boolean)config.get(0);																															
																String RPFlag     =(String) config.get(3);
																String[] contents =((String)config.get(1)).split("&");
																String classifyType=(String) config.get(4);
																String ftpFileName=FileName.substring(0, FileName.lastIndexOf("-"));																
																HashMap<String, ArrayList<File>> finalTransferDatalogs=uploadFileDeal(contents,RPFlag,FileMapper,cusReport);
																Set<String> RPProcess=finalTransferDatalogs.keySet();																																
																for (String process : RPProcess) {
																	String ftpPath    =(String) config.get(2);
																	ArrayList<File> processDatalogs=finalTransferDatalogs.get(process);
																	ArrayList<File> uploadFiles=getUploadFiles(classifyType,zipFlag,lotName,zipType,processDatalogs);
																	TypeWrapper.put("{RPn}", process);
																	Set<String> typeSet=TypeWrapper.keySet();
																	for (String typeMap : typeSet) {
																		ftpFileName=ftpFileName.replace(typeMap, TypeWrapper.get(typeMap));
																		String finalChar=TypeWrapper.get(typeMap);
																		if (modifyMap.containsKey(typeMap)) {
																			HashMap<String, String> nameMap=modifyMap.get(typeMap);
																			if (nameMap.containsKey(finalChar)) {
																				finalChar=nameMap.get(finalChar);
																			}
																		}										
																		ftpPath=ftpPath.replace(typeMap, finalChar);																
																	}		
																	
																	initFtpPath(ftpPath, client,uploadFiles);																	
																	client.changeWorkingDirectory("/"+ftpPath);
																	
																	for (File localfile : uploadFiles) {
																		if (localfile.isDirectory()) {
																			String DirectoryName=localfile.getName();
																			if (!client.changeWorkingDirectory("/"+ftpPath+"/"+DirectoryName)) {
																				client.makeDirectory("/"+ftpPath+"/"+DirectoryName);
																			}
																			client.changeWorkingDirectory("/"+ftpPath+"/"+DirectoryName);
																			File[] files=localfile.listFiles();
																			for (int k = 0; k < files.length; k++) {
																				//FileInputStream fins=new FileInputStream(files[k]);
																				uploadMap.put("/"+ftpPath+"/"+DirectoryName+"&"+GetRandomChar.getRandomChar(10), files[k]);
																				//client.storeFile(files[k].getName(), fins);																																									
																				//fins.close();
																			}							
																		}else {
																			uploadMap.put("/"+ftpPath+"&"+GetRandomChar.getRandomChar(10), localfile);
																			//FileInputStream fins=new FileInputStream(localfile);
																			//client.storeFile(localfile.getName(), fins);
																			if (classifyType.equals("CLASSIFY_LOT")) {
																				client.rename(localfile.getName(), ftpFileName+"."+zipType);
																				printWriter.print("\t"+ftpFileName+"."+zipType+"  size:"+localfile.length()/1024+"KB "+Double.valueOf((localfile.length()/1024))/1024+"MB "+"\r\n");
																			}else {
																				printWriter.print("\t"+localfile.getName()+"  size:"+localfile.length()/1024+"KB "+Double.valueOf((localfile.length()/1024))/1024+"MB "+"\r\n");
																			}
																			//fins.close();
																		}
																	}																																			
																}
																
															} catch (Exception e) {
																// TODO: handle exception
																e.printStackTrace();
																e.printStackTrace(printWriter);	
															}
														}														
													} catch (Exception e) {
														// TODO: handle exception
														e.printStackTrace();
														e.printStackTrace(printWriter);
													}											
												}
											}
											Set<String> pathSet=uploadMap.keySet();
											for (String path : pathSet) {
												String actualPath=path.split("&")[0];
												File file=uploadMap.get(path);
												String remoteName=file.getName();
												client.changeWorkingDirectory(actualPath);
												FileInputStream fins=new FileInputStream(file);
												System.out.println(remoteName);
												try {
													client.enterLocalPassiveMode();
													client.storeFile(remoteName, fins);
												} catch (Exception e) {
													// TODO: handle exception
													e.printStackTrace();
													initFtp(client, host, port, username, password, moudle, 0);
													client.changeWorkingDirectory(actualPath);
													client.storeFile(remoteName, fins);
												}
												System.out.println(remoteName+" upload finish!");
												fins.close();
											}
											initTempFtpDirectory(printWriter);
											if (fileDeleteFlag) {
												try {
													DeleteFile.Delete(Lot);
												} catch (Exception e) {
													// TODO: handle exception
													e.printStackTrace();
													e.printStackTrace(printWriter);
												}
											}
											client.disconnect();
										}
									}									
								}
							}else {
								DeleteFile.Delete(Device);
							}
						}
						
					} catch (Exception e) {
						// TODO: handle excepti;on
						e.printStackTrace();
						e.printStackTrace(printWriter);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				e.printStackTrace(printWriter);
			}
		}
		printWriter.flush();
		printWriter.close();
	}
	private ArrayList<String> initSpecialLot()
	{
		ArrayList<String> arrayList=new ArrayList<>();
		File[] files=SPECIALLOT.listFiles();
		try {
			for (int i = 0; i < files.length; i++) {
				FileReader reader=new FileReader(files[i]);
				BufferedReader bufferedReader=new BufferedReader(reader);
				String content;
				while((content=bufferedReader.readLine())!=null)
				{
					arrayList.add(content);
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return arrayList;
	}
	private void initFtp(FTPClient client,String host,String port,String username,String password,String moudle,int times)
	{
			try {
				Thread.sleep(3000);
				client.connect(host, Integer.valueOf(port));
				client.login(username, password);	
//				client.enterLocalActiveMode();
				client.setDefaultTimeout(100000);
//				if (moudle.equals("Negative")) {
//					client.enterLocalPassiveMode();
//				}
				client.setConnectTimeout(100000);
				client.setBufferSize(1024*1024);
				client.setControlEncoding("GBK");
				client.setFileType(FTPClient.BINARY_FILE_TYPE);
				client.enterLocalPassiveMode();
			} catch (Exception e) {
				// TODO: handle exception	
				e.printStackTrace();
				times++;
				if (times<10) {
					initFtp(client, host, port, username, password, moudle,times);
				}
			}
	}
	private boolean checkFile()
	{
		if (!dataSource.exists()) {
			return false;
		}
		if (!ReportDirectory.exists()) {
			return false;
		}
		if (!LogFile.exists()) {
			LogFile.mkdirs();
		}
		if (!ftpTempDirectory.exists()) {
			ftpTempDirectory.mkdirs();
		}
		return true;
	}
	private HashMap<String, ArrayList<File>> uploadFileDeal(String[] contents,String RPFlag,HashMap<String, File[]> FileMapper,File cusReport)
	{
		ArrayList<File> datalogs=new ArrayList<>();
		HashMap<String, ArrayList<File>> finalTransferDatalogs=new HashMap<>();
		for (String FileType : contents)
		{
				File[] files=FileMapper.get(FileType);
				for (int j = 0; j < files.length; j++) {
					datalogs.add(files[j]);
				}
		}
			
		if (RPFlag.toUpperCase().equals("TRUE")) {
			for (File file : datalogs) {
				String fileName=file.getName();
				if ((fileName.contains("RP0")&&fileName.startsWith("VTEST"))||(!fileName.contains("RT")&&!fileName.startsWith("VTEST"))) {
					if (finalTransferDatalogs.containsKey("CP")) {
						ArrayList<File> CPArrayList=finalTransferDatalogs.get("CP");
						CPArrayList.add(file);
						finalTransferDatalogs.put("CP",CPArrayList);
					}else {
						ArrayList<File> CPArrayList=new ArrayList<>();
						CPArrayList.add(file);
						finalTransferDatalogs.put("CP",CPArrayList);
					}
				}else
				{
					if (finalTransferDatalogs.containsKey("RT")) {
						ArrayList<File> RPArrayList=finalTransferDatalogs.get("RT");
						RPArrayList.add(file);
						finalTransferDatalogs.put("RT",RPArrayList);
					}else {
						ArrayList<File> RPArrayList=new ArrayList<>();
						RPArrayList.add(file);
						finalTransferDatalogs.put("RT",RPArrayList);
					}
				}
			}
		}else
		{
			ArrayList<File> CPArrayList=new ArrayList<>();
			for (File file : datalogs) {																		
				CPArrayList.add(file);																																			
			}
			finalTransferDatalogs.put("CP",CPArrayList);
		}
		return finalTransferDatalogs;
	}
	private HashMap<String, HashMap<String, String>> initModify(HashMap<String, HashMap<String, String>> modifyMap)
	{
		HashMap<String, String> mapper=new HashMap<>();
		mapper.put("Device", "{Cus_Device}");
		mapper.put("Lot", "{Lot_ID}");
		mapper.put("CustomerCode", "{Cus_Code}");
		
		Set<String> keyWordSet=modifyMap.keySet();
		for (String keyWord : keyWordSet) {
			if (mapper.containsKey(keyWord)) {
				modifyMap.put(mapper.get(keyWord), modifyMap.get(keyWord));
				modifyMap.remove(keyWord);
			}
		}
		return modifyMap;
	}
	private void initFtpPath(String ftpPath,FTPClient client,ArrayList<File> uploadFiles) throws IOException
	{
		String[] Patharray;
		if (ftpPath.substring(0, 1).equals("/")) {
			Patharray=ftpPath.substring(1).split("/");
		}else {
			Patharray=ftpPath.split("/");
		}
		client.changeWorkingDirectory("/");
		for (int k = 0; k < Patharray.length; k++) {
			StringBuilder TempPathBuilder=new StringBuilder("/");															
			for (int j = 0; j < k+1; j++) {
				TempPathBuilder.append(Patharray[j]+"/");
			}															
			if (!client.changeWorkingDirectory(TempPathBuilder.toString())) {	
				client.makeDirectory(TempPathBuilder.toString());									
			}
		}
//		try {
//			String[] ServerFiles=client.listNames("/"+ftpPath);
//			if (ServerFiles.length>0) {
//				for (String ServerFileName : ServerFiles) {
//					for (File file : uploadFiles) {
//						if (file.isFile()&&file.getName().equals(ServerFileName)) {
//							client.deleteFile("/"+ftpPath+"/"+ServerFileName);
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
	}
	private void initTempFtpDirectory(PrintWriter printWriter)
	{
		try {
			File[] LocalFiles=ftpTempDirectory.listFiles();
			if (LocalFiles.length>0) {
				for (int j = 0; j < LocalFiles.length; j++) {
					DeleteFile.Delete(LocalFiles[j]);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(printWriter);
		}
	}
	private HashMap<String,File[]> initData(File CusReport,PrintWriter printWriter,File CP,String customerCode,String  deviceName ,String lotName,String CPName,String flagCheck)
	{
		File[] inklessMaping=null;
		File[] Fab_Rawdata=null;
		File[] Fab_Mapping=null;
		File[] ProberMapping=null;
		
		File[] M7000_Datalog=null;
		File[] M7000_Summary=null;
		File[] STDF_Datalog=null;
		File[] Chroma_Datalog=null;
		File[] Chroma_Summary=null;
		File[] Chroma_wmp=null;
		File[] T862_Datalog=null;
		File[] T862_Summary=null;
		
		File[] Reports=CP.listFiles();
		for (File Report : Reports) {
			if (Report.getName().endsWith(".xlsx")&&Report.isFile()) {
				if (Report.getName().contains(lotName)&&Report.getName().length()==lotName.length()+5) {
					CusReport=Report;
				}														
			}
		}
		if (CusReport==null) {
			for (File Report : Reports) {
				if (Report.getName().endsWith(".xlsx")&&Report.isFile()) {
					CusReport=Report;														
				}
			}
		}
		File[] customerReport={CusReport};
		
		if (new File(CP.getPath()+"/Inkless_Mapping").exists())
		{
		inklessMaping=new File(CP.getPath()+"/Inkless_Mapping").listFiles();
		}								
		if (new File(CP.getPath()+"/Fab_Mapping").exists())
		{
			Fab_Mapping=new File(CP.getPath()+"/Fab_Mapping").listFiles();
		}												
		if (new File(CP.getPath()+"/Fab_Rawdata").exists())
		{
			Fab_Rawdata=new File(CP.getPath()+"/Fab_Rawdata").listFiles();
		}
		
		ProberMapping=new File(proberPath+lotName).listFiles();
		
		File[] M700Datas=new File("/TesterData/"+customerCode+"/"+deviceName+"/"+lotName+"/"+CPName).listFiles();												
		ArrayList<File> M700_Datalog_temp_Array=new ArrayList<>();
		ArrayList<File> M700_Summary_temp_Array=new ArrayList<>();
		ArrayList<File> T862_Datalog_temp_Array=new ArrayList<>();
		ArrayList<File> T862_Summary_temp_Array=new ArrayList<>();
		if (flagCheck.equals("false")) {
			try {
				for (File Data : M700Datas) {
					if (Data.isFile()&&Data.getName().contains("TTT")) {
						T862_Datalog_temp_Array.add(Data);
					}else if(Data.isFile()&&Data.getName().endsWith(".lsr")){
						T862_Summary_temp_Array.add(Data);
					}else if (Data.isFile()&&Data.getName().contains("Summary")) {
							M700_Summary_temp_Array.add(Data);
					}else if (Data.isFile()&&Data.getName().contains("datalog")) {
							M700_Datalog_temp_Array.add(Data);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace(printWriter);
				printWriter.print("Datalog»± ß\r\n");
			}
		}else {
			for (File Data : M700Datas) {
				if (Data.isFile()&&Data.getName().contains("TTT")) {
					T862_Datalog_temp_Array.add(Data);
				}else if(Data.isFile()&&Data.getName().endsWith(".lsr")){
					T862_Summary_temp_Array.add(Data);
				}else if (Data.isFile()&&Data.getName().contains("Summary")) {
						M700_Summary_temp_Array.add(Data);
				}else if (Data.isFile()&&Data.getName().contains("datalog")) {
						M700_Datalog_temp_Array.add(Data);
				}
			}
		}
		
		M7000_Datalog=M700_Datalog_temp_Array.toArray(new File[M700_Datalog_temp_Array.size()]);
		M7000_Summary=M700_Summary_temp_Array.toArray(new File[M700_Summary_temp_Array.size()]);
		T862_Datalog=T862_Datalog_temp_Array.toArray(new File[T862_Datalog_temp_Array.size()]);
		T862_Summary=T862_Summary_temp_Array.toArray(new File[T862_Summary_temp_Array.size()]);
		
		File DataSource=new File(dataSourcePath+customerCode+"/"+deviceName+"/"+lotName+"/"+CPName);
		File DataSource2=new File(dataSourceDealPath+customerCode+"/"+deviceName+"/"+lotName+"/"+CPName);
		File[] STDFWithChromas=null;
		if (DataSource2.exists()) {
			 STDFWithChromas=DataSource2.listFiles();
		}else {
			 STDFWithChromas=DataSource.listFiles();
		}			
		ArrayList<File> STDF_Datalog_temp_Array=new ArrayList<>();
		ArrayList<File> Chroma_Datalog_temp_Array=new ArrayList<>();
		ArrayList<File> Chroma_Summary_temp_Array=new ArrayList<>();
		ArrayList<File> Chroma_Wmp_temp_Array=new ArrayList<>();											
			try {
				for (File WaferID : STDFWithChromas) {
					if (WaferID.isDirectory()&&WaferID.listFiles().length>0) {
						File[] Datas=WaferID.listFiles();
						for (File data : Datas) {
							if (data.isFile()&&data.getName().contains("dlogTDO")) {
								Chroma_Datalog_temp_Array.add(data);
							}
							if (data.isFile()&&data.getName().contains("sumryTDO")) {
								Chroma_Summary_temp_Array.add(data);
							}
							if (data.isFile()&&data.getName().contains("wmpTDO")) {
								Chroma_Wmp_temp_Array.add(data);
							}
							if (data.isFile()&&(data.getName().endsWith(".stdf")||data.getName().endsWith(".std"))) {
								STDF_Datalog_temp_Array.add(data);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace(printWriter);
				printWriter.print("Chroma Datalog»± ß\r\n");
			}
			
		if (DataSource2.exists()) {
			HashMap<ArrayList<File>, String> dataMap=new HashMap<>();
			dataMap.put(STDF_Datalog_temp_Array, ".stdf");
			dataMap.put(Chroma_Datalog_temp_Array, "dlogTDO");
			dataMap.put(Chroma_Summary_temp_Array, "sumryTDO");
			dataMap.put(Chroma_Wmp_temp_Array, "wmpTDO");
			Set<ArrayList<File>> keyset=dataMap.keySet();
			for (ArrayList<File> arrayList : keyset) {
				if (arrayList.isEmpty()) {															
					GetDatalog.get(DataSource.listFiles(), arrayList, dataMap.get(arrayList));																												
				}
			 }
		 }
			
		STDF_Datalog=STDF_Datalog_temp_Array.toArray(new File[STDF_Datalog_temp_Array.size()]);
		Chroma_Datalog=Chroma_Datalog_temp_Array.toArray(new File[Chroma_Datalog_temp_Array.size()]);
		Chroma_Summary=Chroma_Summary_temp_Array.toArray(new File[Chroma_Summary_temp_Array.size()]);
		Chroma_wmp=Chroma_Wmp_temp_Array.toArray(new File[Chroma_Wmp_temp_Array.size()]);
				
		HashMap<String, File[]> FileMapper=new HashMap<>();
		FileMapper.put("Inkless_Mapping", inklessMaping);
		FileMapper.put("Prober_Mapping", ProberMapping);
		FileMapper.put("FAB_Mapping", Fab_Mapping);
		FileMapper.put("FAB_Rawdata", Fab_Rawdata);
		FileMapper.put("M7000_Datalog", M7000_Datalog);
		FileMapper.put("M7000_Summary", M7000_Summary);
		FileMapper.put("T862_Datalog", T862_Datalog);
		FileMapper.put("T862_Summary", T862_Summary);
		FileMapper.put("STDF_Datalog", STDF_Datalog);
		FileMapper.put("Chroma_Datalog", Chroma_Datalog);
		FileMapper.put("Chroma_Summary", Chroma_Summary);
		FileMapper.put("Chroma_wmp", Chroma_wmp);
		FileMapper.put("Cus_Report", customerReport);
		return FileMapper;
	}
	private HashMap<String, String> initTyper(String customerCode,String deviceName,String lotName,String CPName )
	{
		SimpleDateFormat yearFormat=new SimpleDateFormat("yyyy");
		SimpleDateFormat mounthFormat=new SimpleDateFormat("MM");
		SimpleDateFormat dayFormat=new SimpleDateFormat("dd");
		SimpleDateFormat hourFormat=new SimpleDateFormat("HH");
		SimpleDateFormat minuteFormat=new SimpleDateFormat("mm");
		SimpleDateFormat secondFormat=new SimpleDateFormat("ss");
		
		HashMap<String, String> TypeWrapper=new HashMap<>();												
		TypeWrapper.put("{Cus_Code}", customerCode);
		TypeWrapper.put("{Cus_Device}", deviceName);
		TypeWrapper.put("{Lot_ID}", lotName);
		TypeWrapper.put("{Wafer_ID}", "Wafer_ID");
		TypeWrapper.put("{Wafer_Number}", "ID_Number");
		TypeWrapper.put("{CPn}", CPName);
		TypeWrapper.put("{RPn}", "RP_Process");
		TypeWrapper.put("{yyyy}", yearFormat.format(new Date()));
		TypeWrapper.put("{MM}", mounthFormat.format(new Date()));
		TypeWrapper.put("{dd}", dayFormat.format(new Date()));
		TypeWrapper.put("{HH}", hourFormat.format(new Date()));
		TypeWrapper.put("{mm}", minuteFormat.format(new Date()));
		TypeWrapper.put("{ss}", secondFormat.format(new Date()));
		return TypeWrapper;
	}
	private ArrayList<File> getUploadFiles(String classifyType,boolean zipFlag,String lotName,String zipType,ArrayList<File> processDatalogs) throws IOException
	{
		ArrayList<File> uploadFiles=new ArrayList<>();
		classifyType=classifyType.toUpperCase().trim();
		if (zipFlag) {																		 
			if (classifyType.equals("CLASSIFY_SINGLE")) {
				for (File file : processDatalogs) {
					File zipFile=new File(ftpTempDirectory.getPath()+"/"+file.getName()+"."+zipType);
					Compress.zip(file, zipFile);
					uploadFiles.add(zipFile);
				}
			}else if(classifyType.equals("CLASSIFY_WAFER")){
				HashMap<String, ArrayList<File>> waferidMap=new HashMap<>();																			
				for (File file : processDatalogs)
				{
					String waferid=getWaferid(file);
					if(waferidMap.containsKey(waferid))
					{
						ArrayList<File> waferidArray=waferidMap.get(waferid);
						waferidArray.add(file);
						waferidMap.put(waferid, waferidArray);
					}else {
						ArrayList<File> waferidArray=new ArrayList<>();
						waferidArray.add(file);
						waferidMap.put(waferid, waferidArray);
					}
				}
				Set<String> waferidSet=waferidMap.keySet();
				for (String waferid : waferidSet) {
					File zipFile=new File(ftpTempDirectory.getPath()+"/"+waferid+"."+zipType);
					Compress_Files.zip2(zipFile, waferidMap.get(waferid));
					uploadFiles.add(zipFile);
				}
			}else {
				File zipFile=new File(ftpTempDirectory.getPath()+"/"+lotName+"."+zipType);
				Compress_Files.zip2(zipFile, processDatalogs);
				uploadFiles.add(zipFile);
			}
		}else
		{
			if(classifyType.equals("CLASSIFY_WAFER")){
				ArrayList<String> pathArray=new ArrayList<>();
				for (File file : processDatalogs)
				{
					String waferid=getWaferid(file);
					File waferiDirectory=new File(ftpTempDirectory.getPath()+"/"+waferid);
					if (!waferiDirectory.exists()) {
						waferiDirectory.mkdirs();
					}
					FilesCopy.copyfile(file, waferiDirectory.getPath());
					if (!pathArray.contains(waferiDirectory.getPath())) {
						pathArray.add(waferiDirectory.getPath());
					}
				}
				for (String path : pathArray) {
					uploadFiles.add(new File(path));
				}
			}else {
				for (File file : processDatalogs)
				{
					uploadFiles.add(file);
				}
			}
		}
		return uploadFiles;
	}
	private ArrayList<File> getCps(ArrayList<String> CPProcess,File[] CPS,PrintWriter printWriter)
	{
		ArrayList<File> Final_CPProcess=new ArrayList<>();
		if (CPProcess.size()==0) {
			File CP=CPS[0];									
			for (File CP_File : CPS) {										
				try {
					if (Integer.valueOf(CP_File.getName().substring(2, 3))>Integer.valueOf(CP.getName().substring(2, 3))) {
						CP=CP_File;
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace(printWriter);
					printWriter.print("CP¡˜≥Ã¥ÌŒÛ£¨”–“Ï≥£CPProcess\r\n");
				}																					
			}							
			Final_CPProcess.add(CP);
		}else
		{
			for (File CP_File : CPS)
			{
				if (CPProcess.contains(CP_File.getName())) {
					Final_CPProcess.add(CP_File);
				}
			}
		}
		return Final_CPProcess;
	}
	private HashMap<String, ArrayList<Object>> getFtpFileMapper(ArrayList<String> fileInfor)
	{
		HashMap<String, ArrayList<Object>> FtpFilesMapper=new HashMap<>();
		int Sum=0;
		for (String infor : fileInfor) {
			ArrayList<Object> FileProperties=new ArrayList<>();
			try {
				String[] infors=infor.split(":");
				boolean zipFlag;
				if (infors[0].toUpperCase().equals("YES")) {
					zipFlag=true;
				}else {
					zipFlag=false;
				}
				String FileName=infors[1];
				String FileType=infors[2];
				String address=infors[3];
				String RPProcess=infors[4];
				String classifyType=infors[5];
				FileProperties.add(zipFlag);
				FileProperties.add(FileType);
				FileProperties.add(address);	
				FileProperties.add(RPProcess);
				FileProperties.add(classifyType);
				FtpFilesMapper.put(FileName+"-"+Sum, FileProperties);						
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				continue;
			}
			Sum++;
		}
		return FtpFilesMapper;
	}
	private  PrintWriter getPrintWriter()
	{
		File logfile=new File(LogFile.getPath()+"/FTPLog-"+(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))+".log");
		FileWriter writer;
		PrintWriter printWriter=null;
		try {
			writer = new FileWriter(logfile);
		    printWriter=new PrintWriter(writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return printWriter;
	}
	private String getWaferid(File file)
	{
		String[] NameTokens=file.getName().split("_");																	
		Integer TesterID=6;
		for (int j = 0; j < NameTokens.length; j++) {
			if (NameTokens[j].contains("TTK")||NameTokens[j].contains("TTJ")||NameTokens[j].contains("TTH")||NameTokens[j].contains("TTM")||NameTokens[j].contains("TTT")||NameTokens[j].contains("TTV")) {
					TesterID=j;
			}
		}
		String WaferID=NameTokens[TesterID-1];
		return WaferID;
	}
	private  HashMap<String, ArrayList<File>> getXmls()
	{
		HashMap<String, ArrayList<File>> xmlMap=new HashMap<>();
		File[] XmlList=dataSource.listFiles();
		try {
			for (int i = 0; i < XmlList.length; i++) {
				String customerCode=XmlList[i].getName().substring(0, 3);
				if (xmlMap.containsKey(customerCode)) {
					ArrayList<File> singleArray=xmlMap.get(customerCode);
					singleArray.add(XmlList[i]);
					xmlMap.put(customerCode, singleArray);
				}else {
					ArrayList<File> singleArray=new ArrayList<>();
					singleArray.add(XmlList[i]);
					xmlMap.put(customerCode, singleArray);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return xmlMap;
	}
}
