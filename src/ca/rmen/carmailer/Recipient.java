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

import java.util.Arrays;

/**
 * A recipient has an e-mail address and an optional set of tags. These tags will replace variables like %1, %2, %3, etc, in the mail content.
 */
public class Recipient {
    final String address;
    final String[] tags;

    /**
     * @param address the e-mail address of the recipient.
     * @param tags If sending a mail with tags, this will contain the values for each of the tags.
     */
    public Recipient(String address, String[] tags) { // NO_UCD (use default)
        this.address = address;
        this.tags = tags == null ? new String[0] : tags;
    }

    @Override
    public String toString() {
        return Recipient.class.getSimpleName() + " [address=" + address + ", tags=" + Arrays.toString(tags) + "]";
    }

}