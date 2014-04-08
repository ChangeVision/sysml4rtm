package sysml4rtm.rtc.export.profilebuilder;

import org.openrtp.namespaces.rtc.BasicInfo;
import org.openrtp.namespaces.rtc.RtcProfile;

import sysml4rtm.constants.Constants;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;

public class RtcProfileBuilder {

	private RtcProfile profile = null;
	private BasicInfoBuilder basicInfoBuilder;
	private String pathToOutput;

	public RtcProfileBuilder() {
		basicInfoBuilder = new BasicInfoBuilder();
	}

	public void setBasicInfoBuilder(BasicInfoBuilder builder) {
		this.basicInfoBuilder = builder;
	}

	public RtcProfile createRtcProfile(IAttribute part) {
		profile = new RtcProfile();
		
		BasicInfo basicinfo = basicInfoBuilder.build(part);
		profile.setBasicInfo(basicinfo);

		ActionBuilder actionBuilder = new ActionBuilder();
		profile.setActions(actionBuilder.build(basicinfo));

		DataPortBuilder dataportBuilder = new DataPortBuilder(pathToOutput);
		profile.getDataPorts().addAll(dataportBuilder.build(part));

		ServicePortBuilder serviceportBuilder = new ServicePortBuilder(pathToOutput);
		profile.getServicePorts().addAll(serviceportBuilder.build(part));
		
		buildBase(part, basicinfo);
		return profile;
	}

	private void buildBase(IAttribute part,BasicInfo basicinfo) {
		IBlock block = (IBlock) part.getType();
		profile.setId(String.format("RTC:%s:%s:%s:1.0.0", basicinfo.getVendor(),
				basicinfo.getCategory(), block.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR)));
		profile.setVersion("0.2");
	}

	public void setPathToOutputFolder(String pathToOutput) {
		this.pathToOutput = pathToOutput;
	}

}
