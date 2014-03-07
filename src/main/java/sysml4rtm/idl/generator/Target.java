package sysml4rtm.idl.generator;

import com.change_vision.jude.api.inf.model.INamedElement;

public class Target {

	private INamedElement targetModel;
	private String pathToOutput;

	public Target(INamedElement targetModel, String pathToOutput) {
		this.targetModel = targetModel;
		this.pathToOutput = pathToOutput;
	}

	public INamedElement getTargetModel() {
		return targetModel;
	}

	public String getPathToOutput() {
		return pathToOutput;
	}

}
