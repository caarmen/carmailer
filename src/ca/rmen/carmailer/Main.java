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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import ca.rmen.carmailer.Mail.Body;
import ca.rmen.carmailer.Parser.BodyType;

/**
 * Command-line entry point to CarMailer.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Log.init();
        // Read the arguments given on the command line
        final int required_arguments_length = 7;
        if (args.length < required_arguments_length) usage();
        int i = 0;

        BodyType bodyType = BodyType.AUTO;
        Charset charset = null;
        String from = null;
        File outputFolder = null;
        boolean dryRun = false;
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
        String password = args[i++];
        SmtpCredentials credentials = new SmtpCredentials(serverName, port, userName, password);
        String recipientsFilePath = args[i++];
        String subject = args[i++];
        String bodyFilePath = args[i++];

        if (from == null) from = credentials.userName;


        // Parse the mail body.
        Body body = Parser.parse(bodyFilePath, bodyType, charset);

        // Read the file with the list of e-mail addresses
        List<Recipient> recipients = Parser.parseRecipients(recipientsFilePath, body.charset);
        Mail mail = new Mail(from, recipients, subject, body);
        SendOptions sendOptions = new SendOptions(dryRun, outputFolder);
        CarMailer.sendEmail(credentials, mail, sendOptions);
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
        System.err.println("--from <from>: the value of the From: field.  By default, the username is used.");
        System.err.println("--dry-run: if true, no mail will actually be sent.");
        System.err.println("--body-type <html|text|auto>: Default is auto.");
        System.err.println("--output-folder <path>: if specified, each mail will be written to a file in this folder");
        System.err
                .println("--charset <charset>: specify the charset for reading and writing. By default the charset is guessed from the content of the file or the http-equiv meta tag in the html file.");
        System.err.println();
        System.err.println("*recipients file*: must be a text file containing one e-mail address per line.");
        System.err.println("*body file*: the body of the mail in html or text format.");
        System.exit(1);
    }

}