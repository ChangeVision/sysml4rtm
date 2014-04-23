package sysml4rtm.finder;

import java.util.ArrayList;
import java.util.List;

import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class ExportedPartTargetFinder {

	private IInternalBlockDiagram diagram;

	public List<IAttribute> find(IInternalBlockDiagram target) {
		try {
			this.diagram = target;
			List<IAttribute> result = new ArrayList<IAttribute>();
			
			if(diagram == null || diagram.getStructuredBlockPresentation() == null || diagram.getStructuredBlockPresentation().getModel() == null)
				return result;
			
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
		if (ModelUtils.isOnCurrentDiagram(diagram,elem) && ModelUtils.isMarkedExport(elem)) {
			return true;
		}
		return false;
	}

}
