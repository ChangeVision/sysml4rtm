package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.openrtp.namespaces.rtc.Dataport;
import org.openrtp.namespaces.rtc_ext.DataportExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.constants.Constants.DataPortType;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IPort;

public class DataPortBuilder {

	public List<Dataport> build(IAttribute part) {
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
		IClass dataType = port.getType();
		if (dataType == null) {
			return Constants.DEFAULT_DATA_TYPE;
		}

		return dataType.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR);
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

}
