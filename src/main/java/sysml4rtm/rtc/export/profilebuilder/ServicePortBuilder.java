package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openrtp.namespaces.rtc.Serviceinterface;
import org.openrtp.namespaces.rtc.Serviceport;
import org.openrtp.namespaces.rtc_ext.ServiceinterfaceExt;
import org.openrtp.namespaces.rtc_ext.ServiceportExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.idl.generator.ServiceInterfaceIDLGenerator;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IPort;

public class ServicePortBuilder {

	private ServiceInterfaceIDLGenerator generator;

	public ServicePortBuilder(String pathToOutputFolder) {
		generator = new ServiceInterfaceIDLGenerator(pathToOutputFolder);
	}
	
	public List<Serviceport> build(IAttribute part) {
		IBlock block = (IBlock) part.getType();
		List<Serviceport> servicePorts = new ArrayList<Serviceport>();

		for (IPort port : block.getPorts()) {
			if (ModelUtils.hasServiceInterface(port)) {
				Serviceport servicePort = new ServiceportExt();
				servicePort.setName(port.getName());
				servicePorts.add(servicePort);

				buildServiceInterface(servicePort, port.getProvidedInterfaces(), "Provided");
				buildServiceInterface(servicePort, port.getRequiredInterfaces(), "Required");
			}
		}

		return servicePorts;
	}

	private void buildServiceInterface(Serviceport servicePort, IClass[] targets, String direction) {
		for (IClass target : targets) {
			Serviceinterface serviceinterface = new ServiceinterfaceExt();
			servicePort.getServiceInterface().add(serviceinterface);

			serviceinterface.setName(StringUtils.uncapitalize(target.getName()));
			serviceinterface.setType(target.getFullName("_"));
			serviceinterface.setInstanceName("");
			serviceinterface.setDirection(direction);
			serviceinterface.setIdlFile(target.getFullName("/") + ".idl");
			serviceinterface.setPath("");
			
			generator.generateIDL(target, Constants.ENCODING);
		}

	}

}
