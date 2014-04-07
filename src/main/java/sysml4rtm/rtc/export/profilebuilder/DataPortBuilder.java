package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.openrtp.namespaces.rtc.Dataport;
import org.openrtp.namespaces.rtc_ext.DataportExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.idl.generator.CustomTypeIDLGenerator;
import sysml4rtm.utils.IDLUtils;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.IPort;

public class DataPortBuilder {

	private IAttribute part;
	private CustomTypeIDLGenerator customTypeIDLGenerator;

	public DataPortBuilder(String pathToOutputFolder) {
		customTypeIDLGenerator = new CustomTypeIDLGenerator(pathToOutputFolder);
	}

	public List<Dataport> build(IAttribute part) {
		this.part = part;
		IBlock block = (IBlock) part.getType();
		List<Dataport> dataPorts = new ArrayList<Dataport>();

		for (IPort port : block.getPorts()) {
			if (!ModelUtils.hasServiceInterface(port)) {
				Dataport dataPort = new DataportExt();
				dataPort.setName(port.getName());
				dataPort.setPortType(getPortDirectionType(part, port).toString());
				
				IClass portDataType = getDataType(port);
				dataPort.setType(getDataTypeExpression(portDataType));
				
				if(IDLUtils.isCustomType(portDataType.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR))){
					generateCustomTypeIdl(portDataType);
				}
				
				dataPorts.add(dataPort);
			}
		}

		return dataPorts;
	}

	private IClass getDataType(IPort port) {
		IClass portDataType = null;

		portDataType = getDataTypeFromItemFlow(port);
		if (portDataType == null) {
			portDataType = getDataTypeFromFlowProperty(port);
		}

		if (portDataType == null)
			throw new IllegalStateException("must validate");

		return portDataType;
	}

	private IClass getDataTypeFromFlowProperty(IPort port) {
		IBlock portType = (IBlock) port.getType();
		if (portType == null)
			throw new IllegalStateException(String.format("%s.%s must validate",
					ModelUtils.getPartName(part), ModelUtils.getPortName(port)));

		IFlowProperty[] flowProperties = portType.getFlowProperties();
		if (flowProperties == null || flowProperties.length == 0)
			return null;

		if (!ModelUtils.hasFlowPropertieshaveSameType(flowProperties)) {
			throw new IllegalStateException(String.format("%s.%s must validate",
					ModelUtils.getPartName(part), ModelUtils.getPortName(port)));
		}

		return flowProperties[0].getType();
	}

	private IClass getDataTypeFromItemFlow(IPort port) {
		IItemFlow[] itemflows = port.getItemFlows();
		if (itemflows == null || itemflows.length == 0)
			return null;

		if (!ModelUtils.hasItemPropertiesHaveSameType(itemflows)) {
			throw new IllegalStateException(String.format("%s.%s must validate",
					ModelUtils.getPartName(part), ModelUtils.getPortName(port)));
		}
		return ModelUtils.getConveyDataType(itemflows[0]);
	}

	private String getDataTypeExpression(IClass dataType) {
		String typeName = dataType.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR);
		if (IDLUtils.isSysMLBuiltinType(typeName)) {
			return IDLUtils.convertSysmlToIdlType(typeName);
		} else if (IDLUtils.isIDLPrimitiveType(typeName)) {
			return IDLUtils.convertIDLType(typeName);
		} else {
			return typeName;
		}
	}

	private void generateCustomTypeIdl(IClass dataType) {
		customTypeIDLGenerator.generateCustomTypeIdl(dataType, Constants.ENCODING);
	}

	private Constants.DataPortType getPortDirectionType(IAttribute part, IPort port) {
		DataPortType direction;
		if (ModelUtils.hasPortType(port)) {
			IBlock portType = (IBlock) port.getType();
			direction = ModelUtils.getDirection(portType.getFlowProperties());
			if (!direction.equals(DataPortType.UNKNOWN))
				return direction;
		}

		return ModelUtils.getDirection(part, port.getItemFlows());
	}

}
