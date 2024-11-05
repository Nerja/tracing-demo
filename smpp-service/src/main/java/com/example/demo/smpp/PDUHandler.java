package com.example.demo.smpp;

import com.cloudhopper.smpp.SmppConstants;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.pdu.SubmitSmResp;
import com.example.demo.model.Message;
import com.example.demo.module.MessageProcessor;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PDUHandler {

    private final ObservationRegistry observationRegistry;
    private final MessageProcessor messageProcessor;
    private final ExecutorService pduRequestExecutorService;

    public PDUHandler(final ObservationRegistry observationRegistry, final MessageProcessor messageProcessor) {
        this.observationRegistry = observationRegistry;
        this.messageProcessor = messageProcessor;
        pduRequestExecutorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void handlePduRequest(PduRequest pduRequest, SmppSession smppSession) throws ExecutionException, InterruptedException {
        pduRequestExecutorService.submit(() -> {
            Observation.createNotStarted("handle-smpp-request", () -> new SMPPPduRequestContext(pduRequest), observationRegistry)
                    .observe(() -> {
                        PduResponse response = pduRequest.createResponse();
                        if (pduRequest.getCommandId() == SmppConstants.CMD_ID_SUBMIT_SM) {
                            Message message = mapSMPPtoMessage((SubmitSm)pduRequest);
                            try {
                                messageProcessor.processMessage(message);
                                SubmitSmResp submitSmResp = new SubmitSmResp();
                                submitSmResp.setMessageId(UUID.randomUUID().toString());
                                submitSmResp.setSequenceNumber(response.getSequenceNumber());
                                try {
                                    smppSession.sendResponsePdu(submitSmResp);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
        });
    }

    private static Message mapSMPPtoMessage(final SubmitSm submitSm) {
        return Message.builder()
                .messageId(UUID.randomUUID().toString())
                .text(new String(submitSm.getShortMessage()))
                .build();
    }
}
