package mestools;

import properties.GetStreamFromMes;
import properties.MesProperties;

/**
 * bin summary into MES
 * @author shawn.sun
 * @category IT
 * @version 2.0
 * @since 2018.3.15
 */
public class WaferidInforIntoMes {
	private String URL="?Action=UploadBinSummaryPerWafer&ACode=";
	public void write(String lotNumber,String waferID,String CP,String BinSummary)
	{
		try {
			int RandomNumber=(int) ((Math.random()*100000000)/1);
			URL=URL+MesProperties.VERIFICATION_CODE+"&ItemName=WaferLot:"+lotNumber+"|WaferID:"+waferID+"|CP:"+CP.trim()+BinSummary+"&rand="+RandomNumber;
			GetStreamFromMes.getStream(URL);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
