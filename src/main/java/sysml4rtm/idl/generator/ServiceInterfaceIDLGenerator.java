package sysml4rtm.idl.generator;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;

public class ServiceInterfaceIDLGenerator extends IDLGeneratorBase {

    private static final String TEMPLATE_SERVICE_IDL = "serviceinterface_idl.vm";;

    public ServiceInterfaceIDLGenerator(Target target) {
        this.target = target;
    }


    public String generateIDL(IClass serviceInterface, String encoding) {
        String idlFileName = getIDLFileName(serviceInterface);

        VelocityContext context = new VelocityContext();
        context.put("interface", serviceInterface);
        context.put("helper", IDLUtils.class);
        context.put("typedefs", getSequenceTypeDef(serviceInterface));
        context.put("this", this);
        context.put("stringutil", StringUtils.class);
        context.put("includedIDLs", getIncludedIDLs(serviceInterface));
        String pathToOutput = IDLUtils
                .getPathToOutputWithPackage(target.getPathToOutput(), serviceInterface);

        String result = IDLUtils
                .saveTemplate(pathToOutput, idlFileName, TEMPLATE_SERVICE_IDL, context, encoding);

        return result;
    }

    public String getAttributeType(IAttribute attribute) {
        IClass type = attribute.getType();
        String typeName = type.getFullName("::");

        return getType(attribute, typeName);
    }


	private String getType(INamedElement element, String typeName) {
		StringBuilder builder = new StringBuilder();

        if (IDLUtils.isSysMLBuiltinType(typeName)) {
			builder.append(convertSysmlToIdlType(typeName));
		}else if (IDLUtils.isIDLPrimitiveType(typeName)){
			builder.append(convertIDLType(typeName));
		}else if (IDLUtils.isIDLSequenceType(typeName)) {
			builder.append(getSequenceTypeDefType(element));
        } else {
            if (IDLUtils.isCustomType(typeName) && !StringUtils.contains(typeName, "::")) {
            	builder.append("::");
            }
            builder.append(typeName);
        }
        builder.append(element.getTypeModifier());
		return builder.toString();
	}

    public String getOperationReturnType(IOperation operation) {
        IClass returnType = operation.getReturnType();
        String typeName = returnType != null? returnType.getFullName("::") : "void";
        
        return getType(operation,typeName);
    }

    public String getOperationParameter(IOperation operation) {
        StringBuilder sb = new StringBuilder();
        for (IParameter param : operation.getParameters()) {
            sb.append(param.getDirection()).append(" ");
            sb.append(getParameterType(param));
            sb.append(" ");
            sb.append(getParameterName(param));
            sb.append(", ");
        }
        return StringUtils.chomp(sb.toString(), ", ");
    }

    public String getParameterType(IParameter param){
    	IClass type = param.getType();
    	String typeexpression = type.getFullName("::");
   	 	return getType(param, typeexpression);
	}
    
    public String getOperationName(IOperation operation) {
        return operation.getName();
    }

    public String getParameterName(IParameter parameter) {
        return parameter.getName();
    }

}
