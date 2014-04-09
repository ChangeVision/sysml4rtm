package sysml4rtm.rts.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openrtp.namespaces.rts.Dataport;
import org.openrtp.namespaces.rts.Serviceport;
import org.openrtp.namespaces.rts_ext.ComponentExt;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class ComponentBuilderTest {

	@Test
	public void idにはパートの型が指定されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getId(), is("RTC:Vender:com::BlockA:1.0"));
	}

	@Test
	public void pathUriには固定文字とパート名が指定されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getPathUri(), is("localhost:2809/partA.rtc"));
	}

	@Test
	public void パート名が空の場合_型名をuncapitalizeした文字と0の組み合わせがpathUriに指定されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp_null_part.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getPathUri(), is("localhost:2809/blockA0.rtc"));
	}

	@Test
	public void instanceNameにはパート名が指定されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getInstanceName(), is("partA"));
	}

	@Test
	public void パート名が空の場合_型名をuncapitalizeした文字と0の組み合わせがinstanceNameに指定されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp_null_part.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getInstanceName(), is("blockA0"));
	}
	
	@Test
	public void 固定プロパティが生成されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getCompositeType(), is("None"));
		assertThat(comp.isIsRequired(), is(true));
		assertThat(comp.isVisible(), is(true));
	}
	

	@Test
	public void ブロックのデータポートとみなされるポートからデータポートタグが生成されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp_port.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getDataPorts().size(),is(2));
		
		assertNotNull(findDataPort(comp.getDataPorts(),"a"));
		assertNotNull(findDataPort(comp.getDataPorts(),"b"));
	}

	@Test
	public void ブロックのサービスポートとみなされるポートからサービスポートタグが生成されること() throws Exception {
		List<ComponentExt> comps = findTestTarget("comp_port.asml", "targets");

		ComponentExt comp = findComponent(comps, "com::BlockA");
		assertThat(comp.getServicePorts().size(),is(2));
		
		assertNotNull(findServicePort(comp.getServicePorts(),"c"));
		assertNotNull(findServicePort(comp.getServicePorts(),"d"));
	}
	
	private Serviceport findServicePort(List<Serviceport> ports, String name){
		for(Serviceport port : ports){
			if(port.getName().equals(name))
				return port;
		}
		
		return null;
	}
	
	private Dataport findDataPort(List<Dataport> ports, String name){
		for(Dataport port : ports){
			if(port.getName().equals(name))
				return port;
		}
		
		return null;
	}
	
	private ComponentExt findComponent(List<ComponentExt> comps, String typeFullName) {
		for (ComponentExt comp : comps) {
			if (comp.getId().equals(String.format("RTC:Vender:%s:1.0", typeFullName)))
				return comp;
		}
		return null;
	}

	private List<ComponentExt> findTestTarget(String pathToModelFile, String diagramName) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IInternalBlockDiagram ibd = AstahModelFinder.findIbdDiagram(diagramName);

		ComponentBuilder builder = new ComponentBuilder();
		return builder.build(ibd);
	}
}
