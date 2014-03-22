carmailer
=========

Java program to send a mail to multiple recipients.

Provide the following on the command line:
* SMTP authentication parameters: server domain name, port, username, password
* A file with a list of e-mail addresses
* A subject
* A file with the body of the message in HTML or plain text format
* Optional parameters to configure the content-type, charset, and sending behavior.  See the usage.


The program will then send an e-mail to each recipient in the list.
If the mail content is HTML, then the e-mail will be sent as a multi-part mail in both plain text and in HTML.
If the mail content is plain text, then the e-mail will be sent as plain text.

To prevent spam, messages are sent in batches, with a delay between each batch.


Usage:
-----
1. Build:


    $ ant jar

2. Run with no arguments to see the usage:


    $ java -jar bin/carmailer.jar
    

```
    Usage: java -jar bin/carmailer.jar [options] <smtp server> <smtp port> <username> <recipients file> <subject> <body file>
    options:
    --password <password>: The password for the SMTP server.  If not given here, you will be prompted to enter the password.
    --from <from>: the value of the From: field.  By default, the username is used.
    --dry-run: if true, no mail will actually be sent.
    --body-type <html|text|auto>: Default is auto.
    --batch-size <n>: send at most n mails in a batch. Default: 100 mails
    --batch-delay <s>: wait s seconds between sending batches. Default: 3600s (1 hour)
    --send-progress <email address>: Send the progress at the end of each batch, and end status to this e-mail address
    --output-folder <path>: if specified, each mail will be written to a file in this folder
    --charset <charset>: specify the charset for reading and writing. By default the charset is guessed from the content of the file or the http-equiv meta tag in the html file.

    *recipients file*: must be a text file containing one e-mail address per line.
    *body file*: the body of the mail in html or text format.

```

Javadoc:
-------

The javadoc is here: http://caarmen.github.io/carmailer
