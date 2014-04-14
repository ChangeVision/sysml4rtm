package sysml4rtm.rts.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rts.TargetPort;
import org.openrtp.namespaces.rts_ext.DataportConnectorExt;
import org.openrtp.namespaces.rts_ext.Property;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class DataPortConnectorsBuilderTest {

	@Test
	public void connectorIdにはモデルのIDが設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		assertThat(conexts.size(),is(3));
		
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		assertNotNull(conext);
	}

	@Test
	public void nameにはItemFlowの場合_モデルのIDが設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getName(), is(con.getId()));
	}

	@Test
	public void nameにはConnectorで名前が設定されている場合_その名称が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("2");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getName(), is("name"));
	}
	
	@Test
	public void nameにはConnectorで名前が設定されていない場合_モデルのIDが設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getName(), is(con.getId()));
	}
	
	@Test
	public void dataTypeとdataport_datatypeにはコネクタから取得されたデータ型が設定されること_ItemFlow() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getDataType(), is("IDL:RTC/TimedBoolean:1.0"));
		
		Property prop = getProperty("dataport.data_type", conext);
		assertNotNull(prop);
		assertThat(prop.getValue(), is("IDL:RTC/TimedBoolean:1.0"));
	}
	
	@Test
	public void dataTypeとdataport_datatypeにはコネクタから取得されたデータ型が設定されること_Connector() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("2");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getDataType(), is("IDL:RTC/TimedDouble:1.0"));
		
		Property prop = getProperty("dataport.data_type", conext);
		assertNotNull(prop);
		assertThat(prop.getValue(), is("IDL:RTC/TimedDouble:1.0"));
	}
	
	@Test
	public void dataTypeとdataport_datatypeにはコネクタから取得されたデータ型が設定されること_Connector_独自型() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getDataType(), is("IDL:com/sample/V1:1.0"));
		
		Property prop = getProperty("dataport.data_type", conext);
		assertNotNull(prop);
		assertThat(prop.getValue(), is("IDL:com/sample/V1:1.0"));
	}
	
	@Test
	public void interfaceType_dataflowType_subscriptionTypeには_固定のプロパティが設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		assertThat(conext.getInterfaceType(), is("corba_cdr"));
		assertThat(conext.getDataflowType(), is("push"));
		assertThat(conext.getSubscriptionType(), is("flush"));
	}
	
	@Test
	public void sourceDataPortのportnameにはポート名が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceDataPort();
		assertThat(sourceDataPort.getPortName(),is("af1"));
	}
	
	@Test
	public void sourceDataPortのcomponentIdにはパートの型が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceDataPort();
		assertThat(sourceDataPort.getComponentId(),is("RTC:Vender:AF:1.0"));
	}
	
	@Test
	public void sourceDataPortのinstanceNameにはパート名が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceDataPort();
		assertThat(sourceDataPort.getInstanceName(),is("af"));
	}
	
	@Test
	public void sourceDataPortのinstanceNameにはパート名が空の場合_型名と0の組み合わせが設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort sourceDataPort = conext.getSourceDataPort();
		assertThat(sourceDataPort.getInstanceName(),is("BT0"));
	}
	
	@Test
	public void targetDataPortのportnameにはポート名が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetDataPort();
		assertThat(target.getPortName(),is("at1"));
	}
	
	@Test
	public void targetDataPortのcomponentIdにはパートの型が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetDataPort();
		assertThat(target.getComponentId(),is("RTC:Vender:AT:1.0"));
	}
	
	@Test
	public void targetDataPortのinstanceNameにはパート名が設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("1");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetDataPort();
		assertThat(target.getInstanceName(),is("at"));
	}
	
	@Test
	public void targetDataPortのinstanceNameにはパート名が空の場合_型名と0の組み合わせが設定されること() throws Exception{
		List<DataportConnectorExt> conexts = findTestTarget("dataport_connectors.asml", "target");
	
		IConnector con = AstahModelFinder.findConnector("3");
		DataportConnectorExt conext = findDataportConnectorExt(conexts, con.getId());
		
		TargetPort target = conext.getTargetDataPort();
		assertThat(target.getInstanceName(),is("BF0"));
	}
	
	private Property getProperty(String name, DataportConnectorExt con){
		List<Property> properties = con.getProperties();
		for(Property prop : properties){
			if(prop.getName().equals(name))
				return prop;
		}
		return null;
	}
	private DataportConnectorExt findDataportConnectorExt(List<DataportConnectorExt> cons, String id){
		for(DataportConnectorExt con : cons){
			if(con.getConnectorId().equals(id))
				return con;
		}
		return null;
	}

	private List<DataportConnectorExt> findTestTarget(String pathToModelFile, String diagramName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IInternalBlockDiagram ibd = AstahModelFinder.findIbdDiagram(diagramName);

		DataPortConnectorsBuilder builder = new DataPortConnectorsBuilder();
		return builder.build(ibd);
	}
}
