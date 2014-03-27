package sysml4rtm.idl.generator;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import sysml4rtm.Messages;
import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.exception.ApplicationException;
import sysml4rtm.utils.ModelUtils;

import com.change_vision.jude.api.inf.model.IAssociation;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IMultiplicityRange;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.IParameter;

public abstract class IDLGeneratorBase {
	protected Target target;
	private int indent = 0;

	private HashMap<String, String> SYSMLTYPE_TO_IDLTYPE_MAPPING;

	public IDLGeneratorBase() {
		SYSMLTYPE_TO_IDLTYPE_MAPPING = new HashMap<String, String>();
		SYSMLTYPE_TO_IDLTYPE_MAPPING.put("SysML::Boolean", "boolean");
		SYSMLTYPE_TO_IDLTYPE_MAPPING.put("SysML::Complex", "double");
		SYSMLTYPE_TO_IDLTYPE_MAPPING.put("SysML::Integer", "long");
		SYSMLTYPE_TO_IDLTYPE_MAPPING.put("SysML::Number", "double");
		SYSMLTYPE_TO_IDLTYPE_MAPPING.put("SysML::Real", "double");
		SYSMLTYPE_TO_IDLTYPE_MAPPING.put("SysML::String", "string");
	}

	@Deprecated
	// おそらく不要
	protected boolean isPreDefineType(String type) {
		return IDLUtils.isIDLPrimitiveType(type) || ProjectAccessorFacade.findElement(type) != null;
	}

	public int getIndent() {
		return indent;
	}

	public String getIndentString() {
		return StringUtils.repeat("    ", indent);
	}

	public String getIndentString(int indentCount) {
		return StringUtils.repeat("    ", indentCount);
	}

	public String getIDLModuleDeclare(IClass clazz) {
		if (!ModelUtils.hasPackage(clazz))
			return "";

		StringBuilder sb = new StringBuilder();

		String packageName = ModelUtils.getPackage(clazz);
		String[] modules = packageName.split(":{2}");
		for (String module : modules) {
			sb.append(getIndentString()).append("module ").append(module).append("{")
					.append(SystemUtils.LINE_SEPARATOR);
			indent++;
		}
		return StringUtils.chop(sb.toString());
	}

	public String getIDLModuleDeclareClose(IClass clazz) {
		if (!ModelUtils.hasPackage(clazz))
			return "";

		StringBuilder sb = new StringBuilder();

		String packageName = ModelUtils.getPackage(clazz);
		String[] modules = packageName.split(":{2}");
		for (int i = 0; i < modules.length; i++) {
			indent--;
			sb.append(getIndentString()).append("};").append(SystemUtils.LINE_SEPARATOR);
		}
		return StringUtils.chop(sb.toString());
	}

	protected String getSequenceTypeDefType(INamedElement element) {
		return IDLUtils.getSequenceTypeDefType(element.getTypeModifier());
	}

	public String getIDLComment(int indentCount, String comment) {
		String indent = getIndentString(indentCount);

		StringBuilder builder = new StringBuilder();
		builder.append(indent).append("/*").append(SystemUtils.LINE_SEPARATOR);
		builder.append(getIDLCommentBody(indentCount, comment));
		builder.append(indent).append("*/").append(SystemUtils.LINE_SEPARATOR);
		return builder.toString();
	}

	public String getIDLCommentBody(int indentCount, String comment) {
		String indent = getIndentString(indentCount);

		StringReader reader = new StringReader(comment);
		List<String> bodies = null;
		try {
			bodies = IOUtils.readLines(reader);
		} catch (IOException e) {
			throw new ApplicationException(e);
		} finally {
			IOUtils.closeQuietly(reader);
		}

		StringBuilder builder = new StringBuilder();
		for (String line : bodies) {
			builder.append(indent).append("* ").append(line);
			builder.append("<br/>");
			builder.append(SystemUtils.LINE_SEPARATOR);
		}
		int lastIndexOfBr = builder.lastIndexOf("<br/>");
		builder.delete(lastIndexOfBr, lastIndexOfBr + "<br/>".length());
		return builder.toString();
	}

