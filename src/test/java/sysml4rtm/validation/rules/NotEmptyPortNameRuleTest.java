package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class NotEmptyPortNameRuleTest {

	@Test
	public void サービスポートに名前が設定されていない場合はエラーとなること() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_serviceport.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block0");
		NotEmptyPortNameRule rule = new NotEmptyPortNameRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_name_is_empty", ":Block0")));
	}
	
	@Test
	public void データポートに名前が設定されていない場合はエラーとなること() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("error_serviceport.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block1");
		NotEmptyPortNameRule rule = new NotEmptyPortNameRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_name_is_empty", ":Block1")));
	}

}
