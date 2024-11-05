package com.example.demo.smpp;

import com.cloudhopper.smpp.*;
import com.cloudhopper.smpp.impl.DefaultSmppServer;
import com.cloudhopper.smpp.pdu.*;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.type.SmppChannelException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SMPPServer {

    private final DefaultSmppServer server;

    public SMPPServer(final PDUHandler pduHandler) {
        SmppServerConfiguration configuration = new SmppServerConfiguration();
        configuration.setPort(7777);
        configuration.setMaxConnectionSize(10);
        configuration.setNonBlockingSocketsEnabled(true);
        configuration.setDefaultRequestExpiryTimeout(30000);
        configuration.setDefaultWindowMonitorInterval(15000);
        configuration.setDefaultWindowSize(10000);
        configuration.setDefaultWindowWaitTimeout(configuration.getDefaultRequestExpiryTimeout());

        server = new DefaultSmppServer(configuration, new DefaultSmppServerHandler(pduHandler));
    }

    @PostConstruct
    public void startServer() throws SmppChannelException {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
    }

    public static class DefaultSmppServerHandler implements SmppServerHandler {

        private final PDUHandler pduHandler;

        public DefaultSmppServerHandler(final PDUHandler pduHandler) {
            this.pduHandler = pduHandler;
        }

        @Override
        public void sessionBindRequested(Long aLong, SmppSessionConfiguration smppSessionConfiguration, BaseBind baseBind) {

        }

        @Override
        public void sessionCreated(Long aLong, SmppServerSession smppServerSession, BaseBindResp baseBindResp) {
            smppServerSession.serverReady(new TestSmppSessionHandler(smppServerSession, pduHandler));
        }

        @Override
        public void sessionDestroyed(Long aLong, SmppServerSession session) {
            session.destroy();
        }
    }

    public static class TestSmppSessionHandler extends DefaultSmppSessionHandler {
        private final SmppSession session;
        private final PDUHandler pduHandler;

        public TestSmppSessionHandler(final SmppSession session, final PDUHandler pduHandler) {
            this.session = session;
            this.pduHandler = pduHandler;
        }

        @Override
        public PduResponse firePduRequestReceived(PduRequest pduRequest) {
            try {
                pduHandler.handlePduRequest(pduRequest, session);
            } catch (Exception e) {
                log.error("Failed to send PDU response", e);
            }
            return null;
        }
    }
}