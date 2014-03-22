From the carmailer top directory:

1. compile the program:

ant dist

2. run the program.  In this example, you are sending the mail from your Gmail account johndoe@gmail.com:

java -jar bin/carmailer.jar  smtp.gmail.com 587 johndoe@gmail.com examples/tag-html/recipients.txt "Testing CarMailer: HTML Mail With Tags" examples/tag-html/mail.html
