package sysml4rtm.finder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ModelFinder;

public class ExportedConnectorTargetFinder {

	public List<IConnector> findDataPortConnector(final IInternalBlockDiagram diagram){
		return findMatchedConnector(diagram, new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				if(object instanceof IPort){
					return !ModelUtils.hasServiceInterface((IPort)object);
				}
				return false;
			}
		});
	}
	
	public List<IConnector> findServicePortConnector(final IInternalBlockDiagram diagram){
		return findMatchedConnector(diagram, new Predicate() {
			
			@Override
			public boolean evaluate(Object object) {
				if(object instanceof IPort){
					return ModelUtils.hasServiceInterface((IPort)object);
				}
				return false;
			}
		});
	}

	private List<IConnector> findMatchedConnector(final IInternalBlockDiagram diagram,Predicate predicate){
		
		List<IConnector> connectors = findConnector(diagram);
		List<IConnector> matched = new ArrayList<IConnector>();
		for(IConnector connector : connectors){
			IPort[] ports = connector.getPorts();
			if(predicate.evaluate(ports[0])){
				matched.add(connector);
			}
		}
		
		return matched;
	}
	
	public List<IConnector> findConnector(final IInternalBlockDiagram diagram) {
		try{
			INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor().findElements(new ModelFinder() {
				
				@Override
				public boolean isTarget(INamedElement elem) {
					if(!(isConnector(elem)))
						return false;

					if(ModelUtils.isOnCurrentDiagram(diagram, elem)){
						IConnector connector = (IConnector)elem;
						IAttribute[] parts = connector.getPartsWithPort();
						if(ModelUtils.isMarkedExport(parts[0]) && ModelUtils.isMarkedExport(parts[1]))
							return true;
					}
					return false;
				}
			});
			
			List<IConnector> result = new ArrayList<IConnector>();
			for(INamedElement elem : elements){
				result.add((IConnector) elem);
			}

			return result;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		
	}
	
	public List<IDependency> findAssemblyConnector(final IInternalBlockDiagram diagram){
		try{
		
			INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor().findElements(new ModelFinder() {
				
				@Override
				public boolean isTarget(INamedElement elem) {
					if(!isDependency(elem) || !ModelUtils.isOnCurrentDiagram(diagram, elem))
						return false;
					
					String tv = elem.getTaggedValue("hiddenDependency");
					return StringUtils.equals(tv,"true");
				}
			});
			
			List<IDependency> result = new ArrayList<IDependency>();
			for(INamedElement elem : elements){
				result.add((IDependency) elem);
			}
			return result;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private void test(final IInternalBlockDiagram diagram) throws InvalidUsingException{
		List<IDependency> elements = findAssemblyConnector(diagram);
		for(INamedElement elem : elements){
			IDependency dep = (IDependency) elem;
			ILinkPresentation lp = (ILinkPresentation) dep.getPresentations()[0];
			
			INodePresentation se = (INodePresentation) lp.getSourceEnd();
			INodePresentation te = (INodePresentation) lp.getTargetEnd();
			
			ILinkPresentation usagep = getUsagePresentation(se.getLinks());
			if(usagep == null)
				usagep = getUsagePresentation(te.getLinks());
			
			ILinkPresentation realizationp = getRealizationPresentation(se.getLinks());
			if(realizationp ==null)
				realizationp = getRealizationPresentation(te.getLinks());
			
			
			IPort usagePort = null;
			IAttribute usagePart = null;
			
			IElement model = usagep.getSourceEnd().getModel();
			if(model != null && model instanceof IPort){
				usagePort = (IPort) model;
				INodePresentation usagePortP = (INodePresentation) usagep.getSourceEnd();
				usagePart = (IAttribute) usagePortP.getParent().getModel();
			}
			
			if(usagePort == null){
				model = usagep.getTargetEnd().getModel();
				
				if(model != null && model instanceof IPort){
					usagePort = (IPort) model;
					INodePresentation usagePortP = (INodePresentation) usagep.getTargetEnd();
					usagePart = (IAttribute) usagePortP.getParent().getModel();
				}
			}
				
			IPort realizationPort = null;
			IAttribute realizationPart = null;

			model = realizationp.getSourceEnd().getModel();
			if(model != null && model instanceof IPort){
				realizationPort = (IPort) model;
				INodePresentation portP = (INodePresentation) realizationp.getSourceEnd();
				realizationPart = (IAttribute) portP.getParent().getModel();
			}
			
			if(realizationPort == null){
				model = realizationp.getTargetEnd().getModel();
				
				if(model != null && model instanceof IPort){
					realizationPort = (IPort) model;
					INodePresentation portP = (INodePresentation) realizationp.getTargetEnd();
					realizationPart = (IAttribute) portP.getParent().getModel();
				}
			}
			
			System.out.println(usagePart.getName() +":" + usagePort.getName());
			System.out.println(realizationPart.getName() +":" + realizationPort.getName());
		}
		
	}
	private IPort getPort(ILinkPresentation linkp) {
		IElement model = linkp.getSourceEnd().getModel();
		if(model != null && model instanceof IPort)
			return (IPort) model;
		
		model = linkp.getTargetEnd().getModel();
		if(model != null && model instanceof IPort)
			return (IPort) model;
		
		return null;
	}

	private ILinkPresentation getRealizationPresentation(ILinkPresentation[] links) {
		for(ILinkPresentation link : links){
			if(link.getType().equals("Realization"))
				return link;
		}
		return null;
	}

	private ILinkPresentation getUsagePresentation(ILinkPresentation[] links) {
		for(ILinkPresentation link : links){
			if(link.getType().equals("Usage"))
				return link;
		}
		return null;
	}

	private boolean isDependency(INamedElement elem){
		return elem instanceof IDependency;
	}
	
	private boolean isConnector(INamedElement elem){
		return elem instanceof IConnector;
	}
}
