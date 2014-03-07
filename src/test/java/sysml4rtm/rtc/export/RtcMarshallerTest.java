package sysml4rtm.rtc.export;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;

public class RtcMarshallerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void shouldGenerateRTCFile_from_BlockModelWithRtcStereotype() throws IOException {
		File outputFolder = marshal("stereotype.asml");

		Collection<File> listFiles = FileUtils.listFiles(outputFolder, new String[] { "xml" },
				false);
		assertThat(listFiles.size(), is(2));

		assertThat(new File(outputFolder, "Block0.xml").exists(), is(true));
		assertThat(new File(outputFolder, "Block2.xml").exists(), is(true));
	}

	@Test
	public void shouldGenerateRTCFile_with_namespace() throws Exception {
		File outputFolder = marshal("marshal_dataport.asml");

		File actual = FileUtils.getFile(outputFolder, "com_sample_Block0.xml");
		assertThat(actual.exists(), is(true));
	}

	@Test
	public void marshal_dataport() throws Exception {
		File outputFolder = folder.newFolder();

		RtcMarshaller marshaller = createDateFixedMarshaller();

		marshaller.marshal(this.getClass().getResource("marshal_dataport.asml").getPath(),
				outputFolder.getPath());

		File actual = FileUtils.getFile(outputFolder, "com_sample_Block0.xml");
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_marshal_dataport.xml")
				.getPath());
		XMLUnit.setXpathNamespaceContext(createNamespace());
		assertXMLEqual(FileUtils.readFileToString(expected), FileUtils.readFileToString(actual));

	}

	private File marshal(String pathToModelFile) throws IOException {
		File outputFolder = folder.newFolder();
		RtcMarshaller marshaller = new RtcMarshaller();

		marshaller.marshal(this.getClass().getResource(pathToModelFile).getPath(),
				outputFolder.getPath());
		return outputFolder;
	}

	private RtcMarshaller createDateFixedMarshaller() {
		final Date tested = new Date(0);
		BasicInfoBuilder basicInfoBuilder = new BasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return tested;
			}
		};

		RtcMarshaller marshaller = new RtcMarshaller();
		marshaller.setBasicInfoBuilder(basicInfoBuilder);
		return marshaller;
	}

	private String getAttribute(Node node, String attrname) {
		Node attr = node.getAttributes().getNamedItem(attrname);
		if (attr == null)
			throw new AssertionFailedError(String.format("attribute %s is missing in %s", attrname,
					node.getNodeName()));
		return attr.getNodeValue();
	}

	private Document createDocument(File input) throws SAXException, IOException {
		return XMLUnit.buildControlDocument(FileUtils.readFileToString(input));
	}

	private XpathEngine createXPathEngine() throws SAXException, IOException {
		NamespaceContext ctx = createNamespace();
		XpathEngine engine = XMLUnit.newXpathEngine();
		engine.setNamespaceContext(ctx);

		return engine;
	}

	private NamespaceContext createNamespace() {
		HashMap<String, String> m = new HashMap<String, String>();
		m.put("rtcExt", "http://www.openrtp.org/namespaces/rtc_ext");
		m.put("rtcDoc", "http://www.openrtp.org/namespaces/rtc_doc");
		m.put("rtc", "http://www.openrtp.org/namespaces/rtc");
		m.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");

		NamespaceContext ctx = new SimpleNamespaceContext(m);
		return ctx;
	}
}
