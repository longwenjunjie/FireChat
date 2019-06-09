package com.yunliaoim.firechat.smack;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;

public class FCStanzaListener implements StanzaListener {
    @Override
    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
        Message headlineMessage = (Message) packet;
    }
}
