package sysml4rtm.rtc.export.profilebuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.openrtp.namespaces.rtc.Serviceport;
import org.openrtp.namespaces.rtc_ext.Position;
import org.openrtp.namespaces.rtc_ext.ServiceinterfaceExt;
import org.openrtp.namespaces.rtc_ext.ServiceportExt;

import sysml4rtm.constants.Constants;
import sysml4rtm.idl.generator.CustomTypeIDLGenerator;
import sysml4rtm.idl.generator.ServiceInterfaceIDLGenerator;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IPort;

public class ServicePortBuilder {

	private ServiceInterfaceIDLGenerator serviceInterfaceGenerator;
	private CustomTypeIDLGenerator customTypeIDLGenerator;
	
	private String pathToOutputFolder;

	public ServicePortBuilder(String pathToOutputFolder) {
		this.pathToOutputFolder = pathToOutputFolder;
		serviceInterfaceGenerator = new ServiceInterfaceIDLGenerator(pathToOutputFolder);
		customTypeIDLGenerator = new CustomTypeIDLGenerator(pathToOutputFolder);
	}
	
	public List<ServiceportExt> build(IAttribute part) {
		IBlock block = (IBlock) part.getType();
		List<ServiceportExt> servicePorts = new ArrayList<ServiceportExt>();

		for (IPort port : block.getPorts()) {
			if (ModelUtils.hasServiceInterface(port)) {
				ServiceportExt servicePort = new ServiceportExt();
				servicePort.setName(port.getName());
				servicePort.setPosition(Position.TOP);
				servicePorts.add(servicePort);

				buildServiceInterface(servicePort, port.getProvidedInterfaces(), "Provided");
				buildServiceInterface(servicePort, port.getRequiredInterfaces(), "Required");
			}
		}

		return servicePorts;
	}

	private void buildServiceInterface(Serviceport servicePort, IClass[] targets, String direction) {
		for (IClass target : targets) {
			ServiceinterfaceExt serviceinterface = new ServiceinterfaceExt();
			servicePort.getServiceInterface().add(serviceinterface);

			serviceinterface.setName(StringUtils.uncapitalize(target.getName()));
			serviceinterface.setType(target.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR));
			serviceinterface.setDirection(direction);
			serviceinterface.setIdlFile(getPathToIdlFile(target));

			serviceinterface.setInstanceName("");
			serviceinterface.setPath("");
			serviceinterface.setVariableName("");
			
			generateUsingServiceInterfaceIdls(target);
		}

	}

	private void generateUsingServiceInterfaceIdls(IClass target) {
		serviceInterfaceGenerator.generateIDL(target, Constants.ENCODING);
		customTypeIDLGenerator.generateCustomTypeIdlInServiceInterface(target, Constants.ENCODING);
	}

	private String getPathToIdlFile(IClass target) {
		return pathToOutputFolder + SystemUtils.FILE_SEPARATOR + target.getFullName("/") + ".idl";
	}

}
