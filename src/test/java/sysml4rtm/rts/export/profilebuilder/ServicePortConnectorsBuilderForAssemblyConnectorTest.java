package sysml4rtm.rts.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rts.TargetPort;
import org.openrtp.namespaces.rts_ext.ServiceportConnectorExt;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class ServicePortConnectorsBuilderForAssemblyConnectorTest {

	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_connectorIdにはモデルのIDが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		assertThat(conexts.size(),is(2));
		
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		assertNotNull(conext);
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_nameにはモデルのIDが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
		
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		assertThat(conext.getName(), is(con.getId()));
	}

	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_sourceDataPortのcomponentIdにはパートの型が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getComponentId(),is("RTC:Vender:B:1.0"));
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_sourceDataPortのinstanceNameにはパート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getInstanceName(),is("b"));
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_sourceDataPortのinstanceNameにはパート名が空の場合_型名をuncapitalizeした文字と0の組み合わせが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("FooService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getInstanceName(),is("D0"));
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_targetDataPortのportnameにはポート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getPortName(),is("pa"));
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_targetDataPortのcomponentIdにはパートの型が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getComponentId(),is("RTC:Vender:A:1.0"));
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_targetDataPortのinstanceNameにはパート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("MyService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getInstanceName(),is("a"));
	}
	
	@Test
	public void アセンブリコネクタから作られるServiceportConnectorExt_targetDataPortのinstanceNameにはパート名が空の場合_型名をuncapitalizeした文字と0の組み合わせが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_assembly_connectors.asml", "target");
	
		IDependency con = AstahModelFinder.findDependency("FooService");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getInstanceName(),is("C0"));
	}

	
	private ServiceportConnectorExt findServicePortConnectorExt(List<ServiceportConnectorExt> cons, String id){
		for(ServiceportConnectorExt con : cons){
			if(con.getConnectorId().equals(id))
				return con;
		}
		return null;
	}

	private List<ServiceportConnectorExt> findTestTarget(String pathToModelFile, String diagramName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IInternalBlockDiagram ibd = AstahModelFinder.findIbdDiagram(diagramName);

		ServicePortConnectorsBuilder builder = new ServicePortConnectorsBuilder();
		return builder.build(ibd);
	}
}
