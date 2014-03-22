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

/**
 * Parameters to determine how we will send the mail
 */
class SendOptions {

    final boolean dryRun;
    final File outputFolder;
    // To avoid being detected as spam, don't send too many mails too quickly:
    // Send mail in batches: Send at most maxMailsPerBatch consecutive mails, and sleep 
    // delayBetweenBatches seconds between batches.
    final int maxMailsPerBatch = 100; // not configurable for now
    final int delayBetweenBatches = 60 * 60; // not configurable for now

    /**
     * @param dryRun if true, no mail will actually be sent.
     * @param outputFolder if not-null, an eml file will be created in this folder for each mail to be sent.
     */
    public SendOptions(boolean dryRun, File outputFolder) {
        this.dryRun = dryRun;
        this.outputFolder = outputFolder;
    }

    @Override
    public String toString() {
        return SendOptions.class.getSimpleName() + " [dryRun=" + dryRun + ", outputFolder=" + outputFolder + "]";
    }
}