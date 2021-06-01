package HW2;

import HW2.Recieve.Receiver2;
import PW1.Packet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

@ExtendWith({MockitoExtension.class})
public class MainTest {
    @Mock
    Receiver2 s = new Receiver2();

    @Test
    void test1() {
        Packet p = new Packet((byte) 1, 1, 1, 1, "df".getBytes());
        Mockito.when(s.receive("0x02".getBytes(StandardCharsets.UTF_8))).
                thenReturn(p);
    }
}
