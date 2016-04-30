package com.jose.castsocialconnector.message.receive;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * Created by Jose Manuel on 29/04/2016.
 */
public class IdleThread extends Thread {
    private volatile boolean running = true;

    public synchronized void kill() {

        if (!running)
            return;
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {

            try {
                ensureOpen();
                System.out.println("enter idle");
                ((IMAPFolder) GetEmailService.inboxNewEmail).idle();
            } catch (Exception e) {
                // something went wrong
                // wait and try again
                e.printStackTrace();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // ignore
                }
            }

        }
    }

    public static void ensureOpen() throws MessagingException {

        if (GetEmailService.inboxNewEmail != null) {
            if (GetEmailService.imapStore != null && !GetEmailService.imapStore.isConnected()) {

            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (GetEmailService.inboxNewEmail.exists() && !GetEmailService.inboxNewEmail.isOpen() && (GetEmailService.inboxNewEmail.getType() & GetEmailService.inboxNewEmail.HOLDS_MESSAGES) != 0) {
            System.out.println("open folder " + GetEmailService.inboxNewEmail.getFullName());
            GetEmailService.inboxNewEmail.open(Folder.READ_ONLY);
            if (!GetEmailService.inboxNewEmail.isOpen())
                throw new MessagingException("Unable to open folder " + GetEmailService.inboxNewEmail.getFullName());
        }

    }

}
