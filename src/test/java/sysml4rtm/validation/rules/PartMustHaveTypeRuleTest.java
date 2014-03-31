package sysml4rtm.validation.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.Test;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.Messages;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IAttribute;

public class PartMustHaveTypeRuleTest {

    @AfterClass
    public static void tearDown() throws Exception {
    	AstahAPI.getAstahAPI().getProjectAccessor().close();
    }
    
	@Test
	public void 型が指定されていないunknowTypeなパートを指定した場合はエラーとなること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("unknowtype.asml"));
		IAttribute target = AstahModelFinder.findPart("unknown:");
		
		PartMustHaveTypeRule rule = new PartMustHaveTypeRule();
		assertThat(rule.validate(target),is(false));
		assertThat(rule.getResults().size(),is(1));
		assertThat(rule.getResults().get(0).getMessage(),is(Messages.getMessage("error.include_unknowntype")));
	}
}
