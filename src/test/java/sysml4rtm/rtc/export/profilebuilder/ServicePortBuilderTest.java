package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openrtp.namespaces.rtc.Serviceinterface;
import org.openrtp.namespaces.rtc.Serviceport;
import org.openrtp.namespaces.rtc_ext.Position;
import org.openrtp.namespaces.rtc_ext.ServiceinterfaceExt;
import org.openrtp.namespaces.rtc_ext.ServiceportExt;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IAttribute;

public class ServicePortBuilderTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private String pathToOutputFolder;
	
	@Test
	public void Serviceport_nameは_提供か要求インタフェースがひもづいているポート名から設定されること() throws Exception{
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block0");

		assertThat(ports.size(), is(3));
		assertNotNull(findPort(ports, "ServicePortA"));
		assertNotNull(findPort(ports, "ServicePortB"));
		assertNotNull(findPort(ports, "ServicePortC"));

	}
	
	@Test
	public void Serviceport_positionは_TOP固定で設定されること() throws Exception{
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "ProvidedA");
		assertThat(port.getPosition(),is(Position.TOP));

	}
	
	@Test
	public void 提供か要求インタフェースがひもづいている数だけServiceinterface要素が作成されること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "multi");
		assertThat(port.getServiceInterface().size(),is(3));
		assertThat(port.getServiceInterface().get(0).getType(),is("A"));
		assertThat(port.getServiceInterface().get(1).getType(),is("B"));
		assertThat(port.getServiceInterface().get(2).getType(),is("C"));
	}
	
	@Test
	public void Serviceinterface_nameは_型名をuncapitalizeした値が設定されること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getName(),is("interfaceA"));
	}
	
	@Test
	public void Serviceinterface_typeは_名前空間付きのインタフェース名が設定されること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getType(),is("InterfaceA"));
		
		port = findPort(ports, "WithNameSpace");
		si = port.getServiceInterface().get(0);
		assertThat(si.getType(),is("com::service::MyService"));
	}
	
	@Test
	public void Serviceinterface_variablename_instancename_pathは_空白が設定されること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "ProvidedA");
		ServiceinterfaceExt si = (ServiceinterfaceExt) port.getServiceInterface().get(0);
		assertThat(si.getInstanceName(),is(""));
		assertThat(si.getPath(),is(""));
		assertThat(si.getVariableName(),is(""));
	}
	
	@Test
	public void Serviceinterface_directionは提供インタフェースの場合_Providedであること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getDirection(),is("Provided"));
	}
	
	
	@Test
	public void Serviceinterface_directionは要求インタフェースの場合_Requiredであること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "RequiredA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getDirection(),is("Required"));
	}
	
	@Test
	public void Serviceinterface_idlFileは_出力したIDLファイルへの絶対パス名であること() throws Exception {
		List<ServiceportExt> ports = findTestTarget("serviceport.asml", ":Block1");
		
		ServiceportExt port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getIdlFile(),is(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "InterfaceA.idl"));
		
		port = findPort(ports, "WithNameSpace");
		si = port.getServiceInterface().get(0);
		assertThat(si.getIdlFile(),is(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "com/service/MyService.idl"));
	}
	
	@Test
	public void サービスポートが提供_要求しているIDLファイルがそれぞれ生成されること() throws Exception {
		findTestTarget("serviceport_idl.asml", ":BlockA");
		
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "a/IA.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "a/b/IB.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "IC.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "d/ID.idl").exists(),is(true));
	}
	
	@Test
	public void サービスポートが提供_要求しているインタフェースで利用している独自型のIDLが生成されること() throws Exception {
		findTestTarget("serviceport_customtype_idl.asml", ":BlockA");
		
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "a/IA.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "a/CustomA.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "a1/CustomA1.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "c/c1/CustomC1.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "c/CustomC.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "CustomD.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "CustomD1.idl").exists(),is(true));
	}
	
	private ServiceportExt findPort(List<ServiceportExt> ports, String portName) {
		for (ServiceportExt port : ports) {
			if (port.getName().equals(portName))
				return port;
		}
		return null;
	}

	private List<ServiceportExt> findTestTarget(String pathToModelFile, String partFullName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IAttribute part = AstahModelFinder.findPart(partFullName);

		pathToOutputFolder = folder.newFolder().getPath();
		ServicePortBuilder builder = new ServicePortBuilder(pathToOutputFolder);
		return builder.build(part);
	}
}
