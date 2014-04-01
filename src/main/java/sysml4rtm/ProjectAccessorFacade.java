package sysml4rtm;

import sysml4rtm.constants.Constants;
import sysml4rtm.exception.ApplicationException;
import sysml4rtm.exception.AstahApiException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class ProjectAccessorFacade {

	public static void openProject(String pathToModelFile) {
		try {
			getProjectAccessor().open(pathToModelFile, true, false, true);
		} catch (Exception e) {
			throw new AstahApiException(e);
		}
	}

	public static ProjectAccessor getProjectAccessor() {
		try {
			return AstahAPI.getAstahAPI().getProjectAccessor();
		} catch (ClassNotFoundException e) {
			throw new AstahApiException(e);
		}
	}

	public static INamedElement findElement(final String fullName) {
		try {
			INamedElement[] results = getProjectAccessor().findElements(new ModelFinder() {

				@Override
				public boolean isTarget(INamedElement elem) {
					return elem.getFullName("::").equals(fullName);
				}
			});

			return results.length > 0 ? results[0] : null;
		} catch (Exception e) {
			throw new AstahApiException(e);
		}
	}

	public static IBlock findBlock(final String fullName) {
		try {
			INamedElement[] elems = getProjectAccessor().findElements(new ModelFinder() {

				@Override
				public boolean isTarget(INamedElement elem) {
					if (elem instanceof IBlock) {
						return elem.getFullName("::").equals(fullName);
					}
					return false;
				}
			});

			if (elems == null || elems.length == 0) {
				return null;
			}
			if (elems.length > 1)
				throw new ApplicationException(
						String.format("%s found multiply elements", fullName));
			return (IBlock) elems[0];
		} catch (Exception e) {
			throw new AstahApiException(e);
		}
	}

	public static INamedElement[] findBlocksWithRTCStereotype() {
		try {
			return getProjectAccessor().findElements(new ModelFinder() {

				@Override
				public boolean isTarget(INamedElement element) {
					if (!(element instanceof IBlock))
						return false;
					return element.hasStereotype(Constants.STEREOTYPE_RTC);
				}
			});
		} catch (Exception e) {
			throw new AstahApiException(e);
		}
	}
}
