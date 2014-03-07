package sysml4rtm.utils;

import org.apache.commons.lang3.StringUtils;

import com.change_vision.jude.api.inf.model.IElement;
import com.change_vision.jude.api.inf.model.INamedElement;

public class ModelUtils {

	public static boolean hasPackage(INamedElement element) {
		IElement owner = element.getOwner();
		boolean hasNamespace = false;
		if (owner != null && owner instanceof INamedElement && owner.getOwner() != null) {
			hasNamespace = true;
		}
		return hasNamespace;
	}

	public static String getPackage(INamedElement element) {
		return getPackage(element, "::");
	}

	public static String getPackage(INamedElement element, String pathSeparator) {
		StringBuffer sb = new StringBuffer();
		IElement owner = element.getOwner();
		if (owner != null && owner instanceof INamedElement) {
			IElement ownerOwner = owner.getOwner();
			while (owner != null && owner instanceof INamedElement && ownerOwner != null) {
				sb.insert(0, ((INamedElement) owner).getName() + pathSeparator);
				owner = ownerOwner;
				ownerOwner = ownerOwner.getOwner();
			}
		}
		return StringUtils.removeEnd(sb.toString(), pathSeparator);
	}

}
