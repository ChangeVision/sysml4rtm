package sysml4rtm.rtc.export.profilebuilder;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrtp.namespaces.rtc.BasicInfo;

import sysml4rtm.constants.Constants;
import sysml4rtm.exception.ApplicationException;

import com.change_vision.jude.api.inf.model.IBlock;

public class BasicInfoBuilder {

	public BasicInfoBuilder() {
	}

	public BasicInfo build(IBlock block) {
		BasicInfo basicinfo = new BasicInfo();

		basicinfo.setName(block.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR));

		basicinfo.setComponentType("STATIC");
		basicinfo.setActivityType("PERIODIC");
		basicinfo.setComponentKind(Constants.ComponentKind.DFC.toString());
		basicinfo.setCategory("Category");
		basicinfo.setExecutionRate(10.0d);
		basicinfo.setExecutionType("PeriodicExecutionContext");
		basicinfo.setMaxInstances(new BigInteger("0"));
		basicinfo.setVendor("Vendor");
		basicinfo.setVersion("1.0");

		basicinfo.setDescription(block.getDefinition());

		setDate(basicinfo);

		return basicinfo;
	}

	protected Date getNow() {
		return new Date();
	}

	private void setDate(BasicInfo basicinfo) {
		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(getNow());
			XMLGregorianCalendar now = factory.newXMLGregorianCalendar(cal);
			basicinfo.setCreationDate(now);
			basicinfo.setUpdateDate(now);
		} catch (DatatypeConfigurationException e) {
			throw new ApplicationException(e);
		}
	}

}
