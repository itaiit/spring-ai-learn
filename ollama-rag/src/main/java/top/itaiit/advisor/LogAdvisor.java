package top.itaiit.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;

public class LogAdvisor implements CallAroundAdvisor {
    private Logger logger = LoggerFactory.getLogger(LogAdvisor.class);
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // Log the request
        logger.info("LogAdvisor aroundCall Request: {}", advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        // Log the response
        logger.info("LogAdvisor aroundCall returned: {}", advisedResponse);
        return advisedResponse;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
