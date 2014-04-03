package sysml4rtm.rtc.export;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class RtcMarshallerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void RTCステレオタイプが付与されたブロックからRTC_XMLファイルが生成されること() throws Exception {
		File outputFolder = marshal("stereotype.asml","ibd");

		Collection<File> listFiles = FileUtils.listFiles(outputFolder, new String[] { "xml" },
				false);
		assertThat(listFiles.size(), is(2));

		assertThat(new File(outputFolder, "Block0.xml").exists(), is(true));
		assertThat(new File(outputFolder, "Block2.xml").exists(), is(true));
	}

	@Test
	public void 名前空間をもつブロックからはRTC_XMLファイルがアンダースコアに変換された名前で生成されること() throws Exception {
		File outputFolder = marshal("marshal_dataports.asml","ibd");

		File actual = FileUtils.getFile(outputFolder, "com_sample_Block0.xml");
		assertThat(actual.exists(), is(true));
	}

	@Test
	public void データポートを持つブロックからRTC_XMLファイルが生成されること() throws Exception {
		File outputFolder = marshal("marshal_dataports.asml","ibd");

		File actual = FileUtils.getFile(outputFolder, "com_sample_Block0.xml");
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_marshal_dataport.xml")
				.getPath());
		
		assertXMLEqual(FileUtils.readFileToString(expected), FileUtils.readFileToString(actual));
	}

	@Test
	public void サービスポートを持つブロックからRTC_XMLファイルが生成されること() throws Exception {
		File outputFolder = marshal("marshal_serviceports.asml","ibd");

		File actual = FileUtils.getFile(outputFolder, "com_Block0.xml");
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_serviceport.xml")
				.getPath());
		System.out.println(FileUtils.readFileToString(actual));
		assertXMLEqual(FileUtils.readFileToString(expected), FileUtils.readFileToString(actual));
	}
	
	private File marshal(String pathToModelFile, String ibdDiagramName) throws Exception {
		File outputFolder = folder.newFolder();
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

		IInternalBlockDiagram diagram = (IInternalBlockDiagram) elems[0];
		RtcMarshaller marshaller = new RtcMarshaller();
		final Date tested = new Date(0);
		BasicInfoBuilder basicInfoBuilder = new BasicInfoBuilder() {
			@Override
			protected Date getNow() {
				return tested;
			}
		};

		marshaller.setBasicInfoBuilder(basicInfoBuilder);
		marshaller.marshal(diagram, outputFolder.getPath());
		return outputFolder;
	}
}
