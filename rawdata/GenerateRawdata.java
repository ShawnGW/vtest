package rawdata;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

import mestools.GetRandomChar;

/**
 * @author shawn.sun
 * @category IT
 * @version 1.0
 * @since 2018.3.15
 */
public class GenerateRawdata implements GenerateRawdataInf{

	private Rawdata rawdata;
	private PrintWriter printWriter;
	private File completeFile;
	private final File tempRawdataDirectory=new File("/TempRawdata");
    public GenerateRawdata(Rawdata rawdata) throws IOException {
		// TODO Auto-generated constructor stub
    	if (!tempRawdataDirectory.exists()) {
			tempRawdataDirectory.mkdirs();
		}
    	this.rawdata=rawdata;
    	this.completeFile =new File("/TempRawdata/"+GetRandomChar.getRandomChar(15)+".raw");
    	this.printWriter =new PrintWriter(completeFile);
	}
    public File generate()
    {
    	writeProperties(rawdata.getProperties());
    	writeTestDie(rawdata.getTestDieMap());
    	writeMarkAndSkipDie(rawdata.getMarkAndSkipDieMap());
    	writeBinSummary(rawdata.getbinSummary());
    	printWriter.flush();
    	printWriter.close();
    	return completeFile;
    }
	@Override
	public void writeProperties(LinkedHashMap<String, String> properties) {
		// TODO Auto-generated method stub
		Set<String> propertieSet=properties.keySet();
		for (String proper : propertieSet) {
			printWriter.print(proper.startsWith("@")?proper.split("@")[1]+":":proper+":");
			printWriter.print(properties.get(proper)+"\r\n");
		}
	}
	@Override
	public void writeTestDie(HashMap<String, String> testDieMap) {
		// TODO Auto-generated method stub
		printWriter.print("RawData\r\n");
		Set<String> propertieSet=testDieMap.keySet();
		for (String coordinate : propertieSet) {
			printWriter.print(coordinate+testDieMap.get(coordinate));
			printWriter.print("\r\n");
		}
		printWriter.print("DataEnd\r\n");
	}

	@Override
	public void writeMarkAndSkipDie(HashMap<String, String> markAndSkipDieMap) {
		// TODO Auto-generated method stub
		printWriter.print("SkipAndMarkStart\r\n");
		Set<String> propertieSet=markAndSkipDieMap.keySet();
		for (String coordinate : propertieSet) {
			printWriter.print(coordinate+markAndSkipDieMap.get(coordinate));
			printWriter.print("\r\n");
		}
		printWriter.print("SkipAndMarkEnd\r\n");
	}
	@Override
	public void writeBinSummary(TreeMap<Integer, Integer> binSmmary) {
		// TODO Auto-generated method stub
		printWriter.print("Bin Summary\r\n");
		Set<Integer> binNumbers=binSmmary.keySet();
		for (Integer bin : binNumbers) {
			printWriter.print("Bin"+bin+":");
			printWriter.print(binSmmary.get(bin)+"\r\n");
		}
		
	}
	
}
