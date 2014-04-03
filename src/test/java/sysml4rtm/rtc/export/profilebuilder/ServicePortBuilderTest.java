package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rtc.Serviceinterface;
import org.openrtp.namespaces.rtc.Serviceport;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IAttribute;

public class ServicePortBuilderTest {

	@Test
	public void Serviceport_nameは_提供か要求インタフェースがひもづいているポート名から設定されること() throws Exception{
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block0");

		assertThat(ports.size(), is(3));
		assertNotNull(findPort(ports, "ServicePortA"));
		assertNotNull(findPort(ports, "ServicePortB"));
		assertNotNull(findPort(ports, "ServicePortC"));

	}
	
	@Test
	public void 提供か要求インタフェースがひもづいている数だけServiceinterface要素が作成されること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "multi");
		assertThat(port.getServiceInterface().size(),is(3));
		assertThat(port.getServiceInterface().get(0).getType(),is("A"));
		assertThat(port.getServiceInterface().get(1).getType(),is("B"));
		assertThat(port.getServiceInterface().get(2).getType(),is("C"));
	}
	
	@Test
	public void Serviceinterface_nameは_型名をuncapitalizeした値が設定されること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getName(),is("interfaceA"));
	}
	
	@Test
	public void Serviceinterface_typeは_名前空間をアンダースコアに変換したインタフェース名が設定されること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getType(),is("InterfaceA"));
		
		port = findPort(ports, "WithNameSpace");
		si = port.getServiceInterface().get(0);
		assertThat(si.getType(),is("com_service_MyService"));
	}
	
	@Test
	public void Serviceinterface_instancename_と_pathは_空白が設定されること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getInstanceName(),is(""));
		assertThat(si.getPath(),is(""));
	}
	
	@Test
	public void Serviceinterface_directionは提供インタフェースの場合_Providedであること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getDirection(),is("Provided"));
	}
	
	
	@Test
	public void Serviceinterface_directionは要求インタフェースの場合_Requiredであること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "RequiredA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getDirection(),is("Required"));
	}
	
	@Test
	public void Serviceinterface_idlFileは型名_idlファイルであること() throws Exception {
		List<Serviceport> ports = findTestTarget("serviceport.asml", ":Block1");
		
		Serviceport port = findPort(ports, "ProvidedA");
		Serviceinterface si = port.getServiceInterface().get(0);
		assertThat(si.getIdlFile(),is("InterfaceA.idl"));
		
		port = findPort(ports, "WithNameSpace");
		si = port.getServiceInterface().get(0);
		assertThat(si.getIdlFile(),is("MyService.idl"));
	}
	
	private Serviceport findPort(List<Serviceport> ports, String portName) {
		for (Serviceport port : ports) {
			if (port.getName().equals(portName))
				return port;
		}
		return null;
	}

	private List<Serviceport> findTestTarget(String pathToModelFile, String partFullName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IAttribute part = AstahModelFinder.findPart(partFullName);

		ServicePortBuilder builder = new ServicePortBuilder();
		return builder.build(part);
	}
}
