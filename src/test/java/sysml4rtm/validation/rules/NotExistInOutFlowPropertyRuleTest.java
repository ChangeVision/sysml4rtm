package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class NotExistInOutFlowPropertyRuleTest {

	
	@Test
	public void フロープロパティがINOUTの場合エラーとなること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_port_flowproperty_inout.asml"));
		IAttribute part = AstahModelFinder.findPart("part1:ErrorBlock1");
		NotExistInOutFlowPropertyRule rule = new NotExistInOutFlowPropertyRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.inout_flowproperty_not_support", "part1:ErrorBlock1", "1:PortB4A")));
		
	}
	
	@Test
	public void サービスポートは妥当性検証の対象外となること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("marshal_serviceports.asml"));
		IAttribute part = AstahModelFinder.findPart(":com::Block0");
		NotExistInOutFlowPropertyRule rule = new NotExistInOutFlowPropertyRule();
		assertThat(rule.validate(part),is(true));
	}

}
