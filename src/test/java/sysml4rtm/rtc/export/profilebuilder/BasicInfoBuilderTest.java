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

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IAttribute;

public class BasicInfoBuilderTest {

	@Test
	public void 固定プロパティが正しく生成されること() throws Exception{
		BasicInfo basicinfo = findTestTarget("marshal_basic.asml", ":Block0");
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
	public void basicinfo_nameに_ブロック名が設定されること() throws Exception{
		BasicInfo basicinfo = findTestTarget("marshal_basic.asml", ":Block0");
		assertThat(basicinfo.getName(), is("Block0"));
	}

	@Test
	public void basicinfo_nameに_名前空間をもつブロックから_名前空間を除くブロック名が設定されること() throws Exception {
		BasicInfo basicinfo = findTestTarget("marshal_basic.asml", "part0:com::changevision::sample::Block1");
		assertThat(basicinfo.getName(), is("Block1"));
	}

	@Test
	public void basicinfo_descriptionには_ブロックの定義が設定されること() throws Exception {
		BasicInfo basicinfo = findTestTarget("marshal_basic.asml", ":Block0");
		assertThat(basicinfo.getDescription(), is("This is description.\nNext line."));
	}

	@Test
	public void basicinfo_descriptionに_日本語を含むブロックの定義が設定されること() throws Exception {
		BasicInfo basicinfo = findTestTarget("marshal_basic_japanese.asml", ":Block0");
		assertThat(basicinfo.getDescription(), is("日本語です。\n次の行です。"));
	}

	@Test
	public void creationDateとupdateDateが設定されること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("marshal_basic.asml"));
		IAttribute part = AstahModelFinder.findPart(":Block0");

		final Date now = new Date();
		BasicInfoBuilder builder = new BasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return now;
			}
		};
		BasicInfo basicinfo = builder.build(part);
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

	private BasicInfo findTestTarget(String pathToModelFile, String partFullName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IAttribute part = AstahModelFinder.findPart(partFullName);

		BasicInfoBuilder builder = new BasicInfoBuilder();
		BasicInfo basicinfo = builder.build(part);
		return basicinfo;
	}

}
