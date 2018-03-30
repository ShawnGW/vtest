package vtestbeans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import rawdata.GenerateRawdata;
import rawdata.Rawdata;
import resultCheck.RawdataCheck;

public class test {
	public static void main(String[] args) throws IOException {
		File file=new File("D:/BFSCSV/1.raw");
		File file2=new File("D:/BFSCSV/025.FA75-5476-25");
		Rawdata rawdata=new InitTskBean("FA75-5476", file2);
		GenerateRawdata generateRawdata=new GenerateRawdata(rawdata, file);
		File file3=generateRawdata.generate();
		file3=new File("D:/BFSCSV/PHFXLDARFHDFADH.raw");
		RawdataCheck rawdataCheck=new RawdataCheck();
		HashMap<String, String> log=new HashMap<>();	
		System.out.println(rawdataCheck.check(file3, log));
		Set<String> logset=log.keySet();
		for (String string : logset) {
			System.out.println(string+" : "+log.get(string));
		}
		file3.renameTo(new File("D:/BFSCSV/PHFXLDARFHDFADH.raw"));
		System.out.println(file3);              
	}
}