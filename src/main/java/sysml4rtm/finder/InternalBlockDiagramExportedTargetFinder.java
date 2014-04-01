package sysml4rtm.finder;

import java.util.ArrayList;
import java.util.List;

import sysml4rtm.constants.Constants;
import sysml4rtm.exception.ApplicationException;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class InternalBlockDiagramExportedTargetFinder {

	private IInternalBlockDiagram diagram;

	public List<IAttribute> find(IInternalBlockDiagram target) {
		try {
			this.diagram = target;
			List<IAttribute> result = new ArrayList<IAttribute>();
			
			IBlock block = (IBlock) diagram.getStructuredBlockPresentation().getModel();
			collectParts(result , block);

			return result;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private void collectParts(List<IAttribute> container, IBlock block) {
		IAttribute[] parts = block.getParts();
		for(IAttribute part : parts){
			if(isTarget(part))
				container.add(part);
			
			IBlock partType = (IBlock) part.getType();
			if(partType.getAttributes().length > 0){
				collectParts(container, partType);
			}
		}
	}

	private boolean isTarget(INamedElement elem) {
		if (isOnCurrentDiagram(elem) && isMarkedExport(elem)) {
			return true;
		}
		return false;
	}
	
	private boolean isMarkedExport(INamedElement element) {
		if(ModelUtils.isPart(element)){
			IAttribute part = (IAttribute) element;
			if(part.hasStereotype(Constants.STEREOTYPE_RTC))
					return true;
			return part.getType() != null && part.getType().hasStereotype(Constants.STEREOTYPE_RTC);
		}
		return false;
	}

	private boolean isOnCurrentDiagram(INamedElement elem) {
		if (!hasPresentation(elem))
			return false;
		try {
			return elem.getPresentations()[0].getDiagram().getId().equals(diagram.getId());
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	private boolean hasPresentation(INamedElement elem) {
		try {
			return elem.getPresentations() != null && elem.getPresentations().length > 0;
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

}
