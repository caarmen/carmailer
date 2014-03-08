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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import ca.rmen.carmailer.Parser.Body;
import ca.rmen.carmailer.Parser.BodyType;

/**
 * This class requires the JavaMail and JSoup libraries.
 * 
 * Send a mail in HTML format to a list of recipients.
 * 
 * The mail will be sent as a multi-part mail with two body parts: one body part for the plain text version of the body, one body part with the html version.
 */
public class CarMailer {

    private static final String TAG = CarMailer.class.getSimpleName();
    // To avoid being detected as spam, don't send too many mails too quickly:
    // Send mail in batches: Send at most MAX_MAILS_PER_BATCH consecutive mails, and sleep 
    // DELAY_BETWEEN_BATCHES_S seconds between batches.
    private static final int MAX_MAILS_PER_BATCH = 100;
    private static final int DELAY_BETWEEN_BATCHES_S = 60 * 60; // one hour

    public static void main(String[] args) throws IOException {
        Log.init();
        // Read the arguments given on the command line
        final int required_arguments_length = 7;
        if (args.length < required_arguments_length) usage();
        int i = 0;

        BodyType bodyType = BodyType.AUTO;
        Charset charset = null;
        for (i = 0; i < args.length - required_arguments_length; i++) {
            if (args[i].equals("--body-type")) {
                try {
                    bodyType = BodyType.valueOf(args[++i].toUpperCase());
                } catch (IllegalArgumentException e) {
                    usage();
                }
            } else if (args[i].equals("--charset")) {
                String charsetName = args[++i];
                try {
                    charset = Charset.forName(charsetName);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid charset " + charset);
                    System.exit(1);
                }
            } else {
                break;
            }
        }
        // We've gone through all the optional arguments, make sure
        // we have all the required ones.
        if (args.length - i != required_arguments_length) usage();
        String smtpServer = args[i++];
        int smtpPort = Integer.valueOf(args[i++]);
        String userName = args[i++];
        String password = args[i++];
        String recipientsFilePath = args[i++];
        String subject = args[i++];
        String bodyFilePath = args[i++];

        // Read the file with the list of e-mail addresses
        List<String> recipients = IOUtils.readLines(recipientsFilePath);

        // Parse the mail body.
        Body body = Parser.parse(bodyFilePath, bodyType, charset);
        sendEmail(smtpServer, smtpPort, userName, password, recipients, subject, body);
    }

    /**
     * Print the required arguments and help to stderr and exit.
     */
    private static void usage() {
        System.err.println("Send an html file by e-mail to a list of recipients.");
        System.err.println();
        System.err.println("Usage: " + CarMailer.class.getSimpleName()
                + " [options] <smtp server> <smtp port> <username> <password> <recipients file> <subject> <body file>");
        System.err.println("options:");
        System.err.println("--body-type <html|text|auto>: default is auto");
        System.err
                .println("--charset <charset>: specify the charset for reading and writing. By default the charset is guessed from the content of the file or the http-equiv meta tag in the html file.");
        System.err.println();
        System.err.println("*recipients file*: must be a text file containing one e-mail address per line.");
        System.err.println("*body file*: the body of the mail in html or text format.");
        System.exit(1);
    }


    /**
     * Send a mail to a list of recipients. One mail will be sent to each recipient. The recipient will be on the To: field of the mail.
     * 
     * @param smtpServer the address of the SMTP server.
     * @param smtpServerPort the port of the SMTP server.
     * @param userName the user name of the sender account on the SMTP server.
     * @param password the password of the sender account on the SMTP server.
     * @param recipients the list of recipients.
     * @param subject the subject of the mail
     * @param body the content of the mail
     */
    private static void sendEmail(String smtpServer, int smtpServerPort, final String userName, final String password, List<String> recipients, String subject,
            Body body) {

        // Set up properties for mail sending.
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpServerPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", String.valueOf(Log.LOGGER.isLoggable(Level.FINER)));
        props.put("mail.transport.protocol", "smtp");
        Session mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });


        int i = 0;
        // Send one mail to each recipient.
        for (String recipient : recipients) {
            try {
                Log.i(TAG, "Sending to " + (++i) + ": " + recipient);
                MimeMessage message = new MimeMessage(mailSession);

                // Set the subject, from, and to fields.
                message.setSubject(MimeUtility.encodeText(subject, body.charset.name(), "Q"));
                message.setFrom(new InternetAddress(userName));
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

                // Construct the mail.
                // If we have both html and text, do a multipart mail with two bodyparts
                if (body.html != null && !body.html.isEmpty()) {

                    Multipart mp = new MimeMultipart("alternative");
                    // Add the plain text version of the mail
                    BodyPart bp = new MimeBodyPart();
                    bp.setContent(body.text, "text/plain;charset=" + body.charset);
                    bp.setHeader("Content-Transfer-Encoding", "quoted-printable");
                    mp.addBodyPart(bp);
                    // Add the html version of the mail
                    bp = new MimeBodyPart();
                    bp.setContent(body.html, "text/html;charset=" + body.charset);
                    bp.setHeader("Content-Transfer-Encoding", "quoted-printable");
                    mp.addBodyPart(bp);
                    message.setContent(mp);
                } else {
                    // Just a plain text mail
                    message.setHeader("Content-Transfer-Encoding", "quoted-printable");
                    message.setText(body.text, body.charset.name());
                }

                // Send the mail.
                Transport transport = mailSession.getTransport();
                transport.connect();
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                if (i % MAX_MAILS_PER_BATCH == 0 && i < recipients.size()) {
                    Log.i(TAG, "Sleeping for " + DELAY_BETWEEN_BATCHES_S + " seconds...");
                    Thread.sleep(DELAY_BETWEEN_BATCHES_S * 1000);
                }

            } catch (Exception e) {
                Log.w(TAG, "Could not send mail to " + recipient, e);
                break;
            }
        }
    }

}