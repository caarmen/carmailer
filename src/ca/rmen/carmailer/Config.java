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


/**
 * Parameters for carmailer
 */
public class Config {

    static final boolean DEBUG = true;
    // To avoid being detected as spam, don't send too many mails too quickly:
    // Send mail in batches: Send at most MAX_MAILS_PER_BATCH consecutive mails, and sleep 
    // DELAY_BETWEEN_BATCHES_S seconds between batches.
    static final int MAX_MAILS_PER_BATCH = 100;
    static final int DELAY_BETWEEN_BATCHES_S = 60 * 60; // one hour

}