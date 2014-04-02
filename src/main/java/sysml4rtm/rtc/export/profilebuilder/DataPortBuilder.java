package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openrtp.namespaces.rtc.Dataport;
import org.openrtp.namespaces.rtc_ext.DataportExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.idl.generator.IDLUtils;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.IPort;

public class DataPortBuilder {

	private IAttribute part;
	
	public List<Dataport> build(IAttribute part) {
		this.part = part;
		IBlock block = (IBlock) part.getType();
		List<Dataport> dataPorts = new ArrayList<Dataport>();
		
		for (IPort port : block.getPorts()) {
			Dataport dataPort = new DataportExt();
			dataPort.setName(port.getName());
			dataPort.setPortType(getPortDirectionType(part,port).toString());
			dataPort.setType(getDataType(port));
			dataPorts.add(dataPort);
		}

		return dataPorts;
	}

	private String getDataType(IPort port) {
		IClass portDataType = null;
		
		portDataType = getPortTypeFromItemFlow(port);
		if(portDataType == null){
			portDataType = getPortTypeFromFlowProperty(port);
		}
		
		if(portDataType == null)
			throw new IllegalStateException("must validate");
		
		return getPortType(portDataType);
	}

	private IClass getPortTypeFromFlowProperty(IPort port) {
		IBlock portType = (IBlock)port.getType();
		if(portType == null)
			throw new IllegalStateException(String.format("%s.%s must validate",ModelUtils.getPartName(part),ModelUtils.getPortName(port)));
		
		IFlowProperty[] flowProperties = portType.getFlowProperties();
		if(flowProperties == null || flowProperties.length == 0)
			return null;
		
		if(!hasFlowPropertieshaveSameType(flowProperties)){
			throw new IllegalStateException(String.format("%s.%s must validate",ModelUtils.getPartName(part),ModelUtils.getPortName(port)));
		}
		
		return flowProperties[0].getType();
	}

	private IClass getPortTypeFromItemFlow(IPort port) {
		IItemFlow[] itemflows = port.getItemFlows();
		if(itemflows == null || itemflows.length == 0)
			return null;
		
		if(!hasItemPropertiesHaveSameType(itemflows)){
			throw new IllegalStateException(String.format("%s.%s must validate",ModelUtils.getPartName(part),ModelUtils.getPortName(port)));
		}
		return getConveyDataType(itemflows[0]);
	}

	private String getPortType(IClass dataType) {
		String typeName = dataType.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR);
		if (IDLUtils.isSysMLBuiltinType(typeName)) {
			return IDLUtils.convertSysmlToIdlType(typeName);
		}else if (IDLUtils.isIDLPrimitiveType(typeName)){
			return IDLUtils.convertIDLType(typeName);
		}else{
			return typeName;
		}
	}
	
	private boolean hasPortType(IPort port){
		return port.getType() != null;
	}
	
	private Constants.DataPortType getPortDirectionType(IAttribute part, IPort port) {
		DataPortType direction;
		if (hasPortType(port)) {
			IBlock portType = (IBlock) port.getType();
			direction = ModelUtils.getDirection(portType.getFlowProperties());
			if(!direction.equals(DataPortType.UNKNOWN))
				return direction;
		}
		
		return ModelUtils.getDirection(part, port.getItemFlows());
	}
	
	
	private static boolean hasItemPropertiesHaveSameType(IItemFlow[] itemflows){
		HashSet<IClass> types = new HashSet<IClass>();
		for (IItemFlow itemflow : itemflows) {
			types.add(getConveyDataType(itemflow));
		}

		return types.size() == 1;
	}

	private static IClass getConveyDataType(IItemFlow itemflow) {
		IAttribute itemProperty = itemflow.getItemProperty();
		IClass conveyDataType = null;
		if(hasTypeFromItemProperty(itemProperty)){
			conveyDataType = itemProperty.getType();
		}else if (hasConvey(itemflow)){
			conveyDataType = itemflow.getConveys()[0];
		}
		return conveyDataType;
	}

	private static boolean hasConvey(IItemFlow itemflow) {
		return itemflow.getConveys().length > 0;
	}

	private static boolean hasTypeFromItemProperty(IAttribute itemProperty) {
		return itemProperty !=null && itemProperty.getType() != null;
	}
	
	private static boolean hasFlowPropertieshaveSameType(IFlowProperty[] flowProperties) {
		HashSet<IClass> types = new HashSet<IClass>();
		for (IFlowProperty flowProperty : flowProperties) {
			types.add(flowProperty.getType());
		}
		return types.size() == 1;
	}

}
