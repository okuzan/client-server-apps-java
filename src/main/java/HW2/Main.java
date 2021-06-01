package HW2;

import HW2.Recieve.Receiver2;
import HW2.Send.Sender;
import HW2.Storage.Product;
import HW2.Storage.ProductGroup;
import HW2.Storage.Warehouse;
import PW1.Packet;

import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) throws SocketException {
        Product p1 = new Product("med", 10, 130);
        Product p2 = new Product("hlib", 2, 15);
        ProductGroup group = new ProductGroup("harchi");
        group.addGoods(p1);
        group.addGoods(p2);
        LinkedBlockingQueue<ProductGroup> queue = new LinkedBlockingQueue<>();
        queue.add(group);
        Warehouse warehouse = new Warehouse(queue);
//        warehouse
        Processor processor = new Processor(warehouse);
        Receiver2 receiver = new Receiver2();

        //get quantity of med
        Packet pp1 = new Packet((byte) 1, 1, 0, 1, "med".getBytes(StandardCharsets.UTF_8));
        Packet pp2 = new Packet((byte) 1, 2, 1, 1, "med%4".getBytes(StandardCharsets.UTF_8));
        Packet pp3 = new Packet((byte) 1, 2, 1, 1, "med%13".getBytes(StandardCharsets.UTF_8));

        Sender sender = new Sender();
        System.out.println(processor.process(pp1));
        processor.process(pp2);
        System.out.println(processor.process(pp1));
        processor.process(pp3); //exceeded amount of products, exception
        System.out.println(processor.process(pp1));

//        new Thread(() -> processor.process(pp1)).start();
//        new Thread(() -> processor.process(pp2)).start();
//        new Thread(() -> processor.process(pp3)).start();
//        new Thread(() -> processor.process(pp1)).start();

//        new Thread(new Receiver.Server()).start();

    }
}
