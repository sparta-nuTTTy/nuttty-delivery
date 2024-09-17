package com.nuttty.eureka.ai.domain.service;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
@Transactional(readOnly = true)
@RefreshScope
public class SlackService {

    @Value("${slack.botKey}")
    private String slackToken;

    // 슬랙 고유 ID
    private String slackUserId = "U07MFBXHTG9";

    // webhook 이용 시 사용 가능, dm 불가
    final String webhookUrl = "https://hooks.slack.com/services/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";

    /**
     * 슬랙 봇으로 DM 보내기
     * @throws IOException
     * @throws SlackApiException
     */
    @Transactional
    public void sendMessage(String message, List<String> slackId) throws IOException, SlackApiException, URISyntaxException {
        Slack slack = Slack.getInstance();

        for (String slackUserId : slackId) {
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .token(slackToken)
                    .channel(slackUserId)  // Slack 사용자 ID
                    .text(message)
                    .build();

            ChatPostMessageResponse response = slack.methods().chatPostMessage(request);

            if (response.isOk()) {
                log.info("Message sent successfully to user: " + slackUserId);
            } else {
                log.error("Error sending message: " + response.getError());
            }
        }

    }

    /**
     * 슬랙 봇으로 DM 보내기
     * @param map
     * @throws IOException
     * @throws SlackApiException
     * @throws URISyntaxException
     */
    @Transactional
    public void sendMessageForMap(Map<String, String> map) throws IOException, SlackApiException, URISyntaxException {
        Slack slack = Slack.getInstance();

        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            String slackId = stringStringEntry.getKey();
            String message = stringStringEntry.getValue();

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .token(slackToken)
                    .channel(slackId)  // Slack 사용자 ID
                    .text(message)
                    .build();

            ChatPostMessageResponse response = slack.methods().chatPostMessage(request);

            if (response.isOk()) {
                log.info("Message sent successfully to user: " + slackUserId);
            } else {
                log.error("Error sending message: " + response.getError());
            }
        }

    }
}
