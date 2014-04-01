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
import org.openrtp.namespaces.rtc.RtcProfile;

import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.constants.Constants;
import sysml4rtm.exception.ApplicationException;
import sysml4rtm.exception.ValidationException;
import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;
import sysml4rtm.rtc.export.profilebuilder.RtcProfileBuilder;
import sysml4rtm.validation.ModelValidator;
import sysml4rtm.validation.ValidationError;

import com.change_vision.jude.api.inf.model.IBlock;
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

	private static File buildOutputFileName(String pathToOutput, INamedElement block) {
		return new File(pathToOutput, block.getFullName("_") + ".xml");
	}

	public void marshal(String pathToOutput) {
		INamedElement[] blocks = ProjectAccessorFacade.findBlocksWithRTCStereotype();

		marshal(blocks,pathToOutput);
	}
	
	public void marshal(INamedElement[] targets , String pathToOutput){
		validate();
		for (INamedElement block : targets) {
			marshalAsFile(pathToOutput, block);
		}
	}
	
	public void marshal(String pathToModelFile, String pathToOutput) {
		ProjectAccessorFacade.openProject(pathToModelFile);
		marshal(pathToOutput);
	}

	private void marshalAsFile(String pathToOutput, INamedElement block) {
		RtcProfile contents = profileBuilder.createRtcProfile((IBlock) block);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					buildOutputFileName(pathToOutput, block)), Constants.ENCODING));

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
	
	private void validate() {
		ModelValidator validator = new ModelValidator();
		List<ValidationError> errors = validator.validate();
		if(errors != null && errors.size() > 0){
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
