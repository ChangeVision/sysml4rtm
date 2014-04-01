package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openrtp.namespaces.rtc.RtcProfile;

import sysml4rtm.AstahModelFinder;

import com.change_vision.jude.api.inf.model.IAttribute;

public class RtcProfileBuilderTest {

	@Test
	public void shouldExtract_version()  throws Exception{
		RtcProfile profile = findTestTarget("marshal_basic.asml", ":Block0");
		assertThat(profile.getVersion(), is("0.2"));
	}

	@Test
	public void shouldExtrace_id()  throws Exception{
		RtcProfile profile = findTestTarget("marshal_basic.asml", ":Block0");
		assertThat(profile.getId(), is("RTC:Vendor:Category:Block0:1.0.0"));
	}

	@Test
	public void shouldExtrace_id_with_namespace() throws Exception {
		RtcProfile profile = findTestTarget("marshal_basic.asml", ":com::changevision::sample::Block1");
		assertThat(profile.getId(),
				is("RTC:Vendor:Category:com::changevision::sample::Block1:1.0.0"));
	}

	private RtcProfile findTestTarget(String pathToModelFile, String partFullname) throws Exception{
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		IAttribute part = AstahModelFinder.findPart(partFullname);

		RtcProfileBuilder builder = new RtcProfileBuilder();
		RtcProfile basicinfo = builder.createRtcProfile(part);
		return basicinfo;
	}
}
