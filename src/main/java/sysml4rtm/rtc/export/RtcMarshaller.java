package sysml4rtm.rtc.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.openrtp.namespaces.rtc.RtcProfile;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.exceptions.ApplicationException;
import sysml4rtm.exceptions.UnSupportDiagramException;
import sysml4rtm.exceptions.ValidationException;
import sysml4rtm.finder.InternalBlockDiagramExportedTargetFinder;
import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;
import sysml4rtm.rtc.export.profilebuilder.RtcProfileBuilder;
import sysml4rtm.validation.ModelValidator;
import sysml4rtm.validation.ValidationError;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class RtcMarshaller {

	private RtcProfileBuilder profileBuilder = null;

	public RtcMarshaller() {
		profileBuilder = new RtcProfileBuilder();
	}

	// injection setter
	public void setBasicInfoBuilder(BasicInfoBuilder basicInfoBuilder) {
		profileBuilder.setBasicInfoBuilder(basicInfoBuilder);
	}

	private static File buildOutputFileName(String pathToOutputFolder, IAttribute part) {
		String pathToParentFolder = pathToOutputFolder + SystemUtils.FILE_SEPARATOR + part.getType().getFullNamespace("/");
		File parent =  new File(pathToParentFolder);
		if(!parent.exists())
			parent.mkdirs();
		
		return new File(parent,part.getType().getName() + ".xml");
	}

	public void marshal(IDiagram currentDiagram, String pathToOutputFolder) {
		checkSupportDiagram(currentDiagram);
		List<IAttribute> parts = getGeneratedTargetElements(currentDiagram);
		validate(parts);

		for (IAttribute part : parts) {
			marshalAsFile(pathToOutputFolder, part);
		}
	}

	private List<IAttribute> getGeneratedTargetElements(IDiagram diagram) {
		List<IAttribute> elements = new InternalBlockDiagramExportedTargetFinder()
				.find((IInternalBlockDiagram) diagram);
		return elements;
	}

	private void checkSupportDiagram(IDiagram diagram) {
		if (!(diagram instanceof IInternalBlockDiagram)) {
			throw new UnSupportDiagramException(Messages
					.getMessage("error.notsupport_diagram"));
		}
	}

	private void marshalAsFile(String pathToOutput, IAttribute part) {
		profileBuilder.setPathToOutputFolder(pathToOutput);
		RtcProfile contents = profileBuilder.createRtcProfile(part);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					buildOutputFileName(pathToOutput, part)), Constants.ENCODING));

			marshal(contents, writer);

			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			throw new ApplicationException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	private void validate(List<? extends INamedElement> parts) {
		ModelValidator validator = new ModelValidator();
		List<ValidationError> errors = validator.validate(parts);
		if (errors != null && errors.size() > 0) {
			throw new ValidationException(errors);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void marshal(RtcProfile profile, Writer writer) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(RtcProfile.class);
		Marshaller marshaller = context.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_ENCODING, Constants.ENCODING);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
		marshaller.marshal(new JAXBElement(new QName(Constants.NAMESPACE_RTC, "RtcProfile"),
				RtcProfile.class, profile), writer);
	}

}
