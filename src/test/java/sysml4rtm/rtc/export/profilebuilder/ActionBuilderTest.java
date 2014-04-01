package sysml4rtm.rtc.export.profilebuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openrtp.namespaces.rtc.Actions;
import org.openrtp.namespaces.rtc.BasicInfo;

import sysml4rtm.constants.Constants;

public class ActionBuilderTest {

	@Test
	public void データフローコンポーネント種別が生成されること() {
		ActionBuilder builder = new ActionBuilder();
		BasicInfo basicinfo = new BasicInfo();
		basicinfo.setComponentKind(Constants.ComponentKind.DFC.toString());
		Actions actions = builder.build(basicinfo);
		assertThat(actions.getOnInitialize().isImplemented(), is(true));
		assertThat(actions.getOnExecute().isImplemented(), is(true));
	}

}
