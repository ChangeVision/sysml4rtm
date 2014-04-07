package sysml4rtm.rtc.export.profilebuilder;

import org.openrtp.namespaces.rtc.BasicInfo;
import org.openrtp.namespaces.rtc.RtcProfile;

import com.change_vision.jude.api.inf.model.IAttribute;

public class RtcProfileBuilder {

	private RtcProfile profile = null;
	private BasicInfoBuilder basicInfoBuilder;
	private String pathToOutput;

	public RtcProfileBuilder() {
		profile = new RtcProfile();
		basicInfoBuilder = new BasicInfoBuilder();
	}

	public void setBasicInfoBuilder(BasicInfoBuilder builder) {
		this.basicInfoBuilder = builder;
	}

	public RtcProfile createRtcProfile(IAttribute part) {
		BasicInfo basicinfo = basicInfoBuilder.build(part);
		profile.setBasicInfo(basicinfo);

		ActionBuilder actionBuilder = new ActionBuilder();
		profile.setActions(actionBuilder.build(basicinfo));

		DataPortBuilder dataportBuilder = new DataPortBuilder(pathToOutput);
		profile.getDataPorts().addAll(dataportBuilder.build(part));

		ServicePortBuilder serviceportBuilder = new ServicePortBuilder(pathToOutput);
		profile.getServicePorts().addAll(serviceportBuilder.build(part));
		
		buildBase(basicinfo);
		return profile;
	}

	private void buildBase(BasicInfo basicinfo) {
		profile.setId(String.format("RTC:%s:%s:%s:1.0.0", basicinfo.getVendor(),
				basicinfo.getCategory(), basicinfo.getName()));
		profile.setVersion("0.2");
	}

	public void setPathToOutputFolder(String pathToOutput) {
		this.pathToOutput = pathToOutput;
	}

}
