package sysml4rtm.rts.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openrtp.namespaces.rts.TargetPort;
import org.openrtp.namespaces.rts_ext.DataportConnectorExt;
import org.openrtp.namespaces.rts_ext.Property;

import sysml4rtm.constants.Constants;
import sysml4rtm.finder.ExportedConnectorTargetFinder;
import sysml4rtm.rtc.export.profilebuilder.DataPortBuilder;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.IPort;

public class DataPortConnectorsBuilder {


	public List<DataportConnectorExt> build(IDiagram currentDiagram) {
		List<DataportConnectorExt> dataPortConnectors =  new ArrayList<DataportConnectorExt>();

		ExportedConnectorTargetFinder finder = new ExportedConnectorTargetFinder();
		List<IConnector> connectors = finder.findDataPortConnector((IInternalBlockDiagram) currentDiagram);
		
		for(IConnector connector : connectors){
			DataportConnectorExt dataportConnector = new DataportConnectorExt();
			
			dataportConnector.setConnectorId(connector.getId());

			String name = connector.getName();
			if(StringUtils.isEmpty(name)){
				name = connector.getId();
			}
			dataportConnector.setName(name);
			
			String dataType = getPortDataType(connector);
			dataportConnector.setDataType(String.format("IDL:%s:1.0", dataType));
			
			dataportConnector.setInterfaceType("corba_cdr");
			dataportConnector.setDataflowType("push");
			dataportConnector.setSubscriptionType("flush");
			
			addSourceTargetDataPort(dataportConnector,connector);
			
			addDataPortDataTypeProperty(dataportConnector);
			
			dataPortConnectors.add(dataportConnector);
		}
		
		return dataPortConnectors;
	}

	private void addDataPortDataTypeProperty(DataportConnectorExt dataportConnector) {
		Property prop = new Property();
		prop.setName("dataport.data_type");
		prop.setValue(dataportConnector.getDataType());
		dataportConnector.getProperties().add(prop);
	}

	private void addSourceTargetDataPort(DataportConnectorExt dataportConnector, IConnector connector) {
		IPort[] ports = connector.getPorts();
		IPort srcPort = ports[0];
		IPort targetPort = ports[1];
		
		IAttribute[] parts = connector.getPartsWithPort();
		IAttribute srcPart = parts[0];
		IAttribute targetPart = parts[1];
		
		dataportConnector.setSourceDataPort(buildTargetPort(srcPort, srcPart));
		dataportConnector.setTargetDataPort(buildTargetPort(targetPort, targetPart));
	}

	private TargetPort buildTargetPort(IPort port, IAttribute part) {
		TargetPort targetPort = new TargetPort();
		targetPort.setPortName(port.getName());
		
		targetPort.setComponentId(String.format("RTC:Vender:%s:1.0", part.getType().getFullName(Constants.MODEL_NAMESPACE_SEPARATOR)));
		
		String name = part.getName();
		if(StringUtils.isEmpty(name)){
			name = part.getType().getName() + Constants.INITIAL_INSTANCE_NUMBER;
		}
		targetPort.setInstanceName(name);
		return targetPort;
	}

	private String getPortDataType(IConnector connector) {
		IPort[] ports = connector.getPorts();
		IClass dataType = DataPortBuilder.getDataType(ports[0]);
		return dataType.getFullName("/");
	}

}
