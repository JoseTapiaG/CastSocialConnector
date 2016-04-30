package com.jose.castsocialconnector.message.receive;

import android.os.AsyncTask;

import com.google.code.oauth2.OAuth2Authenticator;
import com.jose.castsocialconnector.main.MainActivity;
import com.jose.castsocialconnector.xml.XmlContact;
import com.sun.mail.imap.IMAPStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Jose Manuel on 29/04/2016.
 */
public class GetEmailService implements MessageCountListener {

    static IMAPStore imapStore;
    static Folder inbox;
    static Folder inboxNewEmail;
    static IdleThread idleThread;

    MainActivity activity;

    public GetEmailService(MainActivity activity) {
        this.activity = activity;
    }

    public ArrayList<Email> emails;

    public ArrayList<Email> getEmails() {
        synchronized (this) {
            return emails;
        }
    }

    public void connect(String emailAccount, String oauthToken) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... strings) {
                try {
                    imapStore = OAuth2Authenticator.connectToImap(
                            "imap.gmail.com",
                            993,
                            strings[0],
                            strings[1],
                            true);
                    inbox = imapStore.getFolder("INBOX");
                    inboxNewEmail = imapStore.getFolder("INBOX");
                    inboxNewEmail.addMessageCountListener(GetEmailService.this);

                    if (idleThread == null) {
                        idleThread = new IdleThread();
                        idleThread.setDaemon(false);
                        idleThread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(emailAccount, oauthToken);
    }

    public void cargarCorreos() {
        synchronized (this) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {

                        GetEmailService.this.emails = new ArrayList<>();

                        inbox.open(Folder.READ_ONLY);
                        Date d = new Date();
                        Date dateBefore = new Date(d.getTime() - 7 * 24 * 3600 * 1000);
                        SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, dateBefore);
                        SearchTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                        SearchTerm dateSeenTerm = new AndTerm(newerThan, flagTerm);

                        if (GetEmailService.this.activity.xmlContacts.isEmpty())
                            return null;

                        FromStringTerm[] stringTerms = new FromStringTerm[GetEmailService.this.activity.xmlContacts.size()];

                        for (int i = 0; i < GetEmailService.this.activity.xmlContacts.size(); i++) {
                            stringTerms[i] = new FromStringTerm(GetEmailService.this.activity.xmlContacts.get(i).getEmail());
                        }
                        OrTerm orTerm = new OrTerm(stringTerms);
                        SearchTerm andTerm = new AndTerm(dateSeenTerm, orTerm);

                        Message[] folderMessages = inbox.search(andTerm);
                        for (Message msg : folderMessages) {
                            emails.add(new Email(msg.getFrom()[0].toString(), getText(msg)));
                        }

                    } catch (Exception mex) {
                        mex.printStackTrace();
                    } finally {
                        try {
                            inbox.close(true);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
        }
    }

    @Override
    public void messagesAdded(MessageCountEvent messageCountEvent) {
        cargarCorreos();
    }

    @Override
    public void messagesRemoved(MessageCountEvent messageCountEvent) {

    }

    private String getText(Part p) throws
            MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            if (p.isMimeType("text/html")) {
                Document doc = Jsoup.parseBodyFragment(s);
                for (Element element: doc.select("div.extra"))
                    element.remove();
                for (Element element: doc.select("div.gmail_extra"))
                    element.remove();
                for (Element element: doc.select("div.gmail_quote"))
                    element.remove();
                s = doc.outerHtml();
                s = s.replaceAll("\\n ", "<asdfasdf>");
                s = s.replaceAll("&aacute;", "á");
                s = s.replaceAll("&eacute;", "é");
                s = s.replaceAll("&iacute;", "í");
                s = s.replaceAll("&oacute;", "ó");
                s = s.replaceAll("&uacute;", "ú");
                s = s.replaceAll("&ntilde;", "ñ");
                s = s.replaceAll("&Aacute;", "Á");
                s = s.replaceAll("&Eacute;", "É");
                s = s.replaceAll("&Iacute;", "Í");
                s = s.replaceAll("&Oacute;", "Ó");
                s = s.replaceAll("&Uacute;", "Ú");
                s = s.replaceAll("&Ntilde;", "Ñ");
                s = s.replaceAll("&iquest;", "¿");
                s = s.replaceAll("&nbsp;", " ");
                s = s.replaceAll("\\s\\s+", " ");
                s = s.replaceAll("\\<.*?>", "");
                s = s.replace('\t','\0');
            }
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }
}
