package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.openrtp.namespaces.rtc.Dataport;
import org.openrtp.namespaces.rtc_ext.DataportExt;
import org.openrtp.namespaces.rtc_ext.Position;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.constants.Constants;

import com.change_vision.jude.api.inf.model.IAttribute;

public class DataPortBuilderTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private String pathToOutputFolder;
	
	@Test
	public void port_nameはポート名から設定されること() throws Exception{
		List<DataportExt> dataports = findTestTarget("port.asml", ":Block0");

		Dataport port = findPort(dataports, "Out");
		assertThat(port.getName(), is("Out"));
	}

	@Test
	public void positionは_LEFT固定で設定されること() throws Exception{
		List<DataportExt> dataports = findTestTarget("port.asml", ":Block0");
		
		DataportExt port = findPort(dataports, "Out");
		assertThat(port.getPosition(),is(Position.LEFT));

	}
	
	@Test
	public void OUT_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "Out_WithFlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void 複数のOUT_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "Out_WithFlowProperties");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void IN_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "In_WithFlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void 複数のIN_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "In_WithFlowProperties");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void ItemFlowからOUT方向のポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":itemflow::BlockB");

		Dataport port = findPort(dataports, "Single");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void ItemFlowからIN方向のポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":itemflow::BlockA");

		Dataport port = findPort(dataports, "Single");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void 複数のItemFlowからOUT方向のポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":itemflow::BlockB");

		Dataport port = findPort(dataports, "Multi");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void 複数のItemFlowからIN方向のポートの方向種別が決定されること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port.asml", ":itemflow::BlockA");

		Dataport port = findPort(dataports, "Multi");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void RTC組み込み型を利用しているフロープロパティからポートのデータ型を判断できること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "rtcType");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedDouble"));
	}
	
	@Test
	public void 独自型を利用しているフロープロパティからポートのデータ型を判断できること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "customer");
		assertNotNull(port);
		assertThat(port.getType(), is("Customer"));
	}

	@Test
	public void ItemFlowのItemPropertyからポートのデータ型を判断できること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "itemflow");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedDouble"));
	}
	
	@Test
	public void FlowPropertyとItemFlowが両方定義されていて_ItemFlowのItemPropertyが定義されている場合_ItemPropertyの型からポートのデータ型を判断できること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "flowPropAndItemFlow");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedString"));
	}
	
	@Test
	public void ItemFlowのConveyからポートのデータ型を判断できること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "convey");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedFloat"));
	}
	
	@Test
	public void FlowPropertyとItemFlowが両方定義されて_ItemFlowのConveyが定義されている場合_Conveyの型からポートのデータ型を判断できること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "conveyAndFlowProperties");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedBoolean"));
	}
	
	@Test
	public void ポートの型が独自型の場合_そのIDLファイルが生成されること() throws Exception {
		findTestTarget("dataport_customtype_idl.asml", ":BlockA");
		
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "v1/V1.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "com/v2/V2.idl").exists(),is(true));
		assertThat(new File(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "V3.idl").exists(),is(true));
	}
	
	@Test
	public void ポートの共役が有効である場合_フロープロパティから判断される方向性は逆になること() throws Exception {
		List<DataportExt> dataports = findTestTarget("port_conjugated.asml", ":BlockB");

		assertThat(findPort(dataports, "a").getPortType(), is(Constants.DataPortType.IN.toString()));
		assertThat(findPort(dataports, "b").getPortType(), is(Constants.DataPortType.OUT.toString()));
		assertThat(findPort(dataports, "c").getPortType(), is(Constants.DataPortType.OUT.toString()));
	}
	
	@Test
	public void idlFileは_出力したIDLファイルへの絶対パス名であること() throws Exception {
		List<DataportExt> ports = findTestTarget("dataport_customtype_idl.asml", ":BlockB");
		
		DataportExt port = findPort(ports, "default");
		assertThat(port.getIdlFile(),is(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "V3.idl"));
		
		port = findPort(ports, "WithNameSpace");
		assertThat(port.getIdlFile(),is(pathToOutputFolder + SystemUtils.FILE_SEPARATOR + "v1/V1.idl"));
	}
	
	private DataportExt findPort(List<DataportExt> ports, String portName) {
		for (DataportExt port : ports) {
			if (port.getName().equals(portName))
				return port;
		}
		return null;
	}

	private List<DataportExt> findTestTarget(String pathToModelFile, String partFullName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IAttribute part = AstahModelFinder.findPart(partFullName);

		pathToOutputFolder = folder.newFolder().getPath();
		DataPortBuilder builder = new DataPortBuilder(pathToOutputFolder);
		return builder.build(part);
	}
}
