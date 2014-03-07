package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rtc.Dataport;

import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.constants.Constants;
import sysml4rtm.rtc.export.profilebuilder.DataPortBuilder;

import com.change_vision.jude.api.inf.model.IBlock;

public class DataPortBuilderTest {

	@Test
	public void shouldExtract_portName() {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block0");

		Dataport port = findPort(dataports, "Out");
		assertThat(port.getName(), is("Out"));
	}

	@Test
	public void shouldExtract_portDirection_out_by_flowproperty() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "Out_WithFlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_out_by_flowproperties() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "Out_WithFlowProperties");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_not_defined_flowproperty() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "NotDefined_FlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_in_by_flowproperty() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "In_WithFlowProperty");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void shouldExtract_portDirection_in_by_flowproperties() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "In_WithFlowProperties");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void shouldExtract_portDirection_by_inout_flowproperty() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "InOut");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_by_inout_mixflowproperty() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "Block1");

		Dataport port = findPort(dataports, "InOutMix");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_out_by_itemflow() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockB");

		Dataport port = findPort(dataports, "Single");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_in_by_itemflow() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockA");

		Dataport port = findPort(dataports, "Single");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void shouldExtract_portDirection_out_by_multiple_itemflow() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockB");

		Dataport port = findPort(dataports, "Multi");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_in_by_multiple_itemflow() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockA");

		Dataport port = findPort(dataports, "Multi");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void shouldExtract_portDirection_out_by_itemflow_with_portType() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockB");

		Dataport port = findPort(dataports, "NothingFlowPropery");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	@Test
	public void shouldExtract_portDirection_in_by_itemflow_with_portType() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockA");

		Dataport port = findPort(dataports, "NothingFlowPropery");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.IN.toString()));
	}

	@Test
	public void shouldExtract_portDirection_multiple_bydirectional_itemflow() throws Exception {
		List<Dataport> dataports = findTestTarget(this.getClass().getResource("port.asml")
				.getPath(), "itemflow::BlockC");

		Dataport port = findPort(dataports, "unknown");
		assertNotNull(port);
		assertThat(port.getPortType(), is(Constants.DataPortType.OUT.toString()));
	}

	private Dataport findPort(List<Dataport> ports, String portName) {
		for (Dataport port : ports) {
			if (port.getName().equals(portName))
				return port;
		}
		return null;
	}

	private List<Dataport> findTestTarget(String pathToModelFile, String blockFullName) {
		ProjectAccessorFacade.openProject(pathToModelFile);
		IBlock block = ProjectAccessorFacade.findBlock(blockFullName);

		DataPortBuilder builder = new DataPortBuilder();
		return builder.build(block);
	}
}
