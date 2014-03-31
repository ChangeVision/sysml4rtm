package sysml4rtm.rtc.export;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.custommonkey.xmlunit.NamespaceContext;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;

public class RtcMarshallerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void RTCステレオタイプが付与されたブロックからRTC_XMLファイルが生成されること() throws IOException {
		File outputFolder = marshal("stereotype.asml");

		Collection<File> listFiles = FileUtils.listFiles(outputFolder, new String[] { "xml" },
				false);
		assertThat(listFiles.size(), is(2));

		assertThat(new File(outputFolder, "Block0.xml").exists(), is(true));
		assertThat(new File(outputFolder, "Block2.xml").exists(), is(true));
	}

	@Test
	public void 名前空間をもつブロックからはRTC_XMLファイルがアンダースコアに変換された名前で生成されること() throws Exception {
		File outputFolder = marshal("marshal_dataports.asml");

		File actual = FileUtils.getFile(outputFolder, "com_sample_Block0.xml");
		assertThat(actual.exists(), is(true));
	}

	@Test
	public void データポートを持つブロックからRTC_XMLファイルが生成されること() throws Exception {
		File outputFolder = folder.newFolder();

		RtcMarshaller marshaller = createDateFixedMarshaller();

		marshaller.marshal(this.getClass().getResource("marshal_dataports.asml").getPath(),
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
