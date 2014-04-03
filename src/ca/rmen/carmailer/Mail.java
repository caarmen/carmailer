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

import java.nio.charset.Charset;
import java.util.List;

/**
 * The contents of a mail and basic headers (from, to, subject).
 */
public class Mail {
    public final String from;
    public final List<Recipient> recipients;
    public final String subject;
    public final Body body;

    /**
     * @param from The e-mail address which will be the value of the From: header in the mail. Can be of the format "john@doe.com" or
     *            "John Doe &lt;john@doe.com&gt;".
     * @param to The list of {@link Recipient} which will appear in the To: header in the mail.
     * @param subject The subject of the mail.
     * @param body The body of the mail.
     */
    public Mail(String from, List<Recipient> to, String subject, Body body) {
        this.from = from;
        this.recipients = to;
        this.subject = subject;
        this.body = body;
    }

    /**
     * The body content of the mail.
     * 
     */
    public static class Body {
        public final String text;
        public final String html;
        public final Charset charset;

        /**
         * @param text the mail body in plain text format. Must not be null.
         * @param html the mail body in HTML format. May be null.
         * @param charset the character set: must be the same for both the plain text and HTML formats.
         */
        public Body(String text, String html, Charset charset) { // NO_UCD (use default)
            this.text = text;
            this.html = html;
            this.charset = charset;
        }
    }
}