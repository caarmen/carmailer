carmailer
=========

Java program to send a mail to multiple recipients.

Provide the following on the command line:
* SMTP authentication parameters: server domain name, port, username, password
* A file with a list of e-mail addresses
* A subject
* An HTML file with the body of the message

The program will then send an e-mail to each recipient in the list, in both plain text and HTML format.
To prevent spam, messages are sent in batches, with a delay between each batch.