	protected Map<String, String> getSequenceTypeDef(IClass clazz) {
		Map<String, String> typedefs = new TreeMap<String, String>();
		for (IAttribute attribute : clazz.getAttributes()) {
			IAssociation association = attribute.getAssociation();
			if (association == null) {
				if (IDLUtils.isIDLSequenceType(attribute.getType().getName())) {
					addSequenceType(typedefs, clazz, attribute.getName(),
							attribute.getTypeModifier());
				}
			} else {
				IAttribute[] memberEnds = association.getMemberEnds();
				for (IAttribute memberEnd : memberEnds) {
					// 自クラスへの再帰でない場合、自クラス側の関連端を処理しない
					if (!StringUtils.equals(memberEnds[0].getType().getId(), memberEnds[1]
							.getType().getId())
							&& StringUtils.equals(clazz.getId(), memberEnd.getType().getId())) {
						continue;
					}
					if (!IDLUtils.isNavigable(memberEnd.getNavigability())) {
						continue;
					}
					String type = memberEnd.getType().getFullName("::");
					if (IDLUtils.isMultiple(memberEnd.getMultiplicity()[0])
							&& !typedefs.containsKey(type)) {
						String seqType = IDLUtils.extractTypeName(type) + "Seq";
						typedefs.put(type, seqType);
					}
				}
			}
		}

		for (IOperation operation : clazz.getOperations()) {
			IClass returnType = operation.getReturnType();
			if (returnType != null && IDLUtils.isIDLSequenceType(returnType.getName())) {
				addSequenceType(typedefs, clazz, operation.getName(), operation.getTypeModifier());
			}

			for (IParameter parameter : operation.getParameters()) {
				if (IDLUtils.isIDLSequenceType(parameter.getType().getName())) {
					addSequenceType(typedefs, clazz, operation.getName(),
							parameter.getTypeModifier());
				}
			}
		}

		return typedefs;
	}

	protected void addSequenceType(Map<String, String> typedefs, IClass clazz, String targetName,
			String typeModifier) {
		String type = IDLUtils.getElementTypeOfSequence(typeModifier, clazz, targetName);
		if (!isPreDefineType(type)) {
			String message = Messages.getMessage("error.sequence.serviceinterface",
					clazz.getFullName("::"), targetName, typeModifier);
			throw new ApplicationException(message);
		}

		if (!typedefs.containsKey(type)) {
			String seqType = StringUtils.remove(
					IDLUtils.upperCaseAfterSpace(StringUtils.capitalize(IDLUtils
							.extractTypeName(type))) + "Seq", " ");
			typedefs.put(type, seqType);
		}
	}

	// this method is called from velocity.
	public String getAttributes(IClass clazz, String keyword) {
		StringBuilder builder = new StringBuilder();
		int indent = getIndent() + 1;
		Set<String> associationIdSet = new HashSet<String>();
		for (IAttribute attribute : clazz.getAttributes()) {
			IAssociation association = attribute.getAssociation();
			if (association == null) {
				appendStructAttribute(attribute.getDefinition(), indent, keyword,
						getAttributeType(attribute), attribute.getName(),
						attribute.getMultiplicity(), builder);
			} else {
				String associationId = association.getId();
				// 自クラスへの再帰の場合は同じ関連が2回出現するので二重に処理しない
				if (associationIdSet.contains(associationId)) {
					continue;
				}
				IAttribute[] memberEnds = association.getMemberEnds();
				for (IAttribute memberEnd : memberEnds) {
					boolean isRecursive = StringUtils.equals(memberEnds[0].getType().getId(),
							memberEnds[1].getType().getId());
					// 自クラスへの再帰でない場合、自クラス側の関連端を処理しない
					if (!isRecursive
							&& StringUtils.equals(clazz.getId(), memberEnd.getType().getId())) {
						continue;
					}
					if (!IDLUtils.isNavigable(memberEnd.getNavigability())) {
						continue;
					}
					appendStructAttribute(memberEnd.getDefinition(), indent, keyword,
							getMemberEndType(memberEnd),
							IDLUtils.getAttributeName(memberEnd, isRecursive),
							new IMultiplicityRange[0], builder);
				}
				associationIdSet.add(associationId);
			}
		}
		return StringUtils.chomp(builder.toString());
	}

	public String getAttributeType(IAttribute attribute) {
		String typeName = attribute.getType().getFullName("::");
		if (IDLUtils.isSysMLBuiltinType(typeName)) {
			return convertSysmlToIdlType(typeName);
		}else if (IDLUtils.isIDLPrimitiveType(typeName)){
			return convertIDLType(typeName);
		} else if (IDLUtils.isIDLSequenceType(typeName)) {
			return getSequenceTypeDefType(attribute);
		} else {
			StringBuilder builder = new StringBuilder();
			if (IDLUtils.isCustomType(typeName) && !StringUtils.contains(typeName, "::")) {
				builder.append("::");
			}
			builder.append(typeName);
			builder.append(attribute.getTypeModifier());
			return builder.toString();
		}
	}

	private String convertIDLType(String typeName) {
		if(IDLUtils.isIDLPrimitiveType(typeName)){
			return StringUtils.substringAfter(typeName, "IDL::");
		}
		return typeName;
	}

	private String convertSysmlToIdlType(String typeName) {
		return SYSMLTYPE_TO_IDLTYPE_MAPPING.get(typeName);
	}

