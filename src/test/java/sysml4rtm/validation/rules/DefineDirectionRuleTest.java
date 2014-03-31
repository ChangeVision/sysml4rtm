package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class DefineDirectionRuleTest {

	@Test
	public void should_error_occur_when_port_direction_doesnot_define() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_port_direction_not_define.asml"));
		IAttribute part = AstahModelFinder.findPart("part1:ErrorBlock3");
		DefineDirectionRule rule = new DefineDirectionRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.direction_not_define", "part1:ErrorBlock3", "3")));
		
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_port_direction_not_define.asml"));
		part = AstahModelFinder.findPart(":ErrorBlock4");
		rule = new DefineDirectionRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.direction_not_define", ":ErrorBlock4", ":ErrorBlockPort1")));

	}

}
