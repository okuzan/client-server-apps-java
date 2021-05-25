package Lab1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    void shouldEncodePackage() {
        Packet packet = new Packet((byte) 1, 10, 19, 19, "Hello world".getBytes());
        byte[] encoded = Packet.encodePackage(packet);
        Packet decoded = Packet.decodePackage(encoded);
        assertEquals(1, decoded.getClientId());
        assertEquals(10, decoded.getPacketId());
        assertEquals("Hello world", new String(decoded.getMsg()));
    }

    @ParameterizedTest
    @CsvSource({
            "10, 222, 19, 19, Hel32lo world",
            "1, 22, 1, 1, Hel32lo worlfds143d"
    })
    void shouldEncodePackageParams(byte client, long packetId, int code, int userId, String message) {
        Packet packet = new Packet(client, packetId, code, userId, message.getBytes());
        byte[] encoded = Packet.encodePackage(packet);
        Packet decoded = Packet.decodePackage(encoded);
        assertEquals(client, decoded.getClientId());
        assertEquals(packetId, decoded.getPacketId());
        assertEquals(message, new String(decoded.getMsg()));
    }


}

