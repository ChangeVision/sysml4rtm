package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.openrtp.namespaces.rtc_ext.DataportExt;
import org.openrtp.namespaces.rtc_ext.Position;

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

	private CustomTypeIDLGenerator customTypeIDLGenerator;
	private String pathToOutputFolder;

	public DataPortBuilder(String pathToOutputFolder) {
		this.pathToOutputFolder = pathToOutputFolder;
		customTypeIDLGenerator = new CustomTypeIDLGenerator(pathToOutputFolder);
	}
	

	public List<DataportExt> build(IAttribute part) {
		IBlock block = (IBlock) part.getType();
		List<DataportExt> dataPorts = new ArrayList<DataportExt>();

		for (IPort port : block.getPorts()) {
			if (!ModelUtils.hasServiceInterface(port)) {
				DataportExt dataPort = new DataportExt();
				dataPort.setName(port.getName());
				dataPort.setPortType(getPortDirectionType(part, port).toString());
				dataPort.setPosition(Position.LEFT);
				
				IClass portDataType = getDataType(port);
				dataPort.setType(getDataTypeExpression(portDataType));
				
				if(IDLUtils.isCustomType(portDataType.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR))){
					dataPort.setIdlFile(getPathToIdlFile(portDataType));
					generateCustomTypeIdl(portDataType);
				}
				
				dataPorts.add(dataPort);
			}
		}

		return dataPorts;
	}
	
	private String getPathToIdlFile(IClass target) {
		return pathToOutputFolder + SystemUtils.FILE_SEPARATOR + target.getFullName("/") + ".idl";
	}
	
	public static IClass getDataType(IPort port) {
		IClass portDataType = null;

		portDataType = getDataTypeFromItemFlow(port);
		if (portDataType == null) {
			portDataType = getDataTypeFromFlowProperty(port);
		}

		if (portDataType == null)
			throw new IllegalStateException("must validate");

		return portDataType;
	}

	private static IClass getDataTypeFromFlowProperty(IPort port) {
		IBlock portType = (IBlock) port.getType();
		if (portType == null)
			throwValidationException(port);

		IFlowProperty[] flowProperties = portType.getFlowProperties();
		if (flowProperties == null || flowProperties.length == 0)
			return null;

		if (!ModelUtils.hasFlowPropertieshaveSameType(flowProperties)) {
			throwValidationException(port);
		}

		return flowProperties[0].getType();
	}


	private static void throwValidationException(IPort port) {
		IClass type = port.getType();
		throw new IllegalStateException(String.format("%s.%s must validate",
				type.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR), ModelUtils.getPortName(port)));
	}

	private static IClass getDataTypeFromItemFlow(IPort port) {
		IItemFlow[] itemflows = port.getItemFlows();
		if (itemflows == null || itemflows.length == 0)
			return null;

		if (!ModelUtils.hasItemPropertiesHaveSameType(itemflows)) {
			throwValidationException(port);
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
			if (!direction.equals(DataPortType.UNKNOWN)){
				if(port.isConjugated()){
					return DataPortType.getConjugatedType(direction);
				}
				return direction;				
			}
		}

		return ModelUtils.getDirection(part, port.getItemFlows());
	}

}
