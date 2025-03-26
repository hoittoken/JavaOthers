
class SomeThread extends Thread{

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}

public class ThreadsExt {
    public static void main(String[] args) {
        new SomeThread().start();
        new SomeThread().start();
    }
}
