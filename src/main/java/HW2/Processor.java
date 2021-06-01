package HW2;

import HW2.Packet.PacketFormat;
import HW2.Storage.Warehouse;
import PW1.Packet;

public class Processor {
    private final Warehouse warehouse;

    public Processor(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public int process(Packet packet) {
        int code = packet.getCode();
        String message = new String(packet.getMsg());
        String[] splits = message.split(PacketFormat.getDELIMITER());
        switch (code) {
            case 0:
                System.out.println("case 0 - get prod Q");
                return warehouse.getProdQ(message);
            case 1:
                System.out.println("case 1 - discard");
                warehouse.decreaseProdQ(splits[0], Integer.parseInt(splits[1]));
                break;
            case 2:
                System.out.println("case 1 - receive");
                warehouse.increaseProdQ(splits[0], Integer.parseInt(splits[1]));
                break;
            case 3:
                System.out.println("case 3 - add group");
                warehouse.addProductGroup(message);
                break;
            case 4:
                System.out.println("case 4 - add prod to group");
                warehouse.addToGroup(splits[0], splits[1]);
                break;
            case 5:
                System.out.println("case 5 - set price");
                warehouse.setPrice(splits[0], Double.parseDouble(splits[1]));
                break;
            default:
                break;

        }
        return -1;
    }
}
