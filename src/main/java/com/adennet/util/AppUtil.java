package com.adennet.util;

import com.adennet.dto.ServerDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.adennet.util.Constant.FILESYSTEM;
import static com.adennet.util.Constant.MOUNTED;

@Slf4j
public class AppUtil {
    public static JsonNode convertToJson(ObjectMapper mapper, String data) {
        try {
            return mapper.readValue(data, JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Exception while converting to json");
        }
    }

    @SneakyThrows
    public static Date parseDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = dateFormat.parse(dateString);
        return new Date(date.getTime());
    }

    @SneakyThrows
    public static Map<String, String> getDiskDetails(String server, ServerDetail serverDetail) {
        // Create a new JSch session
        JSch jsch = new JSch();
        Session session = jsch.getSession(serverDetail.getUserName(), serverDetail.getHostName(), serverDetail.getPort());
        session.setPassword(serverDetail.getUserPassword());

        // Set the SSH configuration
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        // Connect to the server
        session.connect();

        // Create a new SSH channel
        ChannelExec channel = (ChannelExec) session.openChannel("exec");

        // Set the command to execute
        channel.setCommand("df -h /");

        // Get the input and output streams
        InputStream in = channel.getInputStream();
        OutputStream out = channel.getOutputStream();

        // Connect the channel
        channel.connect();

        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String[] header = reader.readLine().split("\\s+");
        String[] values = reader.readLine().split("\\s+");

        Map<String, String> diskDetails = IntStream.range(0, header.length - 1)
                .boxed()
                .collect(Collectors.toMap(i -> header[i], i -> getNumericValue(header[i], values[i])));
        diskDetails.put("server", server.concat(":: " + serverDetail.getHostName()));

        // Disconnect the channel and session
        reader.close();
        channel.disconnect();
        session.disconnect();
        return diskDetails;
    }

    private static String getNumericValue(String key, String number) {
        if (key.equals(MOUNTED) || key.equals(FILESYSTEM))
            return number;
        return number.replaceAll("\\D", "");
    }

}
