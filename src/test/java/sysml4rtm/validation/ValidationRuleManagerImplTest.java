package sysml4rtm.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

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

public class ValidationRuleManagerImplTest {

	@Test
	public void ルールが登録されていること() {
		ValidationRuleManagerImpl manager = new ValidationRuleManagerImpl();
		assertThat(manager.getValidationRule().size(),is(10));
		
		assertThat(manager.hasRule(ConnectorMustAttachPortRule.class),is(true));
		assertThat(manager.hasRule(DefineDirectionRule.class),is(true));
		assertThat(manager.hasRule(MustDefinePortTypeRule.class),is(true));		
		assertThat(manager.hasRule(NotEmptyPortNameRule.class),is(true));		
		assertThat(manager.hasRule(NotExistInconsitencyItemFlowAndFlowPropertyRelationRule.class),is(true));
		assertThat(manager.hasRule(NotExistInOutFlowPropertyRule.class),is(true));
		assertThat(manager.hasRule(NotExistInOutItemFlowPropertyRule.class),is(true));
		assertThat(manager.hasRule(PartMustHaveTypeRule.class),is(true));
		assertThat(manager.hasRule(PortDataTypeHasTmMemberRule.class),is(true));
		assertThat(manager.hasRule(DefineValidIbdDiagramNameRule.class),is(true));
	}

}
