/*
 * ----------------------------------------------------------------------------
 * "THE WINE-WARE LICENSE" Version 1.0:
 * Authors: Carmen Alvarez. 
 * As long as you retain this notice you can do whatever you want with this stuff. 
 * If we meet some day, and you think this stuff is worth it, you can buy me a 
 * glass of wine in return. 
 * 
 * THE AUTHORS OF THIS FILE ARE NOT RESPONSIBLE FOR LOSS OF LIFE, LIMBS, SELF-ESTEEM,
 * MONEY, RELATIONSHIPS, OR GENERAL MENTAL OR PHYSICAL HEALTH CAUSED BY THE
 * CONTENTS OF THIS FILE OR ANYTHING ELSE.
 * ----------------------------------------------------------------------------
 */
package ca.rmen.carmailer;

/**
 * The parameters required to authenticate on an SMTP server.
 */
public class SmtpCredentials {
    final String serverName;
    final int port;
    final String userName;
    final String password;

    /**
     * @param serverName the IP address or domain name of the SMTP server
     * @param port The port of the SMTP server.
     * @param userName The username of the SMTP account.
     * @param password The password of the SMTP account.
     */
    public SmtpCredentials(String serverName, int port, String userName, String password) {
        this.serverName = serverName;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public String toString() {
        return SmtpCredentials.class.getSimpleName() + " [serverName=" + serverName + ", port=" + port + ", userName=" + userName + ", password=******]";
    }

}