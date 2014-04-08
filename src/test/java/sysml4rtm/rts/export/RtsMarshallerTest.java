package sysml4rtm.rts.export;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.rts.export.profilebuilder.RtsProfileBasicInfoBuilder;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class RtsMarshallerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void 内部ブロック図からRTS_XMLファイルが1つ生成されること() throws Exception {
		File outputFolder = marshal("stereotype.asml","targets");

		assertThat(new File(outputFolder.getPath() +  "/targets.xml").exists(), is(true));
	}

	@Test
	public void RTCステレオタイプが付与されているパートを含むモデルからRTSが生成されること() throws Exception {
		File outputFolder = marshal("comps.asml","targets");

		File actualFile = new File(outputFolder.getPath() +  "/targets.xml");
		File actual = FileUtils.getFile(actualFile);
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_comp.xml")
				.getPath());
		
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
		RtsMarshaller marshaller = new RtsMarshaller();
		final Date tested = new Date(0);
		RtsProfileBasicInfoBuilder basicInfoBuilder = new RtsProfileBasicInfoBuilder() {
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
