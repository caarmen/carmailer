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

import java.io.InputStream;
import java.util.Random;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

public class CarMimeMessage extends MimeMessage {
    private static Random sRandom = new Random();
    private String mMessageId;

    public CarMimeMessage(Session session, String userName) {
        super(session);
        createMessageId(userName);
    }

    public CarMimeMessage(MimeMessage arg0, String userName) throws MessagingException {
        super(arg0);
        createMessageId(userName);
    }

    public CarMimeMessage(Session session, InputStream is, String userName) throws MessagingException {
        super(session, is);
        createMessageId(userName);
    }

    public CarMimeMessage(Folder folder, int msgnum, String userName) {
        super(folder, msgnum);
        createMessageId(userName);
    }

    public CarMimeMessage(Folder folder, InputStream is, int msgnum, String userName) throws MessagingException {
        super(folder, is, msgnum);
        createMessageId(userName);
    }

    public CarMimeMessage(Folder folder, InternetHeaders headers, byte[] content, int msgnum, String userName) throws MessagingException {
        super(folder, headers, content, msgnum);
        createMessageId(userName);
    }

    @Override
    protected void updateMessageID() throws MessagingException {
        setHeader("Message-ID", mMessageId);
    }

    private void createMessageId(String domain) {
        String token1 = String.valueOf(sRandom.nextInt(99999999));
        String token2 = String.valueOf(sRandom.nextInt(99999999));
        mMessageId = "<" + token1 + "." + token2 + "@" + domain + ">";
    }
}
