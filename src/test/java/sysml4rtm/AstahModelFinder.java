package sysml4rtm;

import java.io.InputStream;

import junit.framework.AssertionFailedError;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;

public class AstahModelFinder {

	public static IAttribute findPart(final String target) throws Exception {
		INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor()
				.findElements(new ModelFinder() {

					@Override
					public boolean isTarget(INamedElement elem) {
						if (elem instanceof IAttribute) {
							IAttribute attr = (IAttribute) elem;
							try {
								if (attr.getPresentations().length == 0)
									return false;
							} catch (InvalidUsingException e) {
							}
							String typeName = "";
							if (attr.getType() != null) {
								typeName = attr.getType().getFullName("::");
							}
							String expression = attr.getName() + ":" + typeName;
							if (expression.equals(target))
								return true;
						}
						return false;
					}
				});

		if (elements.length == 0)
			throw new AssertionFailedError(String.format("missing %s", target));

		return (IAttribute) elements[0];
	}

	public static IInternalBlockDiagram findIbdDiagram(final String target) throws Exception {
		INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor()
				.findElements(IInternalBlockDiagram.class, target);

		if (elements.length == 0)
			throw new AssertionFailedError(String.format("missing %s", target));

		return (IInternalBlockDiagram) elements[0];
	}

	public static void open(InputStream stream) throws Exception {
		if(stream == null)
			throw new AssertionFailedError("project not found.");
		
		AstahAPI.getAstahAPI().getProjectAccessor().open(stream);
	}

	public static IBlock findBlock(String target) throws Exception {
		INamedElement[] elements = AstahAPI.getAstahAPI().getProjectAccessor()
				.findElements(IBlock.class, target);

		if (elements.length == 0)
			throw new AssertionFailedError(String.format("missing %s", target));

		return (IBlock) elements[0];
	}
}
