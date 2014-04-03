package sysml4rtm.validation;

import java.util.ArrayList;
import java.util.List;

import sysml4rtm.validation.rules.ConnectorMustAttachPortRule;
import sysml4rtm.validation.rules.DefineDirectionRule;
import sysml4rtm.validation.rules.MustDefinePortTypeRule;
import sysml4rtm.validation.rules.NotExistInOutFlowPropertyRule;
import sysml4rtm.validation.rules.NotExistInOutItemFlowPropertyRule;
import sysml4rtm.validation.rules.NotExistInconsitencyItemFlowAndFlowPropertyRelationRule;
import sysml4rtm.validation.rules.PartMustHaveTypeRule;
import sysml4rtm.validation.rules.PortDataTypeHasTmMemberRule;

public class ValidationRuleManager {

    private static ValidationRuleManager instance = null;
    private List<ValidationRule> rules = new ArrayList<ValidationRule>();

    private ValidationRuleManager() {
        setValidationRule();
    }

    public static ValidationRuleManager getInstance() {
        if (instance == null) {
            instance = new ValidationRuleManager();
        }
        return instance;
    }

    private void setValidationRule() {
    	rules.add(new ConnectorMustAttachPortRule());
    	rules.add(new DefineDirectionRule());
    	rules.add(new MustDefinePortTypeRule());
    	rules.add(new NotExistInconsitencyItemFlowAndFlowPropertyRelationRule());
    	rules.add(new NotExistInOutFlowPropertyRule());
    	rules.add(new NotExistInOutItemFlowPropertyRule());
    	rules.add(new PartMustHaveTypeRule());
    	rules.add(new PortDataTypeHasTmMemberRule());
    }

    public List<ValidationRule> getValidationRule() {
        return rules;
    }

    public boolean hasRule(@SuppressWarnings("rawtypes") Class rule) {
        for (ValidationRule r: rules) {
            if (r.getClass() == rule) {
                return true;
            }
        }
        return false;
    }

}
