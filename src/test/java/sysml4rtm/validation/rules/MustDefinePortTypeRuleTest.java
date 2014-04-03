package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class MustDefinePortTypeRuleTest {

	@Test
	public void FlowPropertyもItemFlowも存在しない場合_型が特定できないエラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block1");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_type_not_define", ":Block1", "notdefine")));
	}

	@Test
	public void ポート型は定義されているがFlowPropertyもItemFlowも存在しない場合_型が特定できないエラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block2");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_type_not_define", ":Block2", "notdefine:ブロック2")));
	}
	
	@Test
	public void ItemFlowが存在するがConveyもItemPropertyのTypeも存在しない場合_型が特定できないエラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block3");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_type_not_define", ":Block3", ":ブロック3")));
	}
	
	@Test
	public void Converyの型が揃っていない場合_型が揃っていないエラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute part = AstahModelFinder.findPart(":BlockBF");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.type_must_same", ":BlockBF", "")));
	}
	
	@Test
	public void ConveryとItemPropertyの型が揃っていない場合_型が揃っていないエラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute part = AstahModelFinder.findPart(":BlockCF");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.type_must_same", ":BlockCF", "")));
	}
	
	@Test
	public void FlowPropertyの型が揃っていない場合_型が揃っていないエラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute part = AstahModelFinder.findPart(":BlockDF");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.type_must_same", ":BlockDF", ":ブロック0")));
	}
	
	@Test
	public void サービスポートは妥当性検証の対象外となること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("marshal_serviceports.asml"));
		IAttribute part = AstahModelFinder.findPart(":com::Block0");
		MustDefinePortTypeRule rule = new MustDefinePortTypeRule();
		assertThat(rule.validate(part),is(true));
	}
}
