package com.example.demo.smpp;

import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.tlv.Tlv;
import com.cloudhopper.smpp.tlv.TlvConvertException;
import io.micrometer.observation.transport.ReceiverContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SMPPPduRequestContext extends ReceiverContext<PduRequest<?>> {

    private static final short TRACEPARENT_TAG = (short) 1534;
    private static final short TRACESTATE_TAG = (short) 1535;

    public SMPPPduRequestContext(PduRequest<?> pduRequest) {
        super(
                (carrier, key) -> {
                    switch (key) {
                        case "traceparent":
                            Tlv traceparentTlv = pduRequest.getOptionalParameter(TRACEPARENT_TAG);
                            if (traceparentTlv != null) {
                                try {
                                    return traceparentTlv.getValueAsString();
                                } catch (TlvConvertException e) {
                                    log.error("Failed to decode traceparent: {}", traceparentTlv, e);
                                    return null;
                                }
                            }
                            return null;
                        case "tracestate":
                            Tlv tracestateTlv = pduRequest.getOptionalParameter(TRACESTATE_TAG);
                            if (tracestateTlv != null) {
                                try {
                                    return tracestateTlv.getValueAsString();
                                } catch (TlvConvertException e) {
                                    log.error("Failed to decode tracestate: {}", tracestateTlv, e);
                                    return null;
                                }
                            }
                            return null;
                        default:
                            return null;
                    }
                });
        setCarrier(pduRequest);
    }

}
