package sysml4rtm.constants;

public class Constants {

	public static final String STEREOTYPE_RTC = "rtc";
	public static final String ENCODING = "UTF-8";
	public static final String MODEL_NAMESPACE_SEPARATOR = "::";
	public static final String DEFAULT_DATA_TYPE = "RTC::TimedDouble";

	// namespace constants
	public static final String NAMESPACE_RTC = "http://www.openrtp.org/namespaces/rtc";
	public static final String NAMESPACE_RTCEXT = "http://www.openrtp.org/namespaces/rtc_ext";
	public static final String NAMESPACE_RTC_ABBREVIATION = "rtc";
	public static final String NAMESPACE_RTCEXT_ABBREVIATION = "rtcExt";
	public static final String NAMESPACE_RTCDOC = "http://www.openrtp.org/namespaces/rtc_doc";
	public static final String NAMESPACE_RTCDOC_ABBREVIATION = "rtcDoc";

	private Constants() {
	}

	public enum ComponentKind {
		DFC("DataFlowComponent"), FSM("FinateStateMachine");

		private String value;

		private ComponentKind(String value) {
			ComponentKind.this.value = value;
		}

		@Override
		public String toString() {
			return ComponentKind.this.value;
		}
	}

	public enum DataPortType {
		// SysML INOUT Port regards RTC-DataOutPort
		OUT("DataOutPort"), IN("DataInPort"), INOUT("DataOutPort");

		private String value;

		private DataPortType(String value) {
			DataPortType.this.value = value;
		}

		@Override
		public String toString() {
			return DataPortType.this.value;
		}
	}
}
