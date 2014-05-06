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
 * Parameters to determine how we will send the mail
 */
public class MailHeaders {

    final String messageIdDomain;
    final String userAgent;
    final String from;
    final String subject;

    /**
     * @param messageIdDomain The Message-ID will have this format: &lt;12345678.12345678@messageIdDomain&gt;
     * @param userAgent
     * @param from The e-mail address which will be the value of the From: header in the mail. Can be of the format "john@doe.com" or
     *            "John Doe &lt;john@doe.com&gt;".
     * @param subject the subject of the mail
     */
    public MailHeaders(String messageIdDomain, String userAgent, String from, String subject) {
        this.messageIdDomain = messageIdDomain;
        this.userAgent = userAgent;
        this.from = from;
        this.subject = subject;
    }

    @Override
    public String toString() {
        return MailHeaders.class.getSimpleName() + " [messageIdDomain=" + messageIdDomain + ", userAgent=" + userAgent + ", from=" + from + ", subject =  "
                + subject + "]";
    }

}