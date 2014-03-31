package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class ConnectorMustAttachPortRuleTest {

	@Test
	public void コネクタの片方がポートに接続されていない場合エラーとなること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("connector_attach_port.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block7F");
		ConnectorMustAttachPortRule rule = new ConnectorMustAttachPortRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.connector_must_attach_port", ":Block7F")));
	}
	
	@Test
	public void コネクタの両方がポートに接続されていない場合エラーとなること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("connector_attach_port.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block8F");
		ConnectorMustAttachPortRule rule = new ConnectorMustAttachPortRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.connector_must_attach_port", ":Block8F")));
	}
	
	@Test
	public void ItemFlowの片方がポートに接続されていない場合エラーとなること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("connector_attach_port.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block10F");
		ConnectorMustAttachPortRule rule = new ConnectorMustAttachPortRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.connector_must_attach_port", ":Block10F")));
	}
	
	@Test
	public void ItemFlowの両方がポートに接続されていない場合エラーとなること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("connector_attach_port.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block9F");
		ConnectorMustAttachPortRule rule = new ConnectorMustAttachPortRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.connector_must_attach_port", ":Block9F")));
	}

}
