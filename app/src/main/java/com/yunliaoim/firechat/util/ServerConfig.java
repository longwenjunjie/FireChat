package com.yunliaoim.firechat.util;

import com.yunliaoim.firechat.utils.util.PreferenceHelper;

public class ServerConfig {
    private static final String SERVER_IP = "fc_server_ip";
    private static final String SERVER_PORT = "fc_server_port";
    private static final String SERVER_DOMAIN = "fc_server_domain";

    public static void setIP(String ip) {
        PreferenceHelper.putString(SERVER_IP, ip);
    }

    public static String getIP() {
        return PreferenceHelper.getString(SERVER_IP);
    }

    public static void setPort(int port) {
        PreferenceHelper.putInt(SERVER_PORT, port);
    }

    public static int getPort() {
        return PreferenceHelper.getInt(SERVER_PORT);
    }

    public static void setDomain(String domain) {
        PreferenceHelper.putString(SERVER_DOMAIN, domain);
    }

    public static String getDomain() {
        return PreferenceHelper.getString(SERVER_DOMAIN);
    }

}
