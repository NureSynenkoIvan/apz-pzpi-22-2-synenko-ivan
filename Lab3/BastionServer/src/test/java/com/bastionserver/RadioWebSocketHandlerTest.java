package com.bastionserver;

import com.bastionserver.analysis.controller.RadioWebSocketHandler;
import com.bastionserver.analysis.service.RadioAggregationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class RadioWebSocketHandlerTest {

    @Mock
    private RadioAggregationService aggregationService;

    @Mock
    private WebSocketSession session;

    private RadioWebSocketHandler handler;

    @Captor
    ArgumentCaptor<TextMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        handler = new RadioWebSocketHandler(aggregationService);
    }

    @Test
    void testHandleDroneMessage() throws Exception {
        String json = """
        {
          "type": "drone",
          "payload": [
            {
              "pkt_len": 42,
              "unk": 0,
              "version": 1,
              "sequence_number": 1234,
              "state_info": 1,
              "serial_number": "ABC123XYZ",
              "longitude": 30.12345,
              "latitude": 50.54321,
              "altitude": 120.5,
              "height": 10.0,
              "v_north": 1,
              "v_east": 2,
              "v_up": 3,
              "d_1_angle": 90,
              "gps_time": 1700000000,
              "app_lat": 50.54320,
              "app_lon": 30.12340,
              "longitude_home": 30.00000,
              "latitude_home": 50.00000,
              "device_type": "DJI",
              "uuid_len": 16,
              "uuid": "550e8400-e29b-41d4-a716-446655440000",
              "crc_packet": "ABCDEF",
              "crc_calculated": "ABCDEF",
              "receiverDeviceId": 42
            }
          ]
        }
        """;

        handler.handleTextMessage(session, new TextMessage(json));

        verify(aggregationService).onDroneData(anyList());
        verify(session).sendMessage(messageCaptor.capture());

        assertEquals("ACK", messageCaptor.getValue().getPayload());
    }

    @Test
    void testHandleSignalMessage() throws Exception {
        String json = """
            {
              "type": "signal",
              "payload": [
                {
                  "deviceId": 2,
                  "frequency": 2450.0,
                  "date": "2025-04-23T18:25:43.511Z",
                  "signalStrength": 45.5
                }
              ]
            }
            """;

        handler.handleTextMessage(session, new TextMessage(json));

        verify(aggregationService).onSignalData(anyList());
        verify(session).sendMessage(messageCaptor.capture());

        assertEquals("ACK", messageCaptor.getValue().getPayload());
    }

    @Test
    void testHandleUnknownType() throws Exception {
        String json = """
            {
              "type": "unknown",
              "payload": []
            }
            """;

        handler.handleTextMessage(session, new TextMessage(json));

        verify(session).sendMessage(messageCaptor.capture());
        assertEquals("ERROR: Unknown type", messageCaptor.getValue().getPayload());

        // не повинно бути викликів агрегації
        verifyNoInteractions(aggregationService);
    }
}
