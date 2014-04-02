package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rtc.Dataport;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.constants.Constants;

import com.change_vision.jude.api.inf.model.IAttribute;

public class DataPortBuilderTest {

	@Test
	public void port_nameはポート名から設定されること() throws Exception{
		List<Dataport> dataports = findTestTarget("port.asml", ":Block0");

		Dataport port = findPort(dataports, "Out");
		assertThat(port.getName(), is("Out"));
	}

	@Test
	public void OUT_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "Out_WithFlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void 複数のOUT_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "Out_WithFlowProperties");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void IN_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "In_WithFlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void 複数のIN_flowPropertyからポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":Block1");

		Dataport port = findPort(dataports, "In_WithFlowProperties");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void ItemFlowからOUT方向のポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":itemflow::BlockB");

		Dataport port = findPort(dataports, "Single");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void ItemFlowからIN方向のポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":itemflow::BlockA");

		Dataport port = findPort(dataports, "Single");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void 複数のItemFlowからOUT方向のポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":itemflow::BlockB");

		Dataport port = findPort(dataports, "Multi");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void 複数のItemFlowからIN方向のポートの方向種別が決定されること() throws Exception {
		List<Dataport> dataports = findTestTarget("port.asml", ":itemflow::BlockA");

		Dataport port = findPort(dataports, "Multi");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void RTC組み込み型を利用しているフロープロパティからポートのデータ型を判断できること() throws Exception {
		List<Dataport> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "rtcType");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedDouble"));
	}
	
	@Test
	public void 独自型を利用しているフロープロパティからポートのデータ型を判断できること() throws Exception {
		List<Dataport> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "customer");
		assertNotNull(port);
		assertThat(port.getType(), is("Customer"));
	}

	@Test
	public void ItemFlowのItemPropertyからポートのデータ型を判断できること() throws Exception {
		List<Dataport> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "itemflow");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedDouble"));
	}
	
	@Test
	public void FlowPropertyとItemFlowが両方定義されていて_ItemFlowのItemPropertyが定義されている場合_ItemPropertyの型からポートのデータ型を判断できること() throws Exception {
		List<Dataport> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "flowPropAndItemFlow");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedString"));
	}
	
	@Test
	public void ItemFlowのConveyからポートのデータ型を判断できること() throws Exception {
		List<Dataport> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "convey");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedFloat"));
	}
	
	@Test
	public void FlowPropertyとItemFlowが両方定義されて_ItemFlowのConveyが定義されている場合_Conveyの型からポートのデータ型を判断できること() throws Exception {
		List<Dataport> dataports = findTestTarget("port_type.asml", ":Block1");

		Dataport port = findPort(dataports, "conveyAndFlowProperties");
		assertNotNull(port);
		assertThat(port.getType(), is("RTC::TimedBoolean"));
	}
	
	private Dataport findPort(List<Dataport> ports, String portName) {
		for (Dataport port : ports) {
			if (port.getName().equals(portName))
				return port;
		}
		return null;
	}

	private List<Dataport> findTestTarget(String pathToModelFile, String partFullName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IAttribute part = AstahModelFinder.findPart(partFullName);

		DataPortBuilder builder = new DataPortBuilder();
		return builder.build(part);
	}
}
