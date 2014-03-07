package sysml4rtm.rtc.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;
import sysml4rtm.rtc.export.profilebuilder.RtcProfileBuilder;

import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;

@SuppressWarnings("restriction")
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

	public void marshal(String pathToModelFile, String pathToOutput) {
		ProjectAccessorFacade.openProject(pathToModelFile);

		INamedElement[] blocks = ProjectAccessorFacade.findBlocksWithRTCStereotype();

		for (INamedElement block : blocks) {
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
	}

	private void marshal(RtcProfile profile, Writer writer) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(RtcProfile.class);
		Marshaller marshaller = context.createMarshaller();

		marshaller.setProperty(Marshaller.JAXB_ENCODING, Constants.ENCODING);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper",
				new NamespacePrefixMapper() {
					@Override
					public String getPreferredPrefix(String namespaceUri, String suggestion,
							boolean requirePrefix) {
						if (namespaceUri.equals(Constants.NAMESPACE_RTC)) {
							return Constants.NAMESPACE_RTC_ABBREVIATION;
						} else if (namespaceUri.equals(Constants.NAMESPACE_RTCEXT)) {
							return Constants.NAMESPACE_RTCEXT_ABBREVIATION;
						} else if (namespaceUri.equals(Constants.NAMESPACE_RTCDOC)) {
							return Constants.NAMESPACE_RTCDOC_ABBREVIATION;
						}
						return suggestion;
					}
				});
		marshaller.marshal(new JAXBElement(new QName(Constants.NAMESPACE_RTC, "RtcProfile"),
				RtcProfile.class, profile), writer);
	}

}
