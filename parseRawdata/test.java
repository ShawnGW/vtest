package parseRawdata;

import java.io.File;

public class test {
	public static void main(String[] args) {
		File rawdataSource=new File("/TestReport/RawData/GCK/GC9104-2.6/GXV41044/CP1/GXV41044-01-H0.raw");
		parseRawdata parseRawdata=new parseRawdata(rawdataSource);
	}
}
