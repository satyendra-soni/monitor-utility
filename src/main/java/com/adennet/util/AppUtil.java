package com.adennet.util;

import com.adennet.dto.MatchResult;
import com.adennet.dto.ServerDetail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.adennet.util.Constant.FILESYSTEM;
import static com.adennet.util.Constant.MOUNTED;

//@Slf4j
public class AppUtil {

    public static Instant parseDate(String dateString){
        return Instant.parse(dateString.replace(" ", "T").concat("Z"));
    }
    @SneakyThrows
    public static Date parseDateTime(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date date;
        if (dateString.matches(".*\\.\\d{3}$")) {
            date = dateFormat.parse(dateString);
        } else if (dateString.matches(".*\\.\\d{6}$")) {
            date = dateFormat3.parse(dateString);
        } else {
            date = dateFormat2.parse(dateString);
        }
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

    public static void writeObjectToJsonFile(ObjectMapper objectMapper,Object object, String fileName) {
        if (Objects.isNull(object))
            return;
        try {
            String jsonString = objectMapper.writeValueAsString(object);
            File file = new File(fileName);
            FileUtils.writeStringToFile(file, jsonString, StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
