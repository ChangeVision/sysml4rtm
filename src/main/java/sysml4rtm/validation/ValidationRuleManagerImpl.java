package sysml4rtm.validation;

import java.util.ArrayList;
import java.util.List;

import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.finder.ExportedPartTargetFinder;
import sysml4rtm.validation.rules.ConnectorMustAttachPortRule;
import sysml4rtm.validation.rules.DefineDirectionRule;
import sysml4rtm.validation.rules.DefineValidIbdDiagramNameRule;
import sysml4rtm.validation.rules.MustDefinePortTypeRule;
import sysml4rtm.validation.rules.NotEmptyPortNameRule;
import sysml4rtm.validation.rules.NotExistInOutFlowPropertyRule;
import sysml4rtm.validation.rules.NotExistInOutItemFlowPropertyRule;
import sysml4rtm.validation.rules.NotExistInconsitencyItemFlowAndFlowPropertyRelationRule;
import sysml4rtm.validation.rules.PartMustHaveTypeRule;
import sysml4rtm.validation.rules.PortDataTypeHasTmMemberRule;
import validation.DefaultValidationRuleManager;
import validation.ValidationRule;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class ValidationRuleManagerImpl extends DefaultValidationRuleManager {

	@Override
	public List<ValidationRule> getValidationRule() {
		List<ValidationRule> rules = new ArrayList<ValidationRule>();
		rules.add(new ConnectorMustAttachPortRule());
		rules.add(new DefineDirectionRule());
		rules.add(new MustDefinePortTypeRule());
		rules.add(new NotEmptyPortNameRule());
		rules.add(new NotExistInconsitencyItemFlowAndFlowPropertyRelationRule());
		rules.add(new NotExistInOutFlowPropertyRule());
		rules.add(new NotExistInOutItemFlowPropertyRule());
		rules.add(new PartMustHaveTypeRule());
		rules.add(new PortDataTypeHasTmMemberRule());
		rules.add(new DefineValidIbdDiagramNameRule());
		return rules;
	}

	@Override
	public List<INamedElement> getTargetModels() {
		List<INamedElement> targets = new ArrayList<INamedElement>();
		try {
			IDiagram currentDiagram = AstahAPI.getAstahAPI().getProjectAccessor().getViewManager()
					.getDiagramViewManager().getCurrentDiagram();

			if(!(currentDiagram instanceof IInternalBlockDiagram))
				return  targets;
				
			targets.add(currentDiagram);

			targets.addAll(new ExportedPartTargetFinder()
					.find((IInternalBlockDiagram) currentDiagram));

		} catch (InvalidUsingException e) {
			throw new ApplicationException(e);
		} catch (ClassNotFoundException e) {
			throw new ApplicationException(e);
		}
		return targets;
	}
	
}
