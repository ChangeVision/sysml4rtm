package sysml4rtm.finder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IInternalBlockDiagram;

public class InternalBlockDiagramExportedTargetFinderTest {

	@Test
	public void find_nest() throws Exception {
		InternalBlockDiagramExportedTargetFinder finder = new InternalBlockDiagramExportedTargetFinder();

		AstahModelFinder.open(this.getClass().getResourceAsStream("subsystem_from_part_nest.asml"));
		
		IInternalBlockDiagram target = AstahModelFinder.findIbdDiagram("nest");
		List<IAttribute> elements = finder.find(target);

		assertThat(elements.size(), is(9));
	}

	@Test
	public void find_flat() throws Exception {
		InternalBlockDiagramExportedTargetFinder finder = new InternalBlockDiagramExportedTargetFinder();

		AstahModelFinder.open(this.getClass().getResourceAsStream("flat.asml"));
		IInternalBlockDiagram target = AstahModelFinder.findIbdDiagram("target");
		List<IAttribute> elements = finder.find(target);

		assertThat(elements.size(), is(2));
	}

}
