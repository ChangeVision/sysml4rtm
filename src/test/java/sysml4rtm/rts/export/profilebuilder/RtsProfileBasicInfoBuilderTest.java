package sysml4rtm.rts.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.openrtp.namespaces.rts_ext.RtsProfileExt;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class RtsProfileBasicInfoBuilderTest {

	@Test
	public void 固定プロパティが正しく生成されること() throws Exception{
		RtsProfileExt basicinfo = findTestTarget("basic.asml", "target");
		assertThat(basicinfo.getVersion(), is("0.2"));
	}

	@Test
	public void idに_内部ブロック図名が設定されること() throws Exception{
		RtsProfileExt basicinfo = findTestTarget("basic.asml", "target");
		assertThat(basicinfo.getId(), is("RTSystem:Vender.target:1.0"));
	}
	
	@Test
	public void descriptionには_図の定義が設定されること() throws Exception {
		RtsProfileExt basicinfo = findTestTarget("basic.asml", "target");
		assertThat(basicinfo.getAbstract(), is("This is description.\nNext line"));
	}
	
	@Test
	public void creationDateとupdateDateが設定されること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("basic.asml"));
		IInternalBlockDiagram ibd = AstahModelFinder.findIbdDiagram("target");

		final Date now = new Date();
		RtsProfileBasicInfoBuilder builder = new RtsProfileBasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return now;
			}
		};
		RtsProfileExt basicinfo = builder.build(ibd);
		XMLGregorianCalendar cal = createCalender(now);

		assertThat(basicinfo.getCreationDate(), is(cal));
		assertThat(basicinfo.getUpdateDate(), is(cal));
	}

	private XMLGregorianCalendar createCalender(Date date) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		DatatypeFactory factory = null;
		try {
			factory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			fail();
		}
		XMLGregorianCalendar cal = factory.newXMLGregorianCalendar(gc);
		return cal;
	}
	
	private RtsProfileExt findTestTarget(String pathToModelFile, String diagramFullname) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IInternalBlockDiagram ibd = AstahModelFinder.findIbdDiagram(diagramFullname);

		RtsProfileBasicInfoBuilder builder = new RtsProfileBasicInfoBuilder();
		return builder.build(ibd);
	}
}
