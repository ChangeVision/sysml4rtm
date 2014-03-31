package sysml4rtm.utils;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IConnector;
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
			throw new IllegalArgumentException();

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
	
}
