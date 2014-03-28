package sysml4rtm.idl.generator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import sysml4rtm.ProjectAccessorFacade;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;

public class ServiceInterfaceIDLGeneratorTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
    @Test
    public void should_generate_module_declaration_from_package_model() throws Exception {
        INamedElement element = getTarget("service_interface_idl.asml", "jp::service::Interface1");

		ServiceInterfaceIDLGenerator generator = new ServiceInterfaceIDLGenerator(null);

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
    public void should_not_generate_module_declaration_from_default_package_model() throws Exception {
        INamedElement element = getTarget("service_interface_idl.asml", "WithoutPackage");
        ServiceInterfaceIDLGenerator generator = new ServiceInterfaceIDLGenerator(null);
        StringBuilder actual = new StringBuilder();
        actual.append(generator.getIDLModuleDeclare((IClass) element));
        actual.append(generator.getIDLModuleDeclareClose((IClass) element));

        assertEquals("", actual.toString());
    }

    @Test
    public void should_generate_operation_parameter_declaration() throws Exception {
        IClass serviceinterface  = (IClass) getTarget("service_interface_idl.asml", "jp::service::Interface1");
        
        IOperation operation = getOperation(serviceinterface.getOperations(), "op3");
        String actual = new ServiceInterfaceIDLGenerator(null).getOperationParameter(operation);
        String expected = "in RTC::TimedDouble param0, out double param1, inout string param2";
        assertEquals(expected, actual);
    }

    @Test
    public void should_generate_operation_parameter_declaration_using_customtype() throws Exception {
        IClass serviceinterface  = (IClass) getTarget("service_interface_idl_using_customtype.asml", "jp::service::Interface1");
        
        IOperation operation = getOperation(serviceinterface.getOperations(), "op3");
        String actual = new ServiceInterfaceIDLGenerator(null).getOperationParameter(operation);
        String expected = "in ::V0 param0, out jp::V1 param1, inout jp::service::V2 param2";
        assertEquals(expected, actual);
    }

    @Test
    public void should_generate_operation_parameter_using_typemodification() throws Exception {
        IClass serviceinterface  = (IClass) getTarget("service_interface_idl_using_customtype.asml", "jp::service::Interface1");
        
        IOperation operation = getOperation(serviceinterface.getOperations(), "op4");
        String actual =  new ServiceInterfaceIDLGenerator(null).getOperationParameter(operation);
        String expected = "in string<20> param0";
        assertEquals(expected, actual);
    }

    private IOperation getOperation(IOperation operations[], String operationName) {
        for (IOperation op : operations) {
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        return null;
    }

    @Test
    public void should_generate_idl_from_simple_interface() throws Exception {
        String pathToOutput = generateCustomTypeIdl("service_interface_idl.asml", "Interface0");
		String actual = getFileContents(pathToOutput + "/Interface0.idl");
		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_simple_serviceif.idl"),
				actual);
    }
    
    @Test
    public void should_generate_idl_from_interface_has_operations() throws Exception {
        String pathToOutput = generateCustomTypeIdl("service_interface_idl.asml", "jp::service::Interface1");
		String actual = getFileContents(pathToOutput + "/jp/service/Interface1.idl");
		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_operations.idl"),
				actual);
    }
    
    @Test
    public void should_generate_idl_returntype_or_parametertype_is_interface() throws Exception {
        String pathToOutput = generateCustomTypeIdl("service_interface_idl_using_customtype.asml", "jp::service::UsingInterface");
		String actual = getFileContents(pathToOutput + "/jp/service/UsingInterface.idl");
		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_using_interface.idl"),
				actual);
		
		// TODO should implement.
//		actual = getFileContents(pathToOutput + "/UsingInterfaceReturnType.idl");
//		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_using_interface_returntype.idl"),
//				actual);
//		
//		actual = getFileContents(pathToOutput + "/jp/UsingInterfaceParam.idl");
//		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_using_interface_parameter.idl"),
//				actual);
		
		
    }

    @Test
    public void should_generate_idl_from_interface_using_custom_datatype() throws Exception {
        String pathToOutput = generateCustomTypeIdl("service_interface_idl_using_customtype.asml", "jp::service::UsingCustomType");
		String actual = getFileContents(pathToOutput + "/jp/service/UsingCustomType.idl");
		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_using_customtype.idl"),
				actual);
    }
    

    @Test
    public void should_generate_idl_using_attribute() throws Exception {
        String pathToOutput = generateCustomTypeIdl("service_interface_idl_using_customtype.asml", "UsingAttribute");
		String actual = getFileContents(pathToOutput + "/UsingAttribute.idl");
		assertEquals(getWorkspaceFileContents("expected_serviceif_idls/expected_using_attribute.idl"),
				actual);
    }
    
    private String generateCustomTypeIdl(String pathToModelFile, String modelFullName) {
		INamedElement element = getTarget(pathToModelFile, modelFullName);
		Target target = new Target(element, folder.getRoot().getPath());
		ServiceInterfaceIDLGenerator generator = new ServiceInterfaceIDLGenerator(target);

		generator.generateIDL((IClass) element, "UTF-8");
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

	private INamedElement getTarget(String pathToModelFile, String modelFullName) {
		ProjectAccessorFacade.openProject(this.getClass().getResource(pathToModelFile).getPath());
		INamedElement element = ProjectAccessorFacade.findElement(modelFullName);
		if(element == null)
			throw new AssertionFailedError(String.format("missing %s.",modelFullName));
		return element;
	}
}
