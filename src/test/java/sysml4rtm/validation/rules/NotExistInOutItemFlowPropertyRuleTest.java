package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class NotExistInOutItemFlowPropertyRuleTest {

	@Test
	public void アイテムフローの向きがINOUTである場合エラーとなること() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_port_itemflow_inout.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block0");
		NotExistInOutItemFlowPropertyRule rule = new NotExistInOutItemFlowPropertyRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.inout_itemproperty_not_support", ":Block0", "block0port")));
	}

}
