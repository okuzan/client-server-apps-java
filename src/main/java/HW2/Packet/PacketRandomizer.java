package HW2.Packet;

import PW1.Packet;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class PacketRandomizer {
    private static final int Q_CODES = PacketFormat.commands.values().length;
    private static final int Q_CLIENT_IDS = PacketFormat.getClientIDs().size();

    public static Packet generate() {
        return new Packet((byte) (Math.random() * Q_CLIENT_IDS), // clientID
                (long) (Math.random() * 9999),                   // packetID
                (int) (Math.random() * Q_CODES),                 // code
                (int) (Math.random() * 100),                     // userID
                randomString((int) (Math.random() * 432)).getBytes(StandardCharsets.UTF_8)); //msg
    }

    public static String randomString(int length) {
        int leftLimit = 97;   // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
