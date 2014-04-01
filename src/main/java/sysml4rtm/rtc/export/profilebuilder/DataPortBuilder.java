package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.openrtp.namespaces.rtc.Dataport;

import sysml4rtm.constants.Constants;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.IPort;

public class DataPortBuilder {

	private static final Constants.DataPortType DEFAULT_PORT_DIRECTION = Constants.DataPortType.OUT;

	private IBlock block;

	public List<Dataport> build(IBlock block) {
		this.block = block;
		List<Dataport> dataPorts = new ArrayList<Dataport>();

		for (IPort port : block.getPorts()) {
			Dataport dataPort = new Dataport();
			dataPort.setName(port.getName());
			dataPort.setPortType(getPortDirectionType(port).toString());
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

	private Constants.DataPortType getPortDirectionType(IPort port) {
		IClass portType = port.getType();
		if (portType == null) {
			IItemFlow[] itemFlows = port.getItemFlows();
			if (itemFlows == null || itemFlows.length == 0)
				throw new IllegalStateException("must validate");

			return getPortDirectionTypeWithItemFlow(port);
		}

		if (!(portType instanceof IBlock)){
			throw new IllegalStateException("must validate");
		}

		IBlock value = (IBlock) portType;
		if (value.getFlowProperties().length == 0) {
			IItemFlow[] itemFlows = port.getItemFlows();
			if (itemFlows == null || itemFlows.length == 0)
				return DEFAULT_PORT_DIRECTION;

			return getPortDirectionTypeWithItemFlow(port);
		}

		HashSet<Constants.DataPortType> directions = new HashSet<Constants.DataPortType>();
		for (IFlowProperty flow : value.getFlowProperties()) {
			if (flow.isDirectionIn()) {
				directions.add(Constants.DataPortType.IN);
			} else if (flow.isDirectionOut()) {
				directions.add(Constants.DataPortType.OUT);
			} else if (flow.isDirectionInOut()) {
				directions.add(Constants.DataPortType.INOUT);
			}
		}

		if (!isDirectionValueAllEquals(directions))
			return DEFAULT_PORT_DIRECTION;

		return directions.iterator().next();
	}

	private Constants.DataPortType getPortDirectionTypeWithItemFlow(IPort port) {
		IItemFlow[] itemFlows = port.getItemFlows();
		HashSet<Constants.DataPortType> directions = new HashSet<Constants.DataPortType>();
		for (IItemFlow itemflow : itemFlows) {
			IAttribute source = itemflow.getPartsWithPort()[0];
			IAttribute target = itemflow.getPartsWithPort()[1];

			if (source == null || target == null)
				return DEFAULT_PORT_DIRECTION;

			if (block.getId().equals(source.getType().getId())) {
				directions.add(Constants.DataPortType.OUT);
			} else if (block.getId().equals(target.getType().getId())) {
				directions.add(Constants.DataPortType.IN);
			}
		}

		if (!isDirectionValueAllEquals(directions))
			return DEFAULT_PORT_DIRECTION;

		return directions.iterator().next();
	}

	private boolean isDirectionValueAllEquals(HashSet<Constants.DataPortType> directions) {
		return directions.size() == 1;
	}

}
