package sysml4rtm.rts.export.profilebuilder;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.openrtp.namespaces.rts_ext.RtsProfileExt;

import sysml4rtm.exceptions.ApplicationException;

import com.change_vision.jude.api.inf.model.IDiagram;

public class RtsProfileBasicInfoBuilder {

	public RtsProfileExt build(IDiagram currentDiagram) {
		RtsProfileExt profile = new RtsProfileExt();
		profile.setId(String.format("RTSystem:Vender.%s:1.0",currentDiagram.getName()));
		profile.setAbstract(currentDiagram.getDefinition());
		profile.setVersion("0.2");
		
		setDate(profile);
		
		return profile;
	}

	protected Date getNow() {
		return new Date();
	}
	
	private void setDate(RtsProfileExt rtsProfile) {
		try {
			DatatypeFactory factory = DatatypeFactory.newInstance();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(getNow());
			XMLGregorianCalendar now = factory.newXMLGregorianCalendar(cal);
			rtsProfile.setCreationDate(now);
			rtsProfile.setUpdateDate(now);
		} catch (DatatypeConfigurationException e) {
			throw new ApplicationException(e);
		}
	}
}
