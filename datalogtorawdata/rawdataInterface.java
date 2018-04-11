package datalogtorawdata;

import java.util.HashMap;

public interface rawdataInterface {
	abstract String getCustomerCode();
	abstract String getDevice();
	abstract String getLot();
	abstract String getWaferId();
	abstract String getTester();
	abstract String getProber();
	abstract String getProberCard();
	abstract String getCp();
	abstract String getOp();
	abstract String getTestStartTime();
	abstract HashMap<String, String> getDiesMap();
	
}
