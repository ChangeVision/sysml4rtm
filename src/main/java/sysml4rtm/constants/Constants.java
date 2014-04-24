package sysml4rtm.constants;

import sysml4rtm.Messages;

public class Constants {

	public static final String STEREOTYPE_RTC = "rtc";
	public static final String ENCODING = "UTF-8";
	public static final String MODEL_NAMESPACE_SEPARATOR = "::";
	public static final String DEFAULT_DATA_TYPE = "RTC::TimedDouble";

	public static final String NAMESPACE_RTC = "http://www.openrtp.org/namespaces/rtc";
	public static final String NAMESPACE_RTS = "http://www.openrtp.org/namespaces/rts";

	public static final String RTC_BUILTIN_TYPE = "rtcBuiltinType";

	public static final String INITIAL_INSTANCE_NUMBER = "0";
	
	public static final String VALIDATION_ERROR_CATEGORY = Messages.getMessage("validationerror.category");
	
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
		OUT("DataOutPort"), IN("DataInPort"), INOUT("DataOutPort"), UNKNOWN("UNKNOWN");

		private String value;

		private DataPortType(String value) {
			DataPortType.this.value = value;
		}

		public static DataPortType getConjugatedType(DataPortType type){
			switch(type){
			case OUT:
				return DataPortType.IN;
			case IN:
				return DataPortType.OUT;
			default:
				return type;
			}				
		}
		
		@Override
		public String toString() {
			return DataPortType.this.value;
		}
	}
}
