package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openrtp.namespaces.rtc.RtcProfile;

import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.rtc.export.profilebuilder.RtcProfileBuilder;

import com.change_vision.jude.api.inf.model.IBlock;

public class RtcProfileBuilderTest {

	@Test
	public void shouldExtract_version() {
		RtcProfile profile = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "Block0");
		assertThat(profile.getVersion(), is("0.2"));
	}

	@Test
	public void shouldExtrace_id() {
		RtcProfile profile = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "Block0");
		assertThat(profile.getId(), is("RTC:Vendor:Category:Block0:1.0.0"));
	}

	@Test
	public void shouldExtrace_id_with_namespace() {
		RtcProfile profile = findTestTarget(this.getClass().getResource("marshal_basic.asml")
				.getPath(), "com::changevision::sample::Block1");
		assertThat(profile.getId(),
				is("RTC:Vendor:Category:com::changevision::sample::Block1:1.0.0"));
	}

	private RtcProfile findTestTarget(String pathToModelFile, String blockFullName) {
		ProjectAccessorFacade.openProject(pathToModelFile);
		IBlock block = ProjectAccessorFacade.findBlock(blockFullName);

		RtcProfileBuilder builder = new RtcProfileBuilder();
		RtcProfile basicinfo = builder.createRtcProfile(block);
		return basicinfo;
	}
}
