package sysml4rtm;

import sysml4rtm.exception.ApplicationException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class ProjectAccessorFacade {

	public static ProjectAccessor getProjectAccessor() {
		try {
			return AstahAPI.getAstahAPI().getProjectAccessor();
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
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
			throw new ApplicationException(e);
		}
	}

}
