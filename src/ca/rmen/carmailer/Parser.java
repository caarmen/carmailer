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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.glaforge.i18n.io.CharsetToolkit;

/**
 * This class requires the JSoup library.
 * 
 * Reads a file, detects the charset, and creates an html and text version of the file.
 */
class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    static class Body {
        final String text;
        final String html;
        final Charset charset;

        Body(String text, String html, Charset charset) {
            this.text = text;
            this.html = html;
            this.charset = charset;
        }
    }

    static enum BodyType {
        HTML, TEXT, AUTO
    };


    /**
     * Read the file at the given path, and return a Body with the text and html versions of the file.
     * 
     * @param charset if null, we will determine the guess the charset, or if this is an html file which
     *            explicitly declares the charset, we will use it. Otherwise we will use the given charset.
     */
    static Body parse(String filePath, BodyType bodyType, Charset charset) throws FileNotFoundException, IOException {
        final Body body;
        File bodyFile = new File(filePath);
        boolean shouldGuessCharset = charset == null;
        if (shouldGuessCharset) charset = CharsetToolkit.guessEncoding(bodyFile, 1024);
        String bodyText = IOUtils.readFile(bodyFile, charset);
        if (bodyType == BodyType.HTML || bodyType == BodyType.AUTO) {
            Document document = Jsoup.parse(bodyFile, null);
            Elements elements = document.getAllElements();
            // If we've forced html format, or we have some real html elements,
            // the body will contain an html part
            if (bodyType == BodyType.HTML || elements.size() > 4) {
                String html = document.outerHtml();
                if (shouldGuessCharset) charset = document.outputSettings().charset();
                String text = htmlToText(document);
                body = new Body(text, html, charset);
            }
            // We're in auto mode and didn't detect real html elements. Use plain text only.
            else {
                body = new Body(bodyText, null, charset);
            }
        }
        // We've forced plain text mode.
        else {
            body = new Body(bodyText, null, charset);
        }
        Log.d(TAG, "Body parsed as " + (body.html == null ? "plain text" : "html") + ", with charset " + body.charset);
        return body;
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
        String debugTextBody = new String(textBody.getBytes("UTF-8"));
        Log.d(TAG, debugTextBody);
        return textBody;
    }

}