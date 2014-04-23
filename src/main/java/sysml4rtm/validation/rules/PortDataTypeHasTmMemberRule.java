package sysml4rtm.validation.rules;

import sysml4rtm.Messages;
import sysml4rtm.constants.Constants;
import sysml4rtm.utils.ModelUtils;
import validation.ValidationError;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IAttribute;
import com.change_vision.jude.api.inf.model.IBlock;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IFlowProperty;
import com.change_vision.jude.api.inf.model.IItemFlow;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPort;

public class PortDataTypeHasTmMemberRule extends DefaultValidationRule {


	@Override
	public boolean isTargetModel(INamedElement target) {
		return ModelUtils.isPart(target);
	}

	@Override
	public boolean validateRule(INamedElement target) throws InvalidUsingException {
		IAttribute part = (IAttribute)target;
		IBlock block = (IBlock) part.getType();
		for (IPort port : block.getPorts()) {
			if(ModelUtils.hasServiceInterface(port))
				continue;
			
			IClass dataType;
			if(hasItemFlow(port)){
				dataType = ModelUtils.getConveyDataType(port.getItemFlows()[0]);
			}else{
				if(!hasFlowProperties(port))
					continue;
				
				IBlock type = (IBlock) port.getType();
				dataType = type.getFlowProperties()[0].getType();
			}
			
			if(dataType == null)
				continue;
			
			if(!hasTmMember(dataType)){
				setResult(new ValidationError(Messages.getMessage(
						"error.port_type_must_have_tm_member", ModelUtils.getPartName(part),
						ModelUtils.getPortName(port),dataType.getFullName(Constants.MODEL_NAMESPACE_SEPARATOR)),part,this));
				return false;
			}
			
		}
		return true;
	}

	private boolean hasTmMember(IClass type){
		for(IAttribute attr : type.getAttributes()){
			String attrTypeName = attr.getType().getFullName(Constants.MODEL_NAMESPACE_SEPARATOR);
			
			if(attr.getName().equals("tm") && attrTypeName.equals("RTC::Time")){
				return true;
			}
		}
		return false;
	}
	private boolean hasFlowProperties(IPort port) {
		IBlock type = (IBlock) port.getType();
		if(type == null)
			return false;
		
		IFlowProperty[] flowProperties = type.getFlowProperties();
		return flowProperties != null && flowProperties.length > 0;
	}

	private boolean hasItemFlow(IPort port) {
		IItemFlow[] itemFlows = port.getItemFlows();
		return itemFlows != null && itemFlows.length > 0;
	}

}
