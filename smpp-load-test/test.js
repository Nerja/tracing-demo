import smpp from "k6/x/smpp"
import oteltracing from 'k6/x/oteltracing';
import { check } from 'k6';
import { sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  discardResponseBodies: true,
  scenarios: {
    contacts: {
      executor: 'constant-arrival-rate',
      duration: '5s',
      rate: 1,
      timeUnit: '1s',
      preAllocatedVUs: 500,
      maxVUs: 500,
    },
  },
};
  
export function setup() {
  let bind = smpp.bind(`${__ENV.SMPP_SERVER}`, `${__ENV.SMPP_SERVER}`, "", "", "")
}
  
export default function () {
  let span = oteltracing.createSpan("submitSM");
  console.log(span.spanContext().traceID().string());
  let msisdn = "46711111111"
  let mtBillingName = "marcus-billing-name"
  let msg = "Hello from K6"
  let submit = smpp.submitMT(msisdn, msg, {
    5248: mtBillingName,
    1534: "00-"+span.spanContext().traceID().string()+"-"+span.spanContext().spanID().string()+"-01",
    1535: "",
  })
  check(submit, {
    "submit success": (r) => r.error == null,
  })
  console.log(submit.message_id)
  sleep(0.0001 * randomIntBetween(1, 100))
  span.end()
}

export function teardown() {
  oteltracing.close()
}
