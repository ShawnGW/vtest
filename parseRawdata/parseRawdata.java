package parseRawdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;

import parseRawdataInterface.GetBinSummaryFromRawdata;
import parseRawdataInterface.GetPropertiesFromRawdata;
import parseRawdataInterface.GetTestDiesFromRawdata;
import parseRawdataInterface.getMarkAndSkipDiesFromRawdata;

public class parseRawdata implements GetPropertiesFromRawdata,GetBinSummaryFromRawdata,getMarkAndSkipDiesFromRawdata,GetTestDiesFromRawdata{

	private File rawdataSource;
	private LinkedHashMap<String, String> properties=new LinkedHashMap<>();
	private HashMap<String, String> hardBinTestDieMap=new HashMap<>();
	private HashMap<String, String> softBinTestDieMap=new HashMap<>();
	private String[][] hardBinTestDiesDimensionalArray=new String[800][800];
	private String[][] softBinTestDiesDimensionalArray=new String[800][800];
	private String[][] markAndSkipDiesDimensionalArray=new String[800][800];
	private HashMap<String, String> markAndSkipMap=new HashMap<>();
	private HashMap<Integer, Integer> binSummary=new HashMap<>();
	
	public parseRawdata(File rawdataSource) {
		// TODO Auto-generated constructor stub
		this.rawdataSource=rawdataSource;
		parse();
	}
	private void parse()
	{
		try {
			FileReader reader=new FileReader(rawdataSource);
			BufferedReader bReader=new BufferedReader(reader);
			String context;
			while((context=bReader.readLine())!=null)
			{
				System.out.println(context);
			}
			bReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@Override
	public LinkedHashMap<String, String> getProperties() {
		// TODO Auto-generated method stub
		return properties;
	}

	@Override
	public HashMap<String, String> getHardBinTestDie() {
		// TODO Auto-generated method stub
		return hardBinTestDieMap;
	}

	@Override
	public HashMap<String, String> getSoftBinTestDie() {
		// TODO Auto-generated method stub
		return softBinTestDieMap;
	}

	@Override
	public String[][] getHardBinTestDiesDimensionalArray() {
		// TODO Auto-generated method stub
		return hardBinTestDiesDimensionalArray;
	}

	@Override
	public String[][] getSoftBinTestDiesDimensionalArray() {
		// TODO Auto-generated method stub
		return softBinTestDiesDimensionalArray;
	}

	@Override
	public HashMap<String, String> getMarkAndSkipDies() {
		// TODO Auto-generated method stub
		return markAndSkipMap;
	}

	@Override
	public String[][] getMarkAndSkipDiesDimensionalArray() {
		// TODO Auto-generated method stub
		return markAndSkipDiesDimensionalArray;
	}

	@Override
	public HashMap<Integer, Integer> getBinSummary() {
		// TODO Auto-generated method stub
		return binSummary;
	}

}
