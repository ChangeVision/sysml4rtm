package sysml4rtm.utils;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.exceptions.ApplicationException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class ModelUtils {

	public static boolean hasPackage(INamedElement element) {
		IElement owner = element.getOwner();
		boolean hasNamespace = false;
		if (owner != null && owner instanceof INamedElement && owner.getOwner() != null) {
			hasNamespace = true;
		}
		return hasNamespace;
	}

	public static String getPackage(INamedElement element) {
		return getPackage(element, "::");
	}

	public static String getPackage(INamedElement element, String pathSeparator) {
		StringBuffer sb = new StringBuffer();
		IElement owner = element.getOwner();
		if (owner != null && owner instanceof INamedElement) {
			IElement ownerOwner = owner.getOwner();
			while (owner != null && owner instanceof INamedElement && ownerOwner != null) {
				sb.insert(0, ((INamedElement) owner).getName() + pathSeparator);
				owner = ownerOwner;
				ownerOwner = ownerOwner.getOwner();
			}
		}
		return StringUtils.removeEnd(sb.toString(), pathSeparator);
	}

	public static boolean isPart(INamedElement element) {
		if (element instanceof IAttribute) {
			if(((IAttribute) element).getType() == null) return false;
			
			IAssociation association = ((IAttribute) element).getAssociation();
			if (association != null) {
				IAttribute[] memberEnds = association.getMemberEnds();
				if (memberEnds[0].equals(element)) {
					return memberEnds[1].isComposite();
				} else {
					return memberEnds[0].isComposite();
				}
			}
		}
		return false;
	}
	
	public static boolean isBlock(INamedElement element) {
		return element instanceof IBlock;
	}
	
	public static boolean isRTCBlock(INamedElement element){
		return isBlock(element) && element.hasStereotype(Constants.STEREOTYPE_RTC);
	}

	public static String getPortName(IPort port) {
		return port.getType() != null ? port.getName() + ":" + port.getType().getName() : port
				.getName();
	}

	public static String getPartName(IAttribute attr) {
		return attr.getType() != null ? attr.getName() + ":" + attr.getType().getName() : attr
				.getName();
	}
	
	public static DataPortType getDirection(IFlowProperty[] flowProperties) {
		if (flowProperties == null || flowProperties.length == 0)
			return DataPortType.UNKNOWN;

		HashSet<DataPortType> directions = new HashSet<DataPortType>();
		for (IFlowProperty flow : flowProperties) {
			if (flow.isDirectionIn()) {
				directions.add(DataPortType.IN);
			} else if (flow.isDirectionOut()) {
				directions.add(DataPortType.OUT);
			} else if (flow.isDirectionInOut()) {
				directions.add(DataPortType.INOUT);
			}
		}

		if (directions.size() != 1) {
			return DataPortType.INOUT;
		}

		return directions.iterator().next();
	}

	public static DataPortType getDirection(IAttribute me, IItemFlow itemflow)	{
		IAttribute source = getSourcePart(itemflow);
		IAttribute target = getTargetPart(itemflow);
	
		if(me.getId().equals(source.getId())){
			return DataPortType.OUT;
		}else if (me.getId().equals(target.getId())){
			return DataPortType.IN;
		}else{
			throw new IllegalStateException();
		}
	}
	
	public static DataPortType getDirection(IAttribute me, IItemFlow[] itemflows)	{
		if(itemflows == null || itemflows.length == 0)
			return DataPortType.UNKNOWN;
		
		HashSet<DataPortType> directions = new HashSet<DataPortType>();
		for(IItemFlow itemflow : itemflows){
			directions.add(getDirection(me, itemflow));
		}
		
		if (directions.size() != 1) {
			return DataPortType.INOUT;
		}

		return directions.iterator().next();
	}
	
	private static IAttribute getTargetPart(IConnector itemflow) {
		if (itemflow.getParts()[1] != null)
			return itemflow.getParts()[1];

		if (itemflow.getPartsWithPort()[1] != null)
			return itemflow.getPartsWithPort()[1];

		return null;
	}

	private static IAttribute getSourcePart(IConnector itemflow) {
		if (itemflow.getParts()[0] != null)
			return itemflow.getParts()[0];

		if (itemflow.getPartsWithPort()[0] != null)
			return itemflow.getPartsWithPort()[0];

		return null;
	}
	
	public static IDiagram getCurrentDiagram() throws InvalidUsingException, ClassNotFoundException {
		return AstahAPI.getAstahAPI().getProjectAccessor().getViewManager().getDiagramViewManager()
				.getCurrentDiagram();
	}

	public static boolean hasPortType(IPort port){
		return port.getType() != null;
	}

	public static boolean hasConvey(IItemFlow itemflow) {
		return itemflow.getConveys().length > 0;
	}

	public static boolean hasTypeFromItemProperty(IAttribute itemProperty) {
		return itemProperty !=null && itemProperty.getType() != null;
	}

	public static boolean hasItemPropertiesHaveSameType(IItemFlow[] itemflows){
		HashSet<IClass> types = new HashSet<IClass>();
		for (IItemFlow itemflow : itemflows) {
			IClass conveyDataType = ModelUtils.getConveyDataType(itemflow);
			types.add(conveyDataType);
		}
	
		return types.size() == 1;
	}

	public static boolean hasFlowPropertieshaveSameType(IFlowProperty[] flowProperties) {
		HashSet<IClass> types = new HashSet<IClass>();
		for (IFlowProperty flowProperty : flowProperties) {
			types.add(flowProperty.getType());
		}
		return types.size() == 1;
	}

	public static IClass getConveyDataType(IItemFlow itemflow) {
		IAttribute itemProperty = itemflow.getItemProperty();
		IClass conveyDataType = null;
		if(hasTypeFromItemProperty(itemProperty)){
			conveyDataType = itemProperty.getType();
		}else if (hasConvey(itemflow)){
			conveyDataType = itemflow.getConveys()[0];
		}
		return conveyDataType;
	}

	public static boolean hasServiceInterface(IPort port) {
		IClass[] requires = port.getRequiredInterfaces();
		IClass[] provides = port.getProvidedInterfaces();
		
		return (requires != null && requires.length > 0) || (provides != null && provides.length > 0);
	}

	public static boolean isMarkedExport(INamedElement element) {
		if(isPart(element)){
			IAttribute part = (IAttribute) element;
			if(part.hasStereotype(Constants.STEREOTYPE_RTC))
					return true;
			return part.getType() != null && part.getType().hasStereotype(Constants.STEREOTYPE_RTC);
		}
		return false;
	}

	public static  boolean isOnCurrentDiagram(IDiagram diagram , INamedElement elem) {
		if (!ModelUtils.hasPresentation(elem))
			return false;
		try {
			return elem.getPresentations()[0].getDiagram().getId().equals(diagram.getId());
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static  boolean hasPresentation(INamedElement elem) {
		try {
			return elem.getPresentations() != null && elem.getPresentations().length > 0;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}


}
