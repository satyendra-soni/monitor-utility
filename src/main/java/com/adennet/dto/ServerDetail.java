package com.adennet.dto;

import lombok.Data;

@Data
public class ServerDetail {
    private int port;
    private String hostName;
    private String userName;
    private String userPassword;
}
