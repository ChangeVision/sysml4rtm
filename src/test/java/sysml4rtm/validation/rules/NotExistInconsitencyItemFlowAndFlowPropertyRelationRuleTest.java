package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class NotExistInconsitencyItemFlowAndFlowPropertyRelationRuleTest {

	@Test
	public void パートのポートのFlowPropertyとItemFlowの方向が違う場合はエラーとなること_IN() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_flow_property_part.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block3");
		NotExistInconsitencyItemFlowAndFlowPropertyRelationRule rule = new NotExistInconsitencyItemFlowAndFlowPropertyRelationRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.Inconsistency_flowproperty_itemflow", ":Block3", ":Port3A")));
	}

	@Test
	public void パートのポートのFlowPropertyとItemFlowの方向が違う場合はエラーとなること_OUT() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_flow_property_part.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block4");
		NotExistInconsitencyItemFlowAndFlowPropertyRelationRule rule = new NotExistInconsitencyItemFlowAndFlowPropertyRelationRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.Inconsistency_flowproperty_itemflow", ":Block4", ":Block4Port")));
	}
	
	@Test
	public void サービスポートは妥当性検証の対象外となること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("marshal_serviceports.asml"));
		IAttribute part = AstahModelFinder.findPart(":com::Block0");
		NotExistInconsitencyItemFlowAndFlowPropertyRelationRule rule = new NotExistInconsitencyItemFlowAndFlowPropertyRelationRule();
		assertThat(rule.validate(part),is(true));
	}
}
