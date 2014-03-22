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

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import ca.rmen.carmailer.Mail.Body;

/**
 * This class requires the JavaMail and JSoup libraries.
 * 
 * Send a mail in HTML or plain text format to a list of recipients.
 */
class CarMailer {

    private static final String TAG = CarMailer.class.getSimpleName();


    /**
     * Send a mail to a list of recipients. One mail will be sent to each recipient. The recipient will be on the To: field of the mail.
     * 
     * @param credientials the authentication parameters for the SMTP server
     * @param mail the mail to be sent
     * @param sendOptions settings for sending the mails.
     */
    static void sendEmail(final SmtpCredentials credentials, Mail mail, SendOptions sendOptions) {

        // Set up properties for mail sending.
        Properties props = new Properties();
        props.put("mail.smtp.host", credentials.serverName);
        props.put("mail.smtp.port", credentials.port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", String.valueOf(Log.LOGGER.isLoggable(Level.FINER)));
        props.put("mail.transport.protocol", "smtp");
        Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(credentials.userName, credentials.password);
            }
        });


        int i = 0;
        // Send one mail to each recipient.
        for (Recipient recipient : mail.recipients) {
            try {
                Log.i(TAG, "Sending to " + (++i) + ": " + recipient.address + ".");

                Message message = createMessage(mailSession, recipient, mail.from, mail.subject, mail.body);

                if (sendOptions.outputFolder != null) {
                    File file = new File(sendOptions.outputFolder, recipient.address + ".eml");
                    FileOutputStream os = new FileOutputStream(file);
                    message.writeTo(os);
                    os.close();
                }
                // Send the mail.
                if (!sendOptions.dryRun) {
                    Transport transport = mailSession.getTransport();
                    transport.connect();
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                    if (i % sendOptions.maxMailsPerBatch == 0 && i < mail.recipients.size()) {
                        Log.i(TAG, "Sleeping for " + sendOptions.delayBetweenBatches + " seconds...");
                        Thread.sleep(sendOptions.delayBetweenBatches * 1000);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Could not send mail to " + recipient + ": " + e.getMessage(), e);
                e.printStackTrace(); // Why doesn't this show up in the logs?
                break;
            }
        }
    }

    /**
     * Create a message to be sent using the JavaMail api.
     * 
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    private static Message createMessage(Session mailSession, Recipient to, String from, String subject, Body body) throws UnsupportedEncodingException,
            MessagingException {

        Log.i(TAG, "Create message for " + to);
        String bodyText = body.text;
        String bodyHtml = body.html;
        for (int tagIndex = 0; tagIndex < to.tags.length; tagIndex++) {
            bodyText = bodyText.replaceAll("%" + (tagIndex + 1), to.tags[tagIndex]);
            if (bodyHtml != null) bodyHtml = bodyHtml.replaceAll("%" + (tagIndex + 1), to.tags[tagIndex]);
        }
        MimeMessage message = new MimeMessage(mailSession);

        // Set the subject, from, and to fields.
        message.setSubject(MimeUtility.encodeText(subject, body.charset.name(), "Q"));
        message.setFrom(new InternetAddress(from));
        message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to.address));

        // Construct the mail.
        // If we have both html and text, do a multipart mail with two bodyparts
        if (body.html != null && !body.html.isEmpty()) {

            Multipart mp = new MimeMultipart("alternative");
            // Add the plain text version of the mail
            BodyPart bp = new MimeBodyPart();
            bp.setContent(bodyText, "text/plain;charset=" + body.charset);
            bp.setHeader("Content-Transfer-Encoding", "quoted-printable");
            mp.addBodyPart(bp);
            // Add the html version of the mail
            bp = new MimeBodyPart();
            bp.setContent(bodyHtml, "text/html;charset=" + body.charset);
            bp.setHeader("Content-Transfer-Encoding", "quoted-printable");
            mp.addBodyPart(bp);
            message.setContent(mp);
        } else {
            // Just a plain text mail
            message.setHeader("Content-Transfer-Encoding", "quoted-printable");
            message.setText(bodyText, body.charset.name());
        }
        return message;
    }

}
