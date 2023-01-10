package com.adennet.util;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.api.ApiTestResponse;
import com.slack.api.status.v2.StatusApiException;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;

import java.io.IOException;
public class Main {
  public static void main(String[] args) throws IOException, SlackApiException, StatusApiException {
      String data="[\n" +
              "    {\n" +
              "        \"server\": \"server-1:: 10.6.0.1\",\n" +
              "        \"Avail\": \"52\",\n" +
              "        \"Used\": \"42\",\n" +
              "        \"Size\": \"98\",\n" +
              "        \"Use%\": \"45\",\n" +
              "        \"Mounted\": \"/\",\n" +
              "        \"Filesystem\": \"/dev/mapper/ubuntu--vg-ubuntu--lv\"\n" +
              "    }\n" +
              "]";
      Payload payload= Payload.builder()
              .text(data)
              .build();
      Slack slack = Slack.getInstance();
      String url="https://hooks.slack.com/services/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";
      WebhookResponse codeTest = slack.send(url, payload);
      System.out.println("codeTest = " + codeTest);
  }
}