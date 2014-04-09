package sysml4rtm.rts.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openrtp.namespaces.rts.Dataport;
import org.openrtp.namespaces.rts.Serviceport;
import org.openrtp.namespaces.rts_ext.ComponentExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.finder.InternalBlockDiagramExportedTargetFinder;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.IPort;

public class ComponentBuilder {

	private static final String INITIAL_INSTANCE_NUMBER = "0";

	public List<ComponentExt> build(IDiagram currentDiagram) {
		List<IAttribute> parts = getGeneratedTargetElements(currentDiagram);
		
		List<ComponentExt> components = new ArrayList<ComponentExt>();
		for(IAttribute part : parts){
			components.add(build(part));
		}
		return components;
	}

	private ComponentExt build(IAttribute part) {
		ComponentExt component = new ComponentExt();
		component.setId(String.format("RTC:Vender:%s:1.0", part.getType().getFullName(Constants.MODEL_NAMESPACE_SEPARATOR)));
		String name = part.getName();
		if(StringUtils.isEmpty(name)){
			name = StringUtils.uncapitalize(part.getType().getName()) + INITIAL_INSTANCE_NUMBER;
		}
		component.setPathUri(String.format("localhost:2809/%s.rtc", name));
		component.setInstanceName(name);
		component.setCompositeType("None");
		component.setIsRequired(true);
		component.setVisible(true);
		
		buildDataPort(component, part);
		buildServicePort(component,part);
		return component;
	}

	private void buildServicePort(ComponentExt component, IAttribute part) {
		IBlock block = (IBlock) part.getType();
		for(IPort port : block.getPorts()){
			if(!ModelUtils.hasServiceInterface(port)){
				continue;
			}
			Serviceport serviceport = new Serviceport();
			serviceport.setName(port.getName());
			component.getServicePorts().add(serviceport);
		}
	}

	private void buildDataPort(ComponentExt component, IAttribute part) {
		IBlock block = (IBlock) part.getType();
		for(IPort port : block.getPorts()){
			if(ModelUtils.hasServiceInterface(port)){
				continue;
			}
			Dataport dataport = new Dataport();
			dataport.setName(port.getName());
			component.getDataPorts().add(dataport);
		}
	}

	private List<IAttribute> getGeneratedTargetElements(IDiagram diagram) {
		List<IAttribute> elements = new InternalBlockDiagramExportedTargetFinder()
				.find((IInternalBlockDiagram) diagram);
		return elements;
	}
}
