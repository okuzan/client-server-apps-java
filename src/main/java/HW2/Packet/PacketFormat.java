package HW2.Packet;

import java.util.HashMap;
import java.util.Map;

public class PacketFormat {
    private static final byte MAGIC_BYTE = 0x13;
    private static final String DELIMITER = "%";
    private static final Map<Integer, String> clientIDs = new HashMap<Integer, String>() {{
        put(1, "Phone");
        put(2, "Tablet");
        put(3, "Desktop");
        put(4, "Other");
    }};

    public static String getDELIMITER() {
        return DELIMITER;
    }

    public static Map<Integer, String> getClientIDs() {
        return clientIDs;
    }

    public static byte getMagicByte() {
        return MAGIC_BYTE;
    }

    enum commands {
        Update,
        Look,
        Delete
    }
}
