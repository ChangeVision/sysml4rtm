package sysml4rtm;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.rtc.export.RtcMarshaller;
import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;
import sysml4rtm.rts.export.RtsMarshaller;
import sysml4rtm.rts.export.profilebuilder.RtsProfileBasicInfoBuilder;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class MarshallerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void accept_ConsoleIn_ConsoleOut() throws Exception {
		File output = marshall("consolein_out.asml", "target");

		assertThat(countOfXmlFiles(output), is(3));

		assertExist(output, "/ConsoleIn.xml");
		assertExist(output, "/ConsoleOut.xml");
		assertExist(output, "/target.xml");

	}

	private String getFilePath(File root, String file) {
		String path = null;
		if (file.startsWith("/"))
			path = file;
		else
			path = "/" + file;

		return root.getPath() + path;
	}

	private void assertRtsXmlEquals(String expectedPath, String actualPath) throws Exception {
		URL expectedResource = this.getClass().getResource(expectedPath);
		if (expectedResource == null)
			throw new AssertionFailedError("missing " + expectedPath);
		File expected = new File(expectedResource.getPath());

		File actual = new File(actualPath);

		assertXMLEqual(FileUtils.readFileToString(expected),
				replaceLocation(FileUtils.readFileToString(actual)));
	}

	private void assertRtcXmlEquals(String expectedPath, String actualPath) throws Exception {
		URL expectedResource = this.getClass().getResource(expectedPath);
		if (expectedResource == null)
			throw new AssertionFailedError("missing " + expectedPath);
		File expected = new File(expectedResource.getPath());

		File actual = new File(actualPath);
		
		assertXMLEqual(FileUtils.readFileToString(expected), FileUtils.readFileToString(actual));
	}

	private int countOfXmlFiles(File output) {
		return FileUtils.listFiles(output, new String[] { "xml" }, true).size();
	}

	private String replaceLocation(String contents) {
		contents = contents.replaceAll("x=\"-*[\\p{Digit}]*\"", "x=\"@loc\"");
		return contents.replaceAll("y=\"-*[\\p{Digit}]*\"", "y=\"@loc\"");
	}

	private void assertExist(File root, String file) {
		assertThat(new File(getFilePath(root, file)).exists(), is(true));
	}

	private File marshall(String pathToModelFile, String ibdDiagramName) throws Exception {
		InputStream resourceAsStream = this.getClass().getResourceAsStream(pathToModelFile);
		if (resourceAsStream == null)
			throw new AssertionFailedError(String.format("missing %s project.", pathToModelFile));

		AstahAPI.getAstahAPI().getProjectAccessor().open(resourceAsStream);

		INamedElement[] elems = AstahAPI.getAstahAPI().getProjectAccessor()
				.findElements(IInternalBlockDiagram.class, ibdDiagramName);
		if (elems == null || elems.length == 0) {
			throw new AssertionFailedError(String.format("missing %s ibd.", ibdDiagramName));
		}

		if (elems.length > 1) {
			throw new AssertionFailedError(String.format("find multiply %s ibd.", ibdDiagramName));
		}

		IDiagram currentDiagram = (IDiagram) elems[0];
		Marshaller marshaller = new Marshaller();

		RtsMarshaller rtsMarshaller = new RtsMarshaller();
		final Date tested = new Date(0);
		RtsProfileBasicInfoBuilder rtsbib = new RtsProfileBasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return tested;
			}
		};
		rtsMarshaller.setBasicInfoBuilder(rtsbib);
		marshaller.setRtsMarshaller(rtsMarshaller);

		RtcMarshaller rtcMarshaller = new RtcMarshaller();
		BasicInfoBuilder rtcbib = new BasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return tested;
			}
		};

		rtcMarshaller.setBasicInfoBuilder(rtcbib);
		marshaller.setRtcMarshaller(rtcMarshaller);

		File newFolder = folder.newFolder();
		marshaller.marshal(currentDiagram, newFolder.getPath());

		return newFolder;
	}
}
