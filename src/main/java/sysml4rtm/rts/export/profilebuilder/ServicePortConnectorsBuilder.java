package sysml4rtm.rts.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openrtp.namespaces.rts.TargetPort;
import org.openrtp.namespaces.rts_ext.ServiceportConnectorExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.finder.ExportedConnectorTargetFinder;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.IPort;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;

public class ServicePortConnectorsBuilder {

	public List<ServiceportConnectorExt> build(IDiagram currentDiagram) {
		List<ServiceportConnectorExt> servicePortConnectors = new ArrayList<ServiceportConnectorExt>();

		ExportedConnectorTargetFinder finder = new ExportedConnectorTargetFinder();
		List<IConnector> connectors = finder
				.findServicePortConnector((IInternalBlockDiagram) currentDiagram);

		for (IConnector connector : connectors) {
			ServiceportConnectorExt servicePortConnector = new ServiceportConnectorExt();

			servicePortConnector.setConnectorId(connector.getId());

			String name = connector.getName();
			if (StringUtils.isEmpty(name)) {
				name = connector.getId();
			}
			servicePortConnector.setName(name);

			addSourceTargetDataPort(servicePortConnector, connector);

			servicePortConnectors.add(servicePortConnector);
		}

		List<IDependency> assemblyConnectors = finder
				.findAssemblyConnector((IInternalBlockDiagram) currentDiagram);
		for (IDependency assemblyConnector : assemblyConnectors) {
			ServiceportConnectorExt servicePortConnector = new ServiceportConnectorExt();

			servicePortConnector.setConnectorId(assemblyConnector.getId());
			servicePortConnector.setName(assemblyConnector.getId());

			addSourceTargetDataPort(servicePortConnector, assemblyConnector);

			servicePortConnectors.add(servicePortConnector);
		}
		return servicePortConnectors;
	}

	private void addSourceTargetDataPort(ServiceportConnectorExt servicePortConnector,
			IDependency assemblyConnector) {

		try {
			ILinkPresentation lp = (ILinkPresentation) assemblyConnector.getPresentations()[0];

			INodePresentation se = (INodePresentation) lp.getSourceEnd();
			INodePresentation te = (INodePresentation) lp.getTargetEnd();

			ILinkPresentation usagep = getUsagePresentation(se.getLinks());
			if (usagep == null)
				usagep = getUsagePresentation(te.getLinks());

			ILinkPresentation realizationp = getRealizationPresentation(se.getLinks());
			if (realizationp == null)
				realizationp = getRealizationPresentation(te.getLinks());

			IPort usagePort = null;
			IAttribute usagePart = null;

			IElement model = usagep.getSourceEnd().getModel();
			if (model != null && model instanceof IPort) {
				usagePort = (IPort) model;
				INodePresentation usagePortP = (INodePresentation) usagep.getSourceEnd();
				usagePart = (IAttribute) usagePortP.getParent().getModel();
			}

			if (usagePort == null) {
				model = usagep.getTargetEnd().getModel();

				if (model != null && model instanceof IPort) {
					usagePort = (IPort) model;
					INodePresentation usagePortP = (INodePresentation) usagep.getTargetEnd();
					usagePart = (IAttribute) usagePortP.getParent().getModel();
				}
			}

			IPort realizationPort = null;
			IAttribute realizationPart = null;

			model = realizationp.getSourceEnd().getModel();
			if (model != null && model instanceof IPort) {
				realizationPort = (IPort) model;
				INodePresentation portP = (INodePresentation) realizationp.getSourceEnd();
				realizationPart = (IAttribute) portP.getParent().getModel();
			}

			if (realizationPort == null) {
				model = realizationp.getTargetEnd().getModel();

				if (model != null && model instanceof IPort) {
					realizationPort = (IPort) model;
					INodePresentation portP = (INodePresentation) realizationp.getTargetEnd();
					realizationPart = (IAttribute) portP.getParent().getModel();
				}
			}

			servicePortConnector.setSourceServicePort(buildTargetPort(usagePort, usagePart));
			servicePortConnector.setTargetServicePort(buildTargetPort(realizationPort, realizationPart));
			
		} catch (InvalidUsingException e) {
			throw new ApplicationException(e);
		}

	}

	private ILinkPresentation getRealizationPresentation(ILinkPresentation[] links) {
		for (ILinkPresentation link : links) {
			if (link.getType().equals("Realization"))
				return link;
		}
		return null;
	}

	private ILinkPresentation getUsagePresentation(ILinkPresentation[] links) {
		for (ILinkPresentation link : links) {
			if (link.getType().equals("Usage"))
				return link;
		}
		return null;
	}

	private void addSourceTargetDataPort(ServiceportConnectorExt dataportConnector,
			IConnector connector) {
		IPort[] ports = connector.getPorts();
		IPort srcPort = ports[0];
		IPort targetPort = ports[1];

		IAttribute[] parts = connector.getPartsWithPort();
		IAttribute srcPart = parts[0];
		IAttribute targetPart = parts[1];

		dataportConnector.setSourceServicePort(buildTargetPort(srcPort, srcPart));
		dataportConnector.setTargetServicePort(buildTargetPort(targetPort, targetPart));
	}

	private TargetPort buildTargetPort(IPort port, IAttribute part) {
		TargetPort targetPort = new TargetPort();
		targetPort.setPortName(port.getName());

		targetPort.setComponentId(String.format("RTC:Vender:%s:1.0",
				part.getType().getFullName(Constants.MODEL_NAMESPACE_SEPARATOR)));

		String name = part.getName();
		if (StringUtils.isEmpty(name)) {
			name = part.getType().getName()
					+ Constants.INITIAL_INSTANCE_NUMBER;
		}
		targetPort.setInstanceName(name);
		return targetPort;
	}

}
