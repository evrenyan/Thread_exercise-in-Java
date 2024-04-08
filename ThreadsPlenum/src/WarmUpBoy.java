import java.util.ArrayList;
class Prosess implements Runnable {
    private String id;
    public Prosess(String id) {
        this.id = id;
    }
    @Override
    public void run() {
        System.out.println("Prosess: " + id + " kjorer.");
    }
}
class Hovedprogram {
    public static void main(String[] args) {
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Thread prosess = new Thread(new Prosess("p" + i));
            prosess.start();
            threads.add(prosess);
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Nå er programmet ferdig");
    }
}