package sysml4rtm.idl.generator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import sysml4rtm.Messages;
import sysml4rtm.ProjectAccessorFacade;
import sysml4rtm.constants.Constants;
import sysml4rtm.exception.ApplicationException;

import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IMultiplicityRange;
import com.change_vision.jude.api.inf.model.INamedElement;

public class IDLUtils {

	public static String templatesPath = System.getProperty("user.home") + File.separator
			+ ".astah" + File.separator + "sysml" + File.separator;

	static {
		initVelocity();
	}

	private static void initVelocity() {
		Properties p = new Properties();
		p.setProperty("resource.loader", "FILE, CLASSPATH");
		p.setProperty("FILE.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		p.setProperty("FILE.resource.loader.path", templatesPath);
		p.setProperty("CLASSPATH.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty("directive.foreach.counter.name", "velocityCount");
		p.setProperty("directive.foreach.iterator.name", "velocityHasNext");
		p.setProperty("directive.foreach.counter.initial.value", "0");

		try {
			Velocity.init(p);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
	}

	public static String getSequenceTypeDefType(String typeModifier) {
		typeModifier = IDLUtils.upperCaseAfterSpace(typeModifier);
		String modifier = StringUtils.remove(typeModifier, " ");
		Matcher matcher = IDLUtils.sequencePattern.matcher(modifier);
		if (!matcher.matches()) {
			throw new IllegalStateException("must not happend");
		}
		String type = matcher.group(1);
		return IDLUtils.extractTypeName(StringUtils.capitalize(type) + "Seq");
	}

	public static String upperCaseAfterSpace(String s) {
		StringBuilder builder = new StringBuilder();
		char[] ca = s.toCharArray();
		for (int i = 0; i < ca.length; ++i) {
			builder.append(0 < i && Character.isWhitespace(ca[i - 1])
					&& Character.isLowerCase(ca[i]) ? Character.toUpperCase(ca[i]) : ca[i]);
		}
		return builder.toString();
	}

	public static Pattern sequencePattern = Pattern.compile("<([\\s\\w:]*)>");

	public static boolean isIDLPrimitiveType(String type) {
		String[] primitives = new String[] { "IDL::short", "IDL::long", "IDL::long long", "IDL::unsigned short",
				"IDL::unsigned long", "IDL::unsigned long long", "IDL::float", "IDL::double", "IDL::long double", "IDL::boolean",
				"IDL::char", "IDL::wchar", "IDL::octet", "IDL::string", "IDL::wstring", "IDL::sequence", "IDL::any", "IDL::fixed" };
		return ArrayUtils.contains(primitives, type);
	}

	public static boolean isIDLSequenceType(String type) {
		return StringUtils.equals(type, "sequence");
	}

	public static boolean isNavigable(String navigability) {
		return StringUtils.equals(navigability, "Navigable");
	}

	public static boolean isMultiple(IMultiplicityRange multiplicityRange) {
		int upper = multiplicityRange.getUpper();
		return upper != 0 && upper != 1 && upper != IMultiplicityRange.UNDEFINED;
	}

	public static String getElementTypeFullName(IClass type, String typeModifier, IClass clazz,
			String targetName) {
		return isIDLSequenceType(type.getName()) ? IDLUtils.getElementTypeOfSequence(typeModifier,
				clazz, targetName) : type.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR);
	}

	public static String getElementTypeOfSequence(String typeModifier, IClass clazz,
			String targetName) {
		String modifier = StringUtils.trim(typeModifier);
		Matcher matcher = sequencePattern.matcher(modifier);
		if (!matcher.matches()) {
			String message = Messages.getMessage("error.sequence.serviceinterface",
					clazz.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR), targetName, modifier);
			throw new ApplicationException(message);
		}

		return StringUtils.trim(matcher.group(1));
	}

	public static String extractTypeName(String fullName) {
		if (StringUtils.contains(fullName, Constants.MODEL_NAMESPACE_SEPARATOR)) {
			return StringUtils.substringAfterLast(fullName, Constants.MODEL_NAMESPACE_SEPARATOR);
		}
		return fullName;

	}

	public static String getAttributeName(IAttribute attribute, boolean isRecursive) {
		String name = attribute.getName();
		if (!StringUtils.isEmpty(name)) {
			return name;
		}
		String typeName = attribute.getType().getFullName(Constants.MODEL_NAMESPACE_SEPARATOR);
		name = StringUtils.uncapitalize(extractTypeName(typeName));
		/*
		 * 自クラスへの再帰でメンバ名を自動生成する場合、名前の衝突を避けるため、メンバ名の最後に"_"を付ける struct Class0{
		 * ^^^^^^ Class0Seq class0_; ^^^^^^ };
		 */
		return isRecursive ? name + "_" : name;
	}

	public static boolean isSysMLBuiltinType(String type){
		return StringUtils.startsWith(type, "SysML::");
	}
	
	public static boolean isCustomType(String type) {
		if (StringUtils.equals(type, "void") || isIDLPrimitiveType(type)
				|| isSysMLBuiltinType(type) || isRTCDataType(type)) {
			return false;
		}
		return true;
	}

	private static boolean isRTCDataType(String type) {
		if(StringUtils.startsWith(type, "RTC::")){
			INamedElement element = ProjectAccessorFacade.findElement(type);
			return element.hasStereotype(Constants.RTC_BUILTIN_TYPE);
		}
		return false;
	}

	public static boolean isOtherCustomTypeSequence(IClass clazz, IAttribute attribute) {
		IClass attributeClass = attribute.getType();
		if (isIDLSequenceType(attributeClass.getName())) {
			String elementTypeName = getElementTypeFullName(attributeClass,
					attribute.getTypeModifier(), clazz, attribute.getName());
			return isCustomType(elementTypeName)
					&& !StringUtils.equals(elementTypeName, clazz.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR));
		}
		return false;
	}

	public static String getPathToOutputWithPackage(String pathToOutput, IClass clazz) {
		StringBuilder sb = new StringBuilder();

		sb.append(pathToOutput);

		String packageName = clazz.getFullNamespace("/");
		if (StringUtils.isNotEmpty(packageName)) {
			sb.append(SystemUtils.FILE_SEPARATOR);
			sb.append(packageName);
		}

		return FilenameUtils.separatorsToSystem(sb.toString());
	}

	public static String saveTemplate(String pathToOutput, String fileName,
			String templateFileName, VelocityContext context, String encoding) {
		StringWriter writer = new StringWriter();
		String templateFileFullName = "templates/" + templateFileName;
		try {
			Velocity.mergeTemplate(templateFileFullName, encoding, context, writer);
		} catch (Exception e) {
			throw new ApplicationException(e);
		}

		File file = new File(pathToOutput + SystemUtils.FILE_SEPARATOR + fileName);
		String result = writer.toString();
		IOUtils.closeQuietly(writer);
		try {
			FileUtils.write(file, result);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		return result;
	}
}
