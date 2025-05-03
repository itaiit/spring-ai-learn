package top.itaiit.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;

import java.util.HashMap;
import java.util.Map;

public class ReReadingAdvisor implements CallAroundAdvisor {
    private final Logger logger = LoggerFactory.getLogger(ReReadingAdvisor.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put("re2_input_query", advisedRequest.userText());
        AdvisedRequest request = AdvisedRequest.from(advisedRequest)
                .userText(
                        """
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """
                )
                .userParams(advisedUserParams)
                .build();
        logger.info("ReReadingAdvisor request: {}", request);
        AdvisedResponse advisedResponse = chain.nextAroundCall(request);
        logger.info("ReReadingAdvisor aroundCall Request: {}", advisedResponse);
        return advisedResponse;
    }
}
