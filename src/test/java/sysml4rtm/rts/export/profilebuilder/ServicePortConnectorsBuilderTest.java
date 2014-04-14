package sysml4rtm.rts.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rts.TargetPort;
import org.openrtp.namespaces.rts_ext.ServiceportConnectorExt;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class ServicePortConnectorsBuilderTest {

	@Test
	public void connectorIdにはモデルのIDが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		assertThat(conexts.size(),is(4));
		
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		assertNotNull(conext);
	}
	
	@Test
	public void nameにはConnectorで名前が設定されている場合_その名称が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
		
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		assertThat(conext.getName(), is("name"));
	}

	@Test
	public void nameにはConnectorで名前が設定されていない場合_モデルのIDが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("2");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		assertThat(conext.getName(), is(con.getId()));
	}
	
	@Test
	public void sourceDataPortのportnameにはポート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getPortName(),is("sa"));
	}
	
	@Test
	public void sourceDataPortのcomponentIdにはパートの型が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getComponentId(),is("RTC:Vender:ConsoleIn:1.0"));
	}
	
	@Test
	public void sourceDataPortのinstanceNameにはパート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getInstanceName(),is("ConsoleIn0"));
	}
	
	@Test
	public void sourceDataPortのinstanceNameにはパート名が空の場合_型名をuncapitalizeした文字と0の組み合わせが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceServicePort();
		assertThat(sourceDataPort.getInstanceName(),is("C0"));
	}
	
	@Test
	public void targetDataPortのportnameにはポート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getPortName(),is("sb"));
	}
	
	@Test
	public void targetDataPortのcomponentIdにはパートの型が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getComponentId(),is("RTC:Vender:ConsoleOut:1.0"));
	}
	
	@Test
	public void targetDataPortのinstanceNameにはパート名が設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getInstanceName(),is("ConsoleOut0"));
	}
	
	@Test
	public void targetDataPortのinstanceNameにはパート名が空の場合_型名をuncapitalizeした文字と0の組み合わせが設定されること() throws Exception{
		List<ServiceportConnectorExt> conexts = findTestTarget("serviceport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		ServiceportConnectorExt conext = findServicePortConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetServicePort();
		assertThat(target.getInstanceName(),is("D0"));
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
