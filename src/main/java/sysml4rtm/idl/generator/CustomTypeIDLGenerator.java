package sysml4rtm.idl.generator;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;

import sysml4rtm.ProjectAccessorFacade;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;

public class CustomTypeIDLGenerator extends IDLGeneratorBase {
	private static final String TEMPLATE_CUSTOMTYPE_IDL = "customtype_idl.vm";;

	public CustomTypeIDLGenerator(Target target) {
		this.target = target;
	}

	public String generateCustomTypeIdl(IClass clazz, String encoding) {
		String idlFileName = getIDLFileName(clazz);

		VelocityContext context = new VelocityContext();
		context.put("class", clazz);
		context.put("helper", IDLUtils.class);
		context.put("typedefs", getSequenceTypeDef(clazz));
		context.put("this", this);
		context.put("stringutil", StringUtils.class);
		context.put("includedIDLs", getIncludedIDLs(clazz));
		context.put("hasRecursiveSequence", hasRecursiveSequence(clazz));
		context.put("idlLines", clazz.getDefinition().split("\n"));
		String pathToOutput = IDLUtils.getPathToOutputWithPackage(target.getPathToOutput(), clazz);

		String result = IDLUtils.saveTemplate(pathToOutput, idlFileName, TEMPLATE_CUSTOMTYPE_IDL,
				context, encoding);
		generateNestedIDLs(clazz, encoding);

		return result;
	}

	public void generateCustomTypeIdlInServiceInterface(IClass serviceInterface, String encoding) {
		for (IAttribute attribute : serviceInterface.getAttributes()) {
			IClass type = attribute.getType();
			String attributeName = attribute.getName();
			String typeModifier = attribute.getTypeModifier();
			IAssociation association = attribute.getAssociation();
			if (association == null) {
				if (isCustomType(type, typeModifier, serviceInterface, attributeName)) {
					generateIDLForCustomTypeWithSequence(type, typeModifier, serviceInterface,
							attributeName, encoding);
				}
			} else {
				IAttribute[] memberEnds = association.getMemberEnds();
				for (IAttribute memberEnd : memberEnds) {
					// 自クラス側の関連端を処理しない
					if (StringUtils.equals(serviceInterface.getId(), memberEnd.getType().getId())) {
						continue;
					}
					if (!IDLUtils.isNavigable(memberEnd.getNavigability())) {
						continue;
					}
					if (isCustomType(type, typeModifier, serviceInterface, attributeName)) {
						generateIDLForCustomTypeWithSequence(type, typeModifier, serviceInterface,
								attributeName, encoding);
					}
				}
			}
		}

		for (IOperation operation : serviceInterface.getOperations()) {
			IClass type = operation.getReturnType();
			String typeModifier = operation.getTypeModifier();
			String operationName = operation.getName();
			if (type != null
					&& isCustomType(type, typeModifier, serviceInterface, operation.getName())) {
				generateIDLForCustomTypeWithSequence(type, typeModifier, serviceInterface,
						operationName, encoding);
			}

			for (IParameter parameter : operation.getParameters()) {
				type = parameter.getType();
				typeModifier = parameter.getTypeModifier();
				if (isCustomType(type, typeModifier, serviceInterface, parameter.getName())) {
					generateIDLForCustomTypeWithSequence(type, typeModifier, serviceInterface,
							operationName, encoding);
				}
			}
		}
	}

	private String generateIDLForCustomTypeWithSequence(IClass type, String typeModifier,
			IClass clazz, String target, String encoding) {
		if (IDLUtils.isIDLSequenceType(type.getName())) {
			INamedElement element = ProjectAccessorFacade.findElement(IDLUtils
					.getElementTypeFullName(type, typeModifier, clazz, target));
			return element != null ? generateCustomTypeIdl((IClass) element, encoding) : null;
		}
		return generateCustomTypeIdl(type, encoding);
	}

	private boolean isCustomType(IClass type, String typeModifier, IClass clazz, String targetName) {
		return IDLUtils.isCustomType(IDLUtils.getElementTypeFullName(type, typeModifier, clazz,
				targetName));
	}

	private void generateNestedIDLs(IClass clazz, String encoding) {
		for (IAttribute attribute : clazz.getAttributes()) {
			IAssociation association = attribute.getAssociation();
			if (association == null) {
				IClass attributeClass = attribute.getType();
				String attributeTypeName = attributeClass.getFullName("::");
				if (IDLUtils.isCustomType(attributeTypeName)) {
					generateCustomTypeIdl(attributeClass, encoding);
				}
				if (IDLUtils.isOtherCustomTypeSequence(clazz, attribute)) {
					String elementTypeName = IDLUtils.getElementTypeFullName(attributeClass,
							attribute.getTypeModifier(), clazz, attribute.getName());
					generateCustomTypeIdl(
							(IClass) ProjectAccessorFacade.findElement(elementTypeName), encoding);
				}
			} else {
				IAttribute[] memberEnds = association.getMemberEnds();
				for (IAttribute memberEnd : memberEnds) {
					// 自分側の関連端を処理しない
					if (StringUtils.equals(clazz.getId(), memberEnd.getType().getId())) {
						continue;
					}
					if (!IDLUtils.isNavigable(memberEnd.getNavigability())) {
						continue;
					}
					IClass memberEndClass = memberEnd.getType();
					String memberEndTypeName = memberEndClass.getFullName("::");
					if (IDLUtils.isCustomType(memberEndTypeName)) {
						generateCustomTypeIdl(memberEndClass, encoding);
					}
				}
			}
		}
	}

	public boolean hasRecursiveSequence(IClass clazz) {
		for (IAttribute attribute : clazz.getAttributes()) {
			IAssociation association = attribute.getAssociation();
			if (association == null) {
				if (isRecursiveSequence(clazz, attribute)) {
					return true;
				}
			} else {
				IAttribute[] memberEnds = association.getMemberEnds();
				if (StringUtils.equals(memberEnds[0].getType().getId(), memberEnds[1].getType()
						.getId())
						&& (IDLUtils.isMultiple(memberEnds[0].getMultiplicity()[0]) || IDLUtils
								.isMultiple(memberEnds[1].getMultiplicity()[0]))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isRecursiveSequence(IClass clazz, IAttribute attribute) {
		IClass attributeClass = attribute.getType();
		if (IDLUtils.isIDLSequenceType(attributeClass.getName())) {
			String elementTypeName = IDLUtils.getElementTypeFullName(attributeClass,
					attribute.getTypeModifier(), clazz, attribute.getName());
			return StringUtils.equals(elementTypeName, clazz.getFullName("::"));
		}
		return false;
	}

	protected boolean supportsServicePort() {
		return true;
	}
}
