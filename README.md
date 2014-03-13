carmailer
=========

Java program to send a mail to multiple recipients.

Provide the following on the command line:
* SMTP authentication parameters: server domain name, port, username, password
* A file with a list of e-mail addresses
* A subject
* A file with the body of the message in HTML or plain text format
* Optional parameters to force the charset, From: address, and content type.

The program will then send an e-mail to each recipient in the list.
If the mail content is HTML, then the e-mail will be sent as a multi-part mail in both plain text and in HTML.
If the mail content is plain text, then the e-mail will be sent as plain text.

To prevent spam, messages are sent in batches, with a delay between each batch.
