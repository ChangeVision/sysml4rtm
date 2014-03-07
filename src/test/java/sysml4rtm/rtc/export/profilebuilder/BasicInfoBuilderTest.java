package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.openrtp.namespaces.rtc.BasicInfo;

import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;

import com.change_vision.jude.api.inf.model.IBlock;

public class BasicInfoBuilderTest {

	@Test
	public void fixedProperty() {
		BasicInfo basicinfo = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "Block0");
		assertThat(basicinfo.getComponentType(), is("STATIC"));
		assertThat(basicinfo.getActivityType(), is("PERIODIC"));
		assertThat(basicinfo.getComponentKind(), is("DataFlowComponent"));
		assertThat(basicinfo.getCategory(), is("Category"));
		assertThat(basicinfo.getExecutionType(), is("PeriodicExecutionContext"));
		assertThat(basicinfo.getExecutionRate(), is(10.0d));
		assertThat(basicinfo.getMaxInstances(), is(new BigInteger("0")));
		assertThat(basicinfo.getVendor(), is("Vendor"));
		assertThat(basicinfo.getVersion(), is("1.0"));
	}

	@Test
	public void shouldExtract_name() {
		BasicInfo basicinfo = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "Block0");
		assertThat(basicinfo.getName(), is("Block0"));
	}

	@Test
	public void shouldExtract_name_with_namespace() throws Exception {
		BasicInfo basicinfo = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "com::changevision::sample::Block1");
		assertThat(basicinfo.getName(), is("com::changevision::sample::Block1"));
	}

	@Test
	public void shouldExtract_description() throws Exception {
		BasicInfo basicinfo = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "Block0");
		assertThat(basicinfo.getDescription(), is("This is description.\nNext line."));
	}

	@Test
	public void shouldExtract_descriptionUsingJapanese() throws Exception {
		BasicInfo basicinfo = findTestTarget(
				this.getClass().getResource("marshal_basic_japanese.asml").getPath(), "Block0");
		assertThat(basicinfo.getDescription(), is("日本語です。\n次の行です。"));
	}

	@Test
	public void shouldExtract_creationDate_and_updateDate() throws Exception {
		ProjectAccessorFacade.openProject(this.getClass().getResource("marshal_basic.asml")
				.getPath());
		IBlock block = ProjectAccessorFacade.findBlock("Block0");

		final Date now = new Date();
		BasicInfoBuilder builder = new BasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return now;
			}
		};
		BasicInfo basicinfo = builder.build(block);
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

	private BasicInfo findTestTarget(String pathToModelFile, String blockFullName) {
		ProjectAccessorFacade.openProject(pathToModelFile);
		IBlock block = ProjectAccessorFacade.findBlock(blockFullName);

		BasicInfoBuilder builder = new BasicInfoBuilder();
		BasicInfo basicinfo = builder.build(block);
		return basicinfo;
	}

}
