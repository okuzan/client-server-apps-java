package PW1;

public class Main {

    public static void main(String[] args) {
        String msg = "Test string";
        System.out.println(new String(
                Packet.decodePackage(Packet.encodePackage(
                        new Packet((byte) 10, 10, 2, 2, msg.getBytes()))).getMsg()));
    }


}

