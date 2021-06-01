package PW2;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Data d = new Data();

        new Worker(2, d);
        new Worker(2, d);
        new Worker(3, d);
        new Worker(3, d);
        new Worker(1, d);
        new Worker(1, d);

//        w2.join();
        System.out.println("end of main...");
    }
}
