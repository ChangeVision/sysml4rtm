package sysml4rtm.rts.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.openrtp.namespaces.rts.RtsProfile;
import org.openrtp.namespaces.rts_ext.RtsProfileExt;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.exceptions.UnSupportDiagramException;
import sysml4rtm.exceptions.ValidationException;
import sysml4rtm.rts.export.profilebuilder.RtsProfileBasicInfoBuilder;
import sysml4rtm.rts.export.profilebuilder.RtsProfileBuilder;
import sysml4rtm.validation.ModelValidator;
import sysml4rtm.validation.ValidationError;

import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;


public class RtsMarshaller {

	private IDiagram currentDiagram;
	private RtsProfileBasicInfoBuilder basicInfoBuilder;

	public RtsMarshaller() {
		basicInfoBuilder = new RtsProfileBasicInfoBuilder();
	}


	public void setBasicInfoBuilder(RtsProfileBasicInfoBuilder basicInfoBuilder) {
		this.basicInfoBuilder = basicInfoBuilder;
	}
	
	public void marshal(IDiagram currentDiagram, String pathToOutputFolder) {
		checkSupportDiagram(currentDiagram);
		this.currentDiagram = currentDiagram;
		validate();

		RtsProfileBuilder builder = new RtsProfileBuilder();
		builder.setBasicInfoBuilder(basicInfoBuilder);
		
		RtsProfileExt profile = builder.createRtsProfile(currentDiagram);
		
		marshallAsFile(pathToOutputFolder, profile);
	}

	private void validate() {
		List<INamedElement> targets = new ArrayList<INamedElement>();
		targets.add(currentDiagram);

		ModelValidator validator = new ModelValidator();
		List<ValidationError> errors = validator.validate(targets);
		if (errors != null && errors.size() > 0) {
			throw new ValidationException(errors);
		}
	}
	
	private void marshallAsFile(String pathToOutputFolder,
			RtsProfileExt profile) {
		BufferedWriter writer = null;
		try {
			createOutputFolder(pathToOutputFolder);
			
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					getOutputRtsFileName(pathToOutputFolder)), Constants.ENCODING));
				
			marshal(profile, writer);

			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}


	private void createOutputFolder(String pathToOutputFolder) {
		File parent =  new File(pathToOutputFolder);
		if(!parent.exists())
			parent.mkdirs();
	}
	
	private String getOutputRtsFileName(String pathToOutputFolder) {
		return pathToOutputFolder + SystemUtils.FILE_SEPARATOR + currentDiagram.getName() + ".xml";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void marshal(RtsProfile profile, Writer writer) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(RtsProfile.class);
		Marshaller marshaller = context.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_ENCODING, Constants.ENCODING);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
		marshaller.marshal(new JAXBElement(new QName(Constants.NAMESPACE_RTS, "RtsProfile"),
				RtsProfile.class, profile), writer);
	}

	private void checkSupportDiagram(IDiagram diagram) {
		if (!(diagram instanceof IInternalBlockDiagram)) {
			throw new UnSupportDiagramException(Messages
					.getMessage("error.notsupport_diagram"));
		}
	}

}
