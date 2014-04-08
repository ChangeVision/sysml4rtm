package sysml4rtm.rts.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openrtp.namespaces.rts_ext.ComponentExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.finder.InternalBlockDiagramExportedTargetFinder;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

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
		return component;
	}

	private List<IAttribute> getGeneratedTargetElements(IDiagram diagram) {
		List<IAttribute> elements = new InternalBlockDiagramExportedTargetFinder()
				.find((IInternalBlockDiagram) diagram);
		return elements;
	}
}
