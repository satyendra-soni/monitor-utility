package com.adennet.util;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.api.ApiTestResponse;
import com.slack.api.status.v2.StatusApiException;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import org.springframework.util.Base64Utils;

import java.io.IOException;
public class Main {
  public static void main(String[] args) throws IOException, SlackApiException, StatusApiException {
      String data="jdbc:postgresql://10.6.0.4:7432/billinghub_usage_management_db";
      byte[] encode = Base64Utils.encode(data.getBytes());
      System.out.println("new String(encode) = " + new String(encode));
  }
}