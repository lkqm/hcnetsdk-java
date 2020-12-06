package com.github.lkqm.hcnet.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ResponseStatusTest {

    @Test
    void ofXml() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<ResponseStatus xmlns=\"http://www.hikvision.com/ver20/XMLSchema\">\n"
                + "<requestURL>/ISAPI/System/deviceInfo</requestURL>\n"
                + "<statusCode>5</statusCode>\n"
                + "<statusString>Invalid Format</statusString>\n"
                + "<subStatusCode>badXmlFormat</subStatusCode>\n"
                + "</ResponseStatus>";
        ResponseStatus responseStatus = ResponseStatus.ofXml(xml);
        assertNotNull(responseStatus, "返回数据不应该为null");
        assertEquals("/ISAPI/System/deviceInfo", responseStatus.getRequestURL(), "requestURL解析不正确");
        assertEquals(5, responseStatus.getStatusCode(), "statusCode解析不正确");
        assertEquals("badXmlFormat", responseStatus.getSubStatusCode(), "subStatusCode解析不正确");
        assertEquals("Invalid Format", responseStatus.getStatusString(), "statusString解析不正确");
    }
}