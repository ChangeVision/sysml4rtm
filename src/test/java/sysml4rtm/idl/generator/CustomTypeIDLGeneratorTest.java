package sysml4rtm.idl.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.AstahModelFinder;
import sysml4rtm.ProjectAccessorFacade;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;

public class CustomTypeIDLGeneratorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void should_generate_module_declaration_from_package_model() throws Exception {
		INamedElement element = getTarget("customtypeidl.asml", "jp::service::Block1");

		CustomTypeIDLGenerator generator = new CustomTypeIDLGenerator(null);

		StringBuilder actual = new StringBuilder();
		actual.append(generator.getIDLModuleDeclare((IClass) element));
		actual.append(generator.getIDLModuleDeclareClose((IClass) element));

		StringBuilder expected = new StringBuilder();
		expected.append("module jp{").append(SystemUtils.LINE_SEPARATOR);
		expected.append("    module service{");
		expected.append("    };").append(SystemUtils.LINE_SEPARATOR);
		expected.append("};");

		assertThat(actual.toString(), is(expected.toString()));
	}

	@Test
	public void should_not_generate_module_declaration_from_default_package_model()
			throws Exception {
		INamedElement element = getTarget("customtypeidl.asml", "WithoutPackage");

		CustomTypeIDLGenerator generator = new CustomTypeIDLGenerator(null);

		StringBuilder actual = new StringBuilder();
		actual.append(generator.getIDLModuleDeclare((IClass) element));
		actual.append(generator.getIDLModuleDeclareClose((IClass) element));

		assertThat(actual.toString(), is(""));
	}

	@Test
	public void should_generate_customtype_basic_struct() throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml", "WithoutPackage");
		String actual = getFileContents(pathToOutput + "/WithoutPackage.idl");
		assertEquals(getWorkspaceFileContents("expected_idls/expected_simple_customtype.idl"),
				actual);
	}

	@Test
	public void should_generate_customtype_basic_struct_with_module_declaration() throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml", "jp::service::Block1");
		String actual = getFileContents(pathToOutput + "/jp/service/Block1.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_simple_customtype_with_module.idl"),
				actual);
	}

	@Test
	public void should_generate_attribute_using_sysml_native_type() throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml",
				"ValueType_SysMLNativeType");
		String actual = getFileContents(pathToOutput + "/ValueType_SysMLNativeType.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_customtype_attribute_using_sysml_native.idl"),
				actual);
	}

	@Test
	public void should_generate_attribute_using_sysml_native_type_and_define_multiplicity()
			throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml",
				"ValueType_SysMLNativeType_Multiplicity");
		String actual = getFileContents(pathToOutput
				+ "/ValueType_SysMLNativeType_Multiplicity.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_customtype_attribute_using_sysml_native_multiplicity.idl"),
				actual);
	}

	@Test
	public void should_generate_comment() throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml", "ValueType_Comment");
		String actual = getFileContents(pathToOutput + "/ValueType_Comment.idl");
		assertEquals(getWorkspaceFileContents("expected_idls/expected_valuetype_comment.idl"),
				actual);
	}

	@Test
	public void should_generate_nested_customtype_using_properties() throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml", "ValueType_Nest");

		assertThat(new File(pathToOutput + "/ValueType_Nest.idl").exists(), is(true));
		assertThat(new File(pathToOutput + "/ValueType_Nest_C1.idl").exists(), is(true));
		assertThat(new File(pathToOutput + "/ValueType_Nest_C2.idl").exists(), is(true));

		assertEquals(getWorkspaceFileContents("expected_idls/expected_customtype_nest.idl"),
				getFileContents(pathToOutput + "/ValueType_Nest.idl"));
		assertEquals(getWorkspaceFileContents("expected_idls/expected_customtype_nest_c1.idl"),
				getFileContents(pathToOutput + "/ValueType_Nest_C1.idl"));
		assertEquals(getWorkspaceFileContents("expected_idls/expected_customtype_nest_c2.idl"),
				getFileContents(pathToOutput + "/ValueType_Nest_C2.idl"));
	}

	@Test
	public void should_generate_nested_customtype_using_assosiation() throws Exception {
		String pathToOutput = generateCustomTypeIdl("customtypeidl.asml",
				"ValueType_Nest_Assosiation");

		assertThat(new File(pathToOutput + "/ValueType_Nest_Assosiation.idl").exists(), is(true));
		assertThat(new File(pathToOutput + "/ValueType_Nest_Assosiation_C1.idl").exists(), is(true));
		assertThat(new File(pathToOutput + "/ValueType_Nest_Assosiation_C2.idl").exists(), is(true));

		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_customtype_nest_assosiation.idl"),
				getFileContents(pathToOutput + "/ValueType_Nest_Assosiation.idl"));
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_customtype_nest_assosiation_c1.idl"),
				getFileContents(pathToOutput + "/ValueType_Nest_Assosiation_C1.idl"));
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_customtype_nest_assosiation_c2.idl"),
				getFileContents(pathToOutput + "/ValueType_Nest_Assosiation_C2.idl"));
	}

	@Test
	public void should_generate_customtype_using_idltype() throws Exception {
		String pathToOutput = generateCustomTypeIdl("using_builtin_type.asml", "UsingIdlType");
		String actual = getFileContents(pathToOutput + "/UsingIdlType.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_using_idl_type.idl"),
				actual);
	}
	
	@Test
	public void should_generate_customtype_using_rtctype() throws Exception {
		String pathToOutput = generateCustomTypeIdl("using_builtin_type.asml", "UsingRTCType");
		String actual = getFileContents(pathToOutput + "/UsingRTCType.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_using_rtc_type.idl"),
				actual);
	}
	
	@Test
	public void should_generate_customtype_using_seqtype() throws Exception {
		String pathToOutput = generateCustomTypeIdl("using_builtin_type.asml", "UsingSeqType");
		String actual = getFileContents(pathToOutput + "/UsingSeqType.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_using_seq_type.idl"),
				actual);
	}
	
	@Test
	public void should_generate_customtype_using_corbasequence() throws Exception {
		String pathToOutput = generateCustomTypeIdl("using_builtin_type.asml", "CustomSeq");
		String actual = getFileContents(pathToOutput + "/CustomSeq.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_corbasequence.idl"),
				actual);
		
		actual = getFileContents(pathToOutput + "/DataA.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_corbasequence_dataa.idl"),
				actual);
		
		actual = getFileContents(pathToOutput + "/DataB.idl");
		assertEquals(
				getWorkspaceFileContents("expected_idls/expected_corbasequence_datab.idl"),
				actual);

	}
	
	private String generateCustomTypeIdl(String pathToModelFile, String modelFullName) throws Exception{
		INamedElement element = getTarget(pathToModelFile, modelFullName);
		CustomTypeIDLGenerator generator = new CustomTypeIDLGenerator(folder.getRoot().getPath());

		generator.generateCustomTypeIdl((IClass) element, "UTF-8");
		return folder.getRoot().getPath();
	}

	private String getWorkspaceFileContents(String pathToFile) {
		URL resource = this.getClass().getResource(pathToFile);
		if (resource == null)
			throw new AssertionFailedError("Missing resource : " + pathToFile);
		return getFileContents(resource.getPath());
	}

	private String getFileContents(String pathToFile) {
		try {
			return FileUtils.readFileToString(new File(pathToFile));
		} catch (IOException e) {
			throw new AssertionFailedError(e.getMessage());
		}
	}

	private INamedElement getTarget(String pathToModelFile, String modelFullName) throws Exception {
		AstahModelFinder.open(this.getClass().getResourceAsStream(pathToModelFile));
		INamedElement element = ProjectAccessorFacade.findElement(modelFullName);
		return element;
	}

}
