package PW2;

public class Worker extends Thread {

    private final int workState;
    private final Data data;

    public Worker(int workState, Data d) {
        super("worker-" + workState);
        this.workState = workState;
        data = d;
        this.start();
    }

    @Override
    public void run() {
//        super.run();
        try {
            for (int i = 0; i < 5; i++) {
                synchronized (data) {
                    while (workState != data.getState()) {
                        data.wait();
                    }
                    if (workState == 1) {
                        data.Tic();
                    } else if (workState == 2) {
                        data.Tak();
                    } else {
                        data.Toy();
                    }
                    data.notifyAll();
                }
            }
        } catch (Exception ignored) {
        }
    }

}
