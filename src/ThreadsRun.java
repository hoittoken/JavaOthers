class Some implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(i);
        }
    }
}

public class ThreadsRun {
    public static void main(String[] args) {
        Thread test = new Thread(new Some());
        Thread test_1 = new Thread(new Some());

        test.start();
        test_1.start();

        // ещё вариант
        Thread test_2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    System.out.println(i);
                }
            }
        });
        test_2.start();
    }
}
