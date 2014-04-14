package sysml4rtm.rts.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openrtp.namespaces.rts_ext.ServiceportConnectorExt;

import sysml4rtm.finder.ExportedConnectorTargetFinder;

import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class ServicePortConnectorsBuilder {


	public List<ServiceportConnectorExt> build(IDiagram currentDiagram) {
		List<ServiceportConnectorExt> servicePortConnectors =  new ArrayList<ServiceportConnectorExt>();

		ExportedConnectorTargetFinder finder = new ExportedConnectorTargetFinder();
		List<IConnector> connectors = finder.findServicePortConnector((IInternalBlockDiagram) currentDiagram);
		
		for(IConnector connector : connectors){
			ServiceportConnectorExt servicePortConnector = new ServiceportConnectorExt();
			
			servicePortConnector.setConnectorId(connector.getId());

			String name = connector.getName();
			if(StringUtils.isEmpty(name)){
				name = connector.getId();
			}
			servicePortConnector.setName(name);
			
			servicePortConnectors.add(servicePortConnector);
		}
		
		
		List<IDependency> assemblyConnectors = finder.findAssemblyConnector((IInternalBlockDiagram) currentDiagram);
		for(IDependency assemblyConnector : assemblyConnectors){
			ServiceportConnectorExt servicePortConnector = new ServiceportConnectorExt();
			
			servicePortConnector.setConnectorId(assemblyConnector.getId());
			servicePortConnector.setName(assemblyConnector.getId());
			
			servicePortConnectors.add(servicePortConnector);
		}
		return servicePortConnectors;
	}

}
