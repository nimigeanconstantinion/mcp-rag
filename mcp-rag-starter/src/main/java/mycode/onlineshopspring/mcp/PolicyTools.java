package mycode.onlineshopspring.mcp;

import mycode.onlineshopspring.policies.PolicySearchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.rag.enabled", havingValue = "true")
public class PolicyTools {

    private final PolicySearchService policySearchService;

    public PolicyTools(PolicySearchService policySearchService) {
        this.policySearchService = policySearchService;
    }
}
