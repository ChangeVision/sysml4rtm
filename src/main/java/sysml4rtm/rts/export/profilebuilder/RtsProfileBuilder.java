package sysml4rtm.rts.export.profilebuilder;

import org.openrtp.namespaces.rts_ext.RtsProfileExt;

import com.change_vision.jude.api.inf.model.IDiagram;

public class RtsProfileBuilder {

	private RtsProfileBasicInfoBuilder basicInfoBuilder;

	public RtsProfileBuilder() {
		basicInfoBuilder = new RtsProfileBasicInfoBuilder();
	}

	public void setBasicInfoBuilder(RtsProfileBasicInfoBuilder builder) {
		this.basicInfoBuilder = builder;
	}
	
	public RtsProfileExt createRtsProfile(IDiagram currentDiagram) {
		
		RtsProfileExt profile = basicInfoBuilder.build(currentDiagram);
		
		ComponentBuilder componentBuilder = new ComponentBuilder();
		profile.getComponents().addAll(componentBuilder.build(currentDiagram));
		
		DataPortConnectorsBuilder dataPortConnectorsBuilder = new DataPortConnectorsBuilder();
		profile.getDataPortConnectors().addAll(dataPortConnectorsBuilder.build(currentDiagram));
		
		ServicePortConnectorsBuilder servicePortConnectorsBuilder = new ServicePortConnectorsBuilder();
		profile.getServicePortConnectors().addAll(servicePortConnectorsBuilder.build(currentDiagram));
		
		return profile;
	}

}
