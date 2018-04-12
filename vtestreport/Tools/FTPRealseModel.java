package Tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class FTPRealseModel {
	public HashMap<String, String> InitMap(String LOT, String ID, String CPPROC, String TIME, String DEVICE, String WAFER,
			String VERSION) {
				HashMap<String, String> NameMap=new HashMap<>();
				NameMap.put("LOT", LOT);
				NameMap.put("FINID", ID);
				NameMap.put("CPPROC", CPPROC);
				NameMap.put("TIME", TIME);
				NameMap.put("DEVICE", DEVICE);
				NameMap.put("WAFER", WAFER);
				NameMap.put("VERSION", VERSION);
				return NameMap;
		// TODO Auto-generated method stub	
	}
	public HashMap<String, String> InitMap(String LOT, String ID, String CPPROC, String TIME, String DEVICE, String WAFER,String VERSION,String SLOT) {
				HashMap<String, String> NameMap=new HashMap<>();
				NameMap.put("LOT", LOT);
				NameMap.put("FINID", ID);
				NameMap.put("CPPROC", CPPROC);
				NameMap.put("TIME", TIME);
				NameMap.put("DEVICE", DEVICE);
				NameMap.put("WAFER", WAFER);
				NameMap.put("VERSION", VERSION);
				NameMap.put("SL", SLOT);
				return NameMap;
		// TODO Auto-generated method stub	
	}
	public void FTP_Release(String CustomerCode, String Device, String Lot, String CP, File file) {
		// TODO Auto-generated method stub
		File ReleaseDirectory=new File("/home/UploadReport/TestReportRelease/"+CustomerCode+"/"+Device+"/"+Lot+"/"+CP+"/Inkless_Mapping");
		if (!ReleaseDirectory.exists()) {
			ReleaseDirectory.mkdirs();
		}
		File ReleaseFile=new File("/home/UploadReport/TestReportRelease/"+CustomerCode+"/"+Device+"/"+Lot+"/"+CP+"/Inkless_Mapping/"+file.getName());
		if (ReleaseFile.exists()) {
			ReleaseFile.delete();
		}
		try {
			FilesCopy.copyfile(file, ReleaseDirectory.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File MailReleaseDirectory=new File("/home/UploadReport/MailReportRelease/"+CustomerCode+"/"+Device+"/"+Lot+"/"+CP+"/Inkless_Mapping");
		if (!MailReleaseDirectory.exists()) {
			MailReleaseDirectory.mkdirs();
		}
		File MailReleaseFile=new File("/home/UploadReport/MailReportRelease/"+CustomerCode+"/"+Device+"/"+Lot+"/"+CP+"/Inkless_Mapping/"+file.getName());
		if (MailReleaseFile.exists()) {
			MailReleaseFile.delete();
		}
		try {
			FilesCopy.copyfile(file, MailReleaseDirectory.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
