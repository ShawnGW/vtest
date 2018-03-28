package mestools;

import vtestbeans.RawdataBean;

public class InitLotConfig {
	private RawdataBean bean;
	public InitLotConfig(RawdataBean bean) {
		// TODO Auto-generated constructor stub
		this.bean=bean;
	}
	public void init(String[] informations)
	{
		for (int i = 0; i < informations.length; i++) {
			if (getTitle(informations[i]).equals("[LOT:LotNum]")) {
				bean.setInnerLot(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:MotherLot]")) {
				bean.setInnnerMotherLot(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:CustCode]")) {
				bean.setCustomerCode(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:CustLot]")) {
				bean.setCustomerLot(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:StartDateTime]")) {
				bean.setStartTime(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:PE_Owner]")) {
				bean.setpEOwner(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Tester_Software_Revision]")) {
				bean.setTesterSoftwareRevision(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[ProcessSpec:TesterModel]")) {
				bean.setTesterModel(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Wafer_Sequence]")) {
				bean.setWaferSequnce(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:WaferID_read]")) {
				bean.setWaferIDRead(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:ProberCard]")) {
				bean.setProberCard(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:CustCName]")) {
				bean.setChineseName(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:ShortName]")) {
				bean.setChinesShortName(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:PartNum]")) {
				bean.setPartNum(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:ProcessRevision]")) {
				bean.setProcessRevision(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[WO:WorkOrderID]")) {
				bean.setWorkOrder(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:LotStatus]")) {
				bean.setLotStatus(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:WipStep]")) {
				bean.setWipStep(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[WO:PONumber]")) {
				bean.setPoNumber(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:WipStage]")) {
				bean.setWipStage(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:CustomerName]")) {
				bean.setCustomerName(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:PartDesc]")) {
				bean.setPartDESC(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:CustPart]")) {
				bean.setCustomerDevice(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:WaferNotch]")) {
				bean.setWaferNotch(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:MapOrInk]")) {
				bean.setMapOrInk(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:isOTP]")) {
				bean.setIsOTP(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:isCOP]")) {
				bean.setiSCOP(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:FabDevice]")) {
				bean.setFabDevice(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:AsyDevice]")) {
				bean.setAsyDevice(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:AssignedTesters]")) {
				bean.setAssignedTesters(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:isTrimed]")) {
				bean.setIsTrimed(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:LoadFile]")) {
				bean.setLoadFile(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Cust_TestSpec]")) {
				bean.setCustomerTestSpec(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Map_Reference]")) {
				bean.setMapReference(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[ProcessShortFlow]")) {
				bean.setProcessShortFlow(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[LOT:WaferPCS]")) {
				bean.setWaferPCS(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:GPIB_Bin]")) {
				bean.setgPIBBin(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Site-Site]")) {
				bean.setSiteToSite(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Continuous_Fail]")) {
				bean.setContinueFail(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[PN:WaferSize]")) {
				bean.setWaferSize(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[LOT:HoldCount]")) {
				bean.setHoldCount(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:StopYield]")) {
				bean.setStopYield(Double.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[PN:DieSizeX]")) {
				bean.setIndexX(Double.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[PN:DieSizeY]")) {
				bean.setIndexY(Double.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:Major_Fail]")) {
				bean.setMajorFail(getValue(informations[i]));
			}
			if (getTitle(informations[i]).equals("[PN:DPW]")) {
				bean.setMesStandGrossDies(Integer.valueOf(getValue(informations[i])));
			}	
			if (getTitle(informations[i]).equals("[PN:TestedDies]")) {
				bean.setMesTestGrossDies(Integer.valueOf(getValue(informations[i])));
			}
			if (getTitle(informations[i]).equals("[LOT:ProcessSPEC]")) {
				bean.setProcessSPEC(getValue(informations[i]));
			}
//			if (getTitle(informations[i]).equals("[FlexibleItem_ProcessSpecAttributes:TestTime/Pcs]")) {
//				bean.setTheoryTestTimes(getValue(informations[i]));
//			}
		}
	}
	private String getTitle(String component)
	{
		return component.split("=")[0];
	}
	private String getValue(String component)
	{
		String value=component.split("=")[1].trim();
		if (value.equals("")) {
			return "NA";
		}else {
			return value;
		}		
	}
}
