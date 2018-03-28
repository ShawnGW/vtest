package mestools;

import properties.MesProperties;

/**
 * get softBin define
 * @author shawn.sun
 * @category IT
 * @version 2.0
 * @since 2018.3.15
 */

public class GetSoftBinDefineFromMes implements MesInterface{
	private final String URL="?ACode="+MesProperties.VERIFICATION_CODE+"&Action=GetSystemParametersByParmType&ItemName=SoftBinDefinition";
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return URL;
	}
}
