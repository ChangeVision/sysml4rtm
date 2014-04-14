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
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.rtc.export.profilebuilder.BasicInfoBuilder;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;

public class RtcMarshallerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void まだ存在しないフォルダにRTC_XMLファイルが生成できること() throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream("stereotype.asml"));
		IInternalBlockDiagram diagram = AstahModelFinder.findIbdDiagram("ibd");
		RtcMarshaller marshaller = new RtcMarshaller();
		
		File outputFolder = folder.newFolder();
		marshaller.marshal(diagram, outputFolder.getPath() + "/dummy");
		assertThat(new File(outputFolder.getPath() +  "/dummy/Block0.xml").exists(), is(true));
	}
	
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
	public void 名前空間をもつブロックからはRTC_XMLファイルが名前空間のフォルダに_ブロック名のファイル名で生成されること() throws Exception {
		File outputFolder = marshal("marshal_dataports.asml","ibd");

		File actual = FileUtils.getFile(outputFolder.getPath() + "/com/sample/Block0.xml");
		assertThat(actual.exists(), is(true));
	}

	@Test
	public void データポートを持つブロックからRTC_XMLファイルが生成されること() throws Exception {
		File outputFolder = marshal("marshal_dataports.asml","ibd");

		File actual = FileUtils.getFile(outputFolder.getPath() + "/com/sample/Block0.xml");
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_marshal_dataport.xml")
				.getPath());
		
		assertXMLEqual(FileUtils.readFileToString(expected), FileUtils.readFileToString(actual));
	}

	@Test
	public void サービスポートを持つブロックからRTC_XMLファイルと依存するIDLファイルが生成されること() throws Exception {
		File outputFolder = marshal("marshal_serviceports.asml","ibd");

		File actual = FileUtils.getFile(outputFolder.getPath() + "/com/Block0.xml");
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_serviceport.xml")
				.getPath());
		assertXMLEqual(getExpectedXml(expected,outputFolder.getPath()), FileUtils.readFileToString(actual));
		
		assertThat(FileUtils.listFiles(outputFolder, new String[]{"idl"}, true).size(),is(4));
		
		assertThat(new File(outputFolder,"InterfaceA.idl").exists(), is(true));
		assertThat(new File(outputFolder,"InterfaceB.idl").exists(), is(true));
		assertThat(new File(outputFolder.getPath() + "/com/service/InterfaceC.idl").exists(), is(true));
		assertThat(new File(outputFolder.getPath() + "/com/service/InterfaceD.idl").exists(), is(true));
	}
	
	@Test
	public void データポート_サービスポート_独自型を利用しているモデルから_RTCXMLファイルとインタフェースや独自型のIDLファイルが生成されること() throws Exception {
		File outputFolder = marshal("sample_model.asml","actuator");

		File actual = FileUtils.getFile(outputFolder.getPath() + "/sample/controllers/PathPlanningController.xml");
		assertThat(actual.exists(), is(true));

		File expected = new File(this.getClass().getResource("expected_pathplanningcontroller.xml")
				.getPath());
		assertXMLEqual(getExpectedXml(expected,outputFolder.getPath()), FileUtils.readFileToString(actual));
		
		assertThat(FileUtils.listFiles(outputFolder, new String[]{"idl"}, true).size(),is(2));
		assertThat(new File(outputFolder.getPath() + "/sample/valuetypes/Angle.idl").exists(), is(true));
		assertThat(new File(outputFolder.getPath() + "/sample/MotorCommandService.idl").exists(), is(true));
	}
	
	private String getExpectedXml(File expected,String replacement) throws Exception {
		String contents = FileUtils.readFileToString(expected);
		return StringUtils.replace(contents, "@output", replacement);
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
