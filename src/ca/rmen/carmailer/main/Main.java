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
package ca.rmen.carmailer.main;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;

import ca.rmen.carmailer.CarMailer;
import ca.rmen.carmailer.Mail;
import ca.rmen.carmailer.Mail.Body;
import ca.rmen.carmailer.Parser;
import ca.rmen.carmailer.Parser.BodyType;
import ca.rmen.carmailer.Recipient;
import ca.rmen.carmailer.SendOptions;
import ca.rmen.carmailer.SmtpCredentials;

/**
 * Command-line entry point to CarMailer.
 */
public class Main {

    /**
     * Reads arguments from the command-line and invokes CarMailer to send a mail to various recipients.
     * Provide no arguments (or any invalid argument) to see the usage.
     */
    public static void main(String[] args) throws IOException {
        // Read the arguments given on the command line
        final int required_arguments_length = 6;
        if (args.length < required_arguments_length) usage();
        int i = 0;

        BodyType bodyType = BodyType.AUTO;
        Charset charset = null;
        String from = null;
        File outputFolder = null;
        int maxMailsPerBatch = 100;
        int delayBetweenBatchesS = 60 * 60; // 1 hour
        String statusEmailAddress = null;
        String messageIdDomain = InetAddress.getLocalHost().getHostName();
        boolean dryRun = false;
        String password = null;
        for (i = 0; i < args.length - required_arguments_length; i++) {
            if (args[i].equals("--password")) {
                password = args[++i];
            } else if (args[i].equals("--body-type")) {
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
            } else if (args[i].equals("--from")) {
                from = args[++i];
            } else if (args[i].equals("--output-folder")) {
                String outputFolderName = args[++i];
                outputFolder = new File(outputFolderName);
                if (!outputFolder.isDirectory() && !outputFolder.mkdir()) {
                    System.err.println(outputFolderName + " does not exist and cannot be created");
                    System.exit(1);
                }
            } else if (args[i].equals("--dry-run")) {
                dryRun = true;
            } else if (args[i].equals("--batch-size")) {
                maxMailsPerBatch = Integer.valueOf(args[++i]);
            } else if (args[i].equals("--batch-delay")) {
                delayBetweenBatchesS = Integer.valueOf(args[++i]);
            } else if (args[i].equals("--send-progress")) {
                statusEmailAddress = args[++i];
            } else if (args[i].equals("--domain")) {
                messageIdDomain = args[++i];
            } else {
                break;
            }
        }
        // We've gone through all the optional arguments, make sure
        // we have all the required ones.
        if (args.length - i != required_arguments_length) usage();
        String serverName = args[i++];
        int port = Integer.valueOf(args[i++]);
        String userName = args[i++];
        if (password == null) {
            System.out.print("SMTP password: ");
            password = new String(System.console().readPassword());
        }
        SmtpCredentials credentials = new SmtpCredentials(serverName, port, userName, password);
        String recipientsFilePath = args[i++];
        String subject = args[i++];
        String bodyFilePath = args[i++];

        if (from == null) from = userName;

        // Parse the mail body.
        Body body = Parser.parse(bodyFilePath, bodyType, charset);

        // Read the file with the list of e-mail addresses
        List<Recipient> recipients = Parser.parseRecipients(recipientsFilePath, body.charset);
        Mail mail = new Mail(from, recipients, subject, body);
        SendOptions sendOptions = new SendOptions(dryRun, outputFolder, statusEmailAddress, maxMailsPerBatch, delayBetweenBatchesS);
        CarMailer.sendEmail(credentials, mail, sendOptions, messageIdDomain);
    }

    /**
     * Print the required arguments and help to stderr and exit.
     */
    private static void usage() {
        System.err.println("Send an html file by e-mail to a list of recipients.");
        System.err.println();
        System.err.println("Usage: " + getProgramName() + " [options] <smtp server> <smtp port> <username> <recipients file> <subject> <body file>");
        System.err.println("options:");
        System.err.println("--password <password>: The password for the SMTP server.  If not given here, you will be prompted to enter the password.");
        System.err.println("--from <from>: the value of the From: field.  By default, the username is used.");
        System.err.println("--dry-run: if true, no mail will actually be sent.");
        System.err.println("--body-type <html|text|auto>: Default is auto.");
        System.err.println("--batch-size <n>: send at most n mails in a batch. Default: 100 mails");
        System.err.println("--batch-delay <s>: wait s seconds between sending batches. Default: 3600s (1 hour)");
        System.err.println("--send-progress <email address>: Send the progress at the end of each batch, and end status to this e-mail address");
        System.err.println("--output-folder <path>: if specified, each mail will be written to a file in this folder");
        System.err
                .println("--charset <charset>: specify the charset for reading and writing. By default the charset is guessed from the content of the file or the http-equiv meta tag in the html file.");
        System.err.println();
        System.err.println("*recipients file*: must be a text file containing one e-mail address per line.");
        System.err.println("*body file*: the body of the mail in html or text format.");
        System.exit(1);
    }

    /**
     * Try to return a string like "java -jar bin/carmailer.jar" to be used within the usage.
     */
    private static String getProgramName() {
        try {
            ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
            if (protectionDomain != null) {
                CodeSource codeSource = protectionDomain.getCodeSource();
                if (codeSource != null) {
                    URL location = codeSource.getLocation();
                    if (location != null) {
                        String path = location.getPath();
                        if (path != null) {
                            File file = new File(path);
                            if (file.isFile()) {
                                String relativeFile = new File(".").toURI().relativize(file.toURI()).getPath();
                                return "java -jar " + relativeFile;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            // don't really care
        }
        return "java " + Main.class.getName();
    }
}