	private String getMemberEndType(IAttribute memberEnd) {
		String typeName = memberEnd.getType().getFullName("::");
		if (IDLUtils.isMultiple(memberEnd.getMultiplicity()[0])) {
			return IDLUtils.extractTypeName(typeName) + "Seq";
		} else {
			StringBuilder builder = new StringBuilder();
			if (IDLUtils.isCustomType(typeName) && !StringUtils.contains(typeName, "::")) {
				builder.append("::");
			}
			builder.append(typeName);
			builder.append(memberEnd.getTypeModifier());
			return builder.toString();
		}
	}

	private void appendStructAttribute(String definition, int indent, String keyword, String type,
			String name, IMultiplicityRange[] multiplicities, StringBuilder builder) {
		if (StringUtils.isNotEmpty(definition)) {
			builder.append(getIDLComment(indent, definition));
		}
		builder.append(getIndentString());
		builder.append("    ");
		if (StringUtils.isEmpty(keyword)) {
			builder.append(type);
			builder.append(" ");
			builder.append(name);
			for (IMultiplicityRange multiplicity : multiplicities) {
				builder.append("[");
				builder.append(multiplicity.getUpper());
				builder.append("]");
			}
		} else {
			builder.append(keyword);
			builder.append(" ");
			builder.append(type);
			builder.append(" ");
			builder.append(name);
		}
		builder.append(";");
		builder.append(SystemUtils.LINE_SEPARATOR);
	}

	protected Set<String> getIncludedIDLs(IClass clazz) {
		Set<String> idls = new TreeSet<String>();

		String typeName = clazz.getFullName("::");
		for (IAttribute attribute : clazz.getAttributes()) {
			IAssociation association = attribute.getAssociation();
			if (association == null) {
				IClass attributeClass = attribute.getType();
				String attributeTypeName = attributeClass.getFullName("::");
				IClass type = (IClass) ProjectAccessorFacade.findElement(attributeTypeName);
				if (IDLUtils.isCustomType(attributeTypeName)
						&& !StringUtils.equals(typeName, attributeTypeName) && type != null) {
					idls.add(getIncludedIDL(type));
					idls.addAll(getIncludedIDLs(type));
				}
				if (IDLUtils.isOtherCustomTypeSequence(clazz, attribute)) {
					String elementTypeName = IDLUtils.getElementTypeFullName(attributeClass,
							attribute.getTypeModifier(), clazz, attribute.getName());
					type = (IClass) ProjectAccessorFacade.findElement(elementTypeName);
					if (type != null) {
						idls.add(getIncludedIDL(type));
						idls.addAll(getIncludedIDLs(type));
					}
				}
			} else {
				IAttribute[] memberEnds = association.getMemberEnds();
				for (IAttribute memberEnd : memberEnds) {
					// 自クラス側の関連端を処理しない
					if (StringUtils.equals(clazz.getId(), memberEnd.getType().getId())) {
						continue;
					}
					if (!IDLUtils.isNavigable(memberEnd.getNavigability())) {
						continue;
					}
					IClass memberEndClass = memberEnd.getType();
					String memberEndTypeName = memberEndClass.getFullName("::");
					IClass type = (IClass) ProjectAccessorFacade.findElement(memberEndTypeName);
					if (IDLUtils.isCustomType(memberEndTypeName) && type != null) {
						idls.add(getIncludedIDL(type));
						idls.addAll(getIncludedIDLs(type));
					}
				}
			}
		}

		for (IOperation operation : clazz.getOperations()) {
			IClass returnType = operation.getReturnType();
			if (returnType != null) {
				String typeModifier = operation.getTypeModifier();
				String targetName = operation.getName();
				String fullName = IDLUtils.getElementTypeFullName(returnType, typeModifier, clazz,
						targetName);
				IClass type = (IClass) ProjectAccessorFacade.findElement(fullName);
				if (IDLUtils.isCustomType(fullName) && type != null) {
					idls.add(getIncludedIDL(type));
					idls.addAll(getIncludedIDLs(type));
				}
			}

			for (IParameter parameter : operation.getParameters()) {
				IClass parameterType = parameter.getType();
				String typeModifier = parameter.getTypeModifier();
				String targetName = parameter.getName();
				String fullName = IDLUtils.getElementTypeFullName(parameterType, typeModifier,
						clazz, targetName);
				IClass type = (IClass) ProjectAccessorFacade.findElement(fullName);
				if (IDLUtils.isCustomType(fullName) && type != null) {
					idls.add(getIncludedIDL(type));
					idls.addAll(getIncludedIDLs(type));
				}
			}
		}
		return idls;
	}

	private String getIncludedIDL(IClass idlClass) {
		StringBuilder builder = new StringBuilder();
		String namespace = idlClass.getFullNamespace("/");
		if (StringUtils.isNotEmpty(namespace)) {
			builder.append(namespace);
			builder.append("/");
		}
		builder.append(getIDLFileName(idlClass));
		return builder.toString();
	}

	protected String getIDLFileName(IClass clazz) {
		return clazz.getName() + ".idl";
	}
}
