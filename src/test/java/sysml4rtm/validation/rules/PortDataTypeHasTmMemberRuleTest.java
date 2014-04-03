package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IAttribute;

public class PortDataTypeHasTmMemberRuleTest {

	@Test
	public void ポート型は名前がtm_型名がRTC_Timeのメンバーを持っていない場合エラーとなること_SysML組み込み型の場合() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute target = AstahModelFinder.findPart(":BlockEF");
		
		PortDataTypeHasTmMemberRule rule = new PortDataTypeHasTmMemberRule();
		assertThat(rule.validate(target),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_type_must_have_tm_member",":BlockEF",":ブロック1","SysML::Real")));
	}
	
	@Test
	public void ポート型は名前がtm_型名がRTC_Timeのメンバーを持っていない場合エラーとなること_IDL型の場合() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute target = AstahModelFinder.findPart(":BlockFF");
		
		PortDataTypeHasTmMemberRule rule = new PortDataTypeHasTmMemberRule();
		assertThat(rule.validate(target),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_type_must_have_tm_member",":BlockFF",":ブロック4","IDL::long")));
	}

	@Test
	public void ポート型は名前がtm_型名がRTC_Timeのメンバーを持っていない場合エラーとなること_ItemFlowConveyのIDL型の場合() throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream("must_define_port_type.asml"));
		IAttribute target = AstahModelFinder.findPart(":BlockGF");
		
		PortDataTypeHasTmMemberRule rule = new PortDataTypeHasTmMemberRule();
		assertThat(rule.validate(target),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.port_type_must_have_tm_member",":BlockGF","","IDL::char")));
	}
	
	@Test
	public void サービスポートは妥当性検証の対象外となること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("marshal_serviceports.asml"));
		IAttribute part = AstahModelFinder.findPart(":com::Block0");
		PortDataTypeHasTmMemberRule rule = new PortDataTypeHasTmMemberRule();
		assertThat(rule.validate(part),is(true));
	}
}
