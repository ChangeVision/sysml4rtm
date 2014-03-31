package sysml4rtm.validation;

import java.util.ArrayList;
import java.util.List;

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
//    	rules.add(new BlockPortMustHaveFlowPropertyRule(generator));
//    	rules.add(new ConnectorMustAttachPortRule(generator));
//    	rules.add(new DefineDirectionRule(generator));
//    	rules.add(new NotExistDuplicatePartNameRule(generator));
//    	rules.add(new NotExistInconsitencyItemFlowAndFlowPropertyRelationRule(generator));
//    	rules.add(new NotExistInOutFlowPropertyRule(generator));
//    	rules.add(new NotExistInvalidLevelRelationForBlockRule(generator));
//    	rules.add(new NotExistInvalidLevelRelationForPartRule(generator));
//    	rules.add(new PartMustHaveTypeRule(generator));
//        rules.add(new PortMustHaveSingleConnectorRule(generator));
//        rules.add(new SimulinkValidIbdDiagramNameRule(generator));
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
