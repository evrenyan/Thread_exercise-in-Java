import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
class Bakeri {
    private int bakverk = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition ikkeTomt = lock.newCondition();
    private boolean ferdigBakt = false;

    public void lagBakevare(int baakerId) {
        lock.lock();
        try {
            bakverk++;
            System.out.println("bakeren med id: " + baakerId + " har laget en bakvare.");
            ikkeTomt.signal();
        } finally {
            lock.unlock();
        }
    }
    public void spisBakevare(String kunde) {
        lock.lock();
        try {
            while (bakverk <= 0) {
               if (ferdigBakt) {
                   return;
               }
               ikkeTomt.await();
            }
            bakverk--;
            System.out.println("Kunden " + kunde + " spiser en bakvare.");
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }
    public void ferdigBakt()  {
        lock.lock();
        try {
            ferdigBakt = true;
            ikkeTomt.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
class Baker implements Runnable{
    Bakeri monitor;
    int id;
    public Baker(Bakeri monitor, int id) {
        this.id = id;
        this.monitor = monitor;
    }
    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            monitor.lagBakevare(id);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        monitor.ferdigBakt();
    }
}
class Kunde implements Runnable {
    Bakeri monitor;
    String navn;
    public Kunde(Bakeri monitor, String navn) {
        this.navn = navn;
        this.monitor = monitor;
    }
    @Override
    public void run() {
        for (int i = 0; i < 4; i++) {
            monitor.spisBakevare(navn);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
class Testprogram {
    public static void main(String[] args) throws InterruptedException {
        Bakeri bakeri = new Bakeri();
        int antallBakere = 4;
        int antallKunder = 4;

        Thread[] bakertraad = new Thread[antallBakere];
        for (int i = 0; i < antallBakere; i++) {
            bakertraad[i] = new Thread(new Baker(bakeri, i));
        }
        Thread[] kundetraad = new Thread[antallKunder];
        for (int i = 0; i < antallKunder; i++) {
            kundetraad[i] = new Thread(new Baker(bakeri, i));
        }
        for (int i = 0; i < antallBakere; i++) {
            bakertraad[i].join();
        }
        for (int i = 0; i < antallKunder; i++) {
            kundetraad[i].join();
        }
    }
}
