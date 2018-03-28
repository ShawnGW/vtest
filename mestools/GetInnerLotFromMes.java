package mestools;

import properties.MesProperties;

/**
 * get inner lot
 * @author shawn.sun
 * @category IT
 * @version 2.0
 * @since 2018.3.15
 */
public class GetInnerLotFromMes implements MesInterface{
	private String URL="?ACode="+MesProperties.VERIFICATION_CODE+"&Action=GetWaferAttributes&ItemName=";
	private String waferId;
	public GetInnerLotFromMes(String waferId) {
		// TODO Auto-generated constructor stub
		this.waferId=waferId;
	}
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return URL+waferId;
	}

}
