package vtestbeans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
		RawdataCheck rawdataCheck=new RawdataCheck();
		HashMap<String, String> log=new HashMap<>();
		HashMap<String, Boolean> checkedProperties=new HashMap<>();
		checkedProperties.put("Inner Lot", false);
		System.out.println(rawdataCheck.check(file3, checkedProperties, log));;
		file3.renameTo(new File("D:/BFSCSV/PHFXLDARFHDFADH.raw"));
		System.out.println(file3);              
	}
}