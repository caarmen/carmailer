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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class requires the JavaMail and JSoup libraries.
 * 
 * Send a mail in HTML format to a list of recipients.
 * 
 * The mail will be sent as a multi-part mail with two body parts: one body part for the plain text version of the body, one body part with the html version.
 */
public class CarMailer {

    private static final boolean DEBUG = true;
    // To avoid being detected as spam, don't send too many mails too quickly:
    // Send mail in batches: Send at most MAX_MAILS_PER_BATCH consecutive mails, and sleep 
    // DELAY_BETWEEN_BATCHES_S seconds between batches.
    private static final int MAX_MAILS_PER_BATCH = 100;
    private static final int DELAY_BETWEEN_BATCHES_S = 60 * 60; // one hour

    public static void main(String[] args) throws IOException {
        // Read the arguments given on the command line
        if (args.length != 7) usage();
        int i = 0;
        String smtpServer = args[i++];
        int smtpPort = Integer.valueOf(args[i++]);
        String userName = args[i++];
        String password = args[i++];
        String recipientsFilePath = args[i++];
        String subject = args[i++];
        String mailHTMLFilePath = args[i++];

        // Read the file with the list of e-mail addresses
        List<String> recipients = readLines(recipientsFilePath);

        // Parse the mail body.
        Document document = Jsoup.parse(new File(mailHTMLFilePath), null);
        String charset = document.outputSettings().charset().name();
        String htmlBody = document.outerHtml();
        String textBody = htmlToText(document);
        sendEmail(smtpServer, smtpPort, userName, password, recipients, subject, textBody, htmlBody, charset);
    }

    /**
     * Print the required arguments and help to stderr and exit.
     */
    private static void usage() {
        System.err.println("Send an html file by e-mail to a list of recipients.");
        System.err.println();
        System.err.println("Usage: " + CarMailer.class.getSimpleName()
                + " <smtp server> <smtp port> <username> <password> <recipients file> <subject> <html file>");
        System.err.println("*recipients file*: must be a text file containing one e-mail address per line.");
        System.err
                .println("*html file*: the body of the mail in html format. The file must contain an http-equiv meta tag which specifies the charset of the document.");
        System.exit(1);
    }

    /**
     * Convert an HTML document to text. Just calling {@link Document#text()} is not enough, as &lt;p&gt; and &lt;br&gt; elements will not be replaced with new
     * lines. This method replaces br elements with a single newline, and p elements with two newlines.
     * 
     * http://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
     * 
     * @return the content of the HTML document as plain text.
     * @throws IOException
     */
    private static String htmlToText(Document document) throws IOException {
        final String placeholder = "#OMGOMG#";

        // Replace all br's with a single placeholder
        Elements elements = document.getElementsByTag("br");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            element.text(placeholder);
        }

        // Replace all p's with a double placeholder
        elements = document.getElementsByTag("p");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            element.text(element.text() + placeholder + placeholder);
        }

        // Convert the html document to text and replace all the placeholders with newlines.
        String textBody = document.text();
        textBody = textBody.replaceAll(placeholder + " *", "\n");

        // Print the text version of the mail in debug mode.
        if (DEBUG) {
            PrintStream stdout = new PrintStream(System.out, true, "UTF-8");
            System.setOut(stdout);
            System.out.println(textBody);
        }
        return textBody;
    }

    /**
     * @return a list of lines in the given file. Lines which begin with # are considered to be commented out and are ignored
     * @throws IOException
     */
    private static List<String> readLines(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        List<String> result = new ArrayList<String>();
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            line = line.trim();
            if (line.startsWith("#")) continue;
            result.add(line);
        }
        reader.close();
        return result;
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
     * @param textBody the content of the mail as plain text.
     * @param htmlBody the content of the mail as HTML
     * @param charset the character set of both the HTML and plain text versions of the mail.
     */
    private static void sendEmail(String smtpServer, int smtpServerPort, final String userName, final String password, List<String> recipients, String subject,
            String textBody, String htmlBody, String charset) {

        // Set up properties for mail sending.
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpServerPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", DEBUG);
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
                System.out.println("Sending to " + (++i) + ": " + recipient);
                MimeMessage message = new MimeMessage(mailSession);

                // Set the subject, from, and to fields.
                message.setSubject(MimeUtility.encodeText(subject, charset, "Q"));
                message.setFrom(new InternetAddress(userName));
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

                // Construct the mail.
                Multipart mp = new MimeMultipart("alternative");
                // Add the plain text version of the mail
                BodyPart bp = new MimeBodyPart();
                bp.setContent(textBody, "text/plain;charset=" + charset);
                bp.setHeader("Content-Transfer-Encoding", "quoted-printable");
                mp.addBodyPart(bp);
                // Add the html version of the mail
                bp = new MimeBodyPart();
                bp.setContent(htmlBody, "text/html;charset=" + charset);
                bp.setHeader("Content-Transfer-Encoding", "quoted-printable");
                mp.addBodyPart(bp);
                message.setContent(mp);

                // Send the mail.
                Transport transport = mailSession.getTransport();
                transport.connect();
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
                if (i % MAX_MAILS_PER_BATCH == 0 && i < recipients.size()) {
                    System.out.println("Sleeping for " + DELAY_BETWEEN_BATCHES_S + " seconds...");
                    Thread.sleep(DELAY_BETWEEN_BATCHES_S * 1000);
                }

            } catch (Exception e) {
                System.err.println("Could not send mail to " + recipient);
                e.printStackTrace();
                break;
            }
        }
    }

}