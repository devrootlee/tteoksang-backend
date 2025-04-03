package com.example.tteoksang.exception;

//{
//        "attachments": [
//        {
//        "pretext": "attachment block 위에 나타낼 text",
//        "fallback": "요청 실패 시 보낼 메시지",
//        "color": "#2eb886",
//        "author_name": "roseline",
//        "author_link": "https://github.com/roseline124",
//        "author_icon": "https://avatars.githubusercontent.com/u/41788121?s=460&u=ee6a6f6499aa68a23947cfb76d5e9cb6eebfd29c&v=4",
//        "title": "attachments 사용법",
//        "title_link": "https://api.slack.com/",
//        "text": "attachment block 안에 보여줄 메시지",
//        "fields": [
//        {
//        "title": "필드",
//        "value": "value",
//        "short": false
//        }
//        ],
//        "image_url": "http://my-website.com/path/to/image.jpg",
//        "thumb_url": "http://example.com/path/to/thumb.png",
//        "footer": "Slack API",
//        "footer_icon": "https://platform.slack-edge.com/img/default_application_icon.png",
//        "ts": 123456789
//        }
//        ]
//        }

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * attachments 구조
 * https://api.slack.com/reference/messaging/attachments
 */
@Component
public class SlackNotification {

    public void sendNotification(String statusCode, String exceptionReason){
        List<Map<String,Object>> attachments = new ArrayList<>();

        Map<String, Object> attachment = new HashMap<>();
        attachment.put("title", statusCode);
        attachment.put("text", exceptionReason);
        attachment.put("color", "#ff0000");
        attachments.add(attachment);

        // WebClient 사용하여 POST 요청 보내기
        WebClient webClient = WebClient.create();
        webClient.post()
                .uri("https://hooks.slack.com/services/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") // Webhook URL
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("attachments", attachments))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
