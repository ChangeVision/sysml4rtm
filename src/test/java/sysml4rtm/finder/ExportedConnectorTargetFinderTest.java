package sysml4rtm.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IConnector;
import com.change_vision.jude.api.inf.model.IDependency;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class ExportedConnectorTargetFinderTest {

	@Test
	public void rtsステレオタイプが付いているパート間のコネクタが取得できること() throws Exception {
		ExportedConnectorTargetFinder finder = new ExportedConnectorTargetFinder();

		AstahModelFinder.open(this.getClass().getResourceAsStream("assembly_connector.asml"));
		
		IInternalBlockDiagram target = AstahModelFinder.findIbdDiagram("target");
		List<IConnector> elements = finder.findConnector(target);

		assertThat(elements.size(), is(2));
	}
	
	@Test
	public void データポートとサービスポート_それぞれのポート間のコネクタが取得できること() throws Exception {
		ExportedConnectorTargetFinder finder = new ExportedConnectorTargetFinder();
		
		AstahModelFinder.open(this.getClass().getResourceAsStream("assembly_connector.asml"));
		
		IInternalBlockDiagram target = AstahModelFinder.findIbdDiagram("target");
		
		assertThat(finder.findDataPortConnector(target).size(), is(1));
		
		assertThat(finder.findServicePortConnector(target).size(), is(1));
	}
	
	@Test
	public void rtsステレオタイプが付いているパート間のアセンブリコネクタが取得できること() throws Exception {
		ExportedConnectorTargetFinder finder = new ExportedConnectorTargetFinder();

		AstahModelFinder.open(this.getClass().getResourceAsStream("assembly_connector.asml"));
		
		IInternalBlockDiagram target = AstahModelFinder.findIbdDiagram("target");
		List<IDependency> elements = finder.findAssemblyConnector(target);

		assertThat(elements.size(), is(1));
	}
	
}
