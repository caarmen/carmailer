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
class Mail {
    final String from;
    final List<Recipient> recipients;
    final String subject;
    final Body body;

    Mail(String from, List<Recipient> to, String subject, Body body) {
        this.from = from;
        this.recipients = to;
        this.subject = subject;
        this.body = body;
    }

    static class Body {
        final String text;
        final String html;
        final Charset charset;

        Body(String text, String html, Charset charset) {
            this.text = text;
            this.html = html;
            this.charset = charset;
        }
    }
}