package com.hisd3.hismk2.services

import com.hisd3.hismk2.domain.hrm.Employee
import io.ably.lib.rest.AblyRest
import io.ably.lib.rest.Auth
import io.ably.lib.rest.Channel
import io.ably.lib.types.AblyException
import io.ably.lib.types.ClientOptions
import org.springframework.stereotype.Service

@Service
class AblyNotificationService {

    // app.properties is not working. this is a temporary (but working) api key
    String apiKey = "Kmom-Q.yAzRkQ:F9LLtl6Nubj4CRkWT1EWxZs3Hq5mjVKpV98nCl1L0BI"

    Auth.TokenRequest generateTokenRequest() {
        try {
            // Initialize AblyRest with your API key
            ClientOptions options = new ClientOptions(apiKey);
            AblyRest ablyRest = new AblyRest(options);
            // Generate a token request
            Auth.TokenParams tokenParams = new Auth.TokenParams();

            Auth.TokenRequest tokenRequest = ablyRest.auth.createTokenRequest(tokenParams, null);
            return tokenRequest

        } catch (AblyException e) {
            e.printStackTrace();
        }

        return null;
    }

    void notifyEmployees(List<Employee> employees, String title, String data) throws AblyException {
        employees.each {employee ->
            def channelId = employee.id.toString()
            notifyChannel(channelId, title, data)
        }
    }

    void notifyChannel(String channelId, String title, String data) throws AblyException {
        try {
            ClientOptions options = new ClientOptions(apiKey);
            AblyRest ablyRest = new AblyRest(options);
            Channel channel = ablyRest.channels.get(channelId)
            channel.publish(title, data)
        } catch (AblyException e) {
            e.printStackTrace();
        }
    }
}
