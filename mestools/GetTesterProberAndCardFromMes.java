package mestools;

import properties.MesProperties;

/**
 * get Tester and Prober from MES
 * @author shawn.sun
 * @category IT
 * @version 2.0
 * @since 2018.3.15
 */
public class GetTesterProberAndCardFromMes implements MesInterface{
	private String URL="?ACode="+MesProperties.VERIFICATION_CODE+"&Action=GetLotHistory&ItemName=";
	private String lot;
	public GetTesterProberAndCardFromMes(String lot) {
		// TODO Auto-generated constructor stub
		this.lot=lot;
	}
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return URL=URL+lot;
	}
}
