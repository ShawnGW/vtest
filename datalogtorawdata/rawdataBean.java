package datalogtorawdata;

import java.util.HashMap;

public class rawdataBean{
	private String customerCode;
	private String device;
	private String lot;
	private String waferId;
	private String tester;
	private String prober;
	private String proberCard;
	private String cp;
	private String op;
	private String testStartTime;
	private HashMap<String, String> diesMap;
	
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getLot() {
		return lot;
	}
	public void setLot(String lot) {
		this.lot = lot;
	}
	public String getWaferId() {
		return waferId;
	}
	public void setWaferId(String waferId) {
		this.waferId = waferId;
	}
	public String getTester() {
		return tester;
	}
	public void setTester(String tester) {
		this.tester = tester;
	}
	public String getProber() {
		return prober;
	}
	public void setProber(String prober) {
		this.prober = prober;
	}
	public String getProberCard() {
		return proberCard;
	}
	public void setProberCard(String proberCard) {
		this.proberCard = proberCard;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getTestStartTime() {
		return testStartTime;
	}
	public void setTestStartTime(String testStartTime) {
		this.testStartTime = testStartTime;
	}
	public HashMap<String, String> getDiesMap() {
		return diesMap;
	}
	public void setDiesMap(HashMap<String, String> diesMap) {
		this.diesMap = diesMap;
	}


}
