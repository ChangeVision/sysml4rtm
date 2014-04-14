package sysml4rtm.rts.export.profilebuilder;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openrtp.namespaces.rts_ext.ComponentExt;
import org.openrtp.namespaces.rts_ext.Location;

import sysml4rtm.constants.Constants;
import sysml4rtm.finder.ExportedPartTargetFinder;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.presentation.INodePresentation;

public class ComponentBuilder {


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
			name = StringUtils.uncapitalize(part.getType().getName()) + Constants.INITIAL_INSTANCE_NUMBER;
		}
		component.setPathUri(String.format("localhost/%s.rtc", name));
		component.setInstanceName(name);
		component.setCompositeType("None");
		component.setIsRequired(true);
		component.setVisible(true);
		
		addLocation(component,part);
		
		return component;
	}

	private void addLocation(ComponentExt component, IAttribute part) {
		Location location = new Location();
		long x = 100;
		long y = 100;
		try{
			if(part.getPresentations().length > 0){
				INodePresentation p = (INodePresentation) part.getPresentations()[0];
				x = (long) p.getLocation().getX();
				y = (long) p.getLocation().getY();
			}
		}catch(InvalidUsingException e){
		}
		
		location.setX(BigInteger.valueOf(x));
		location.setY(BigInteger.valueOf(y));
		location.setHeight(BigInteger.valueOf(-1));
		location.setWidth(BigInteger.valueOf(-1));
		location.setDirection("RIGHT");
		component.setLocation(location);
	}

	private List<IAttribute> getGeneratedTargetElements(IDiagram diagram) {
		List<IAttribute> elements = new ExportedPartTargetFinder()
				.find((IInternalBlockDiagram) diagram);
		return elements;
	}
}
