package sysml4rtm;

import sysml4rtm.rtc.export.RtcMarshaller;
import sysml4rtm.rts.export.RtsMarshaller;

import com.change_vision.jude.api.inf.model.IDiagram;

public class Marshaller {
	
	private RtcMarshaller rtcMarshaller;
	private RtsMarshaller rtsMarshaller;

	public Marshaller() {
		rtcMarshaller = new RtcMarshaller();
		rtsMarshaller = new RtsMarshaller();
	}

	public void marshal(IDiagram currentDiagram, String outputPath) {
		rtcMarshaller.marshal(currentDiagram,outputPath);
		rtsMarshaller.marshal(currentDiagram,outputPath);
	}
	
	public void setRtcMarshaller(RtcMarshaller rtcMarshaller){
		this.rtcMarshaller = rtcMarshaller;
	}
	
	public void setRtsMarshaller(RtsMarshaller rtsMarshaller){
		this.rtsMarshaller = rtsMarshaller;
	}

}
