package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class DefineValidIbdDiagramNameRuleTest {

	@Test
	public void isValidDiagramName() {
		DefineValidIbdDiagramNameRule rule = new DefineValidIbdDiagramNameRule();
		
		assertThat(rule.isValidDiagramName("あいうえお"),is(false));
		assertThat(rule.isValidDiagramName("test-abd"),is(false));
		assertThat(rule.isValidDiagramName("test.adb"),is(false));
		assertThat(rule.isValidDiagramName("test_adb"),is(true));
	}

	@Test
	public void 内部ブロック図名に妥当性がない場合エラーが発生すること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("invalid_diagram_name.asml"));
		IInternalBlockDiagram diagram = AstahModelFinder.findIbdDiagram("内部ブロック図0");
		DefineValidIbdDiagramNameRule rule = new DefineValidIbdDiagramNameRule();
		assertThat(rule.validate(diagram),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.invalid_diagram_name")));
	}
	
}
