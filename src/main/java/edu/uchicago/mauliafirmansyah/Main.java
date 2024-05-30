package edu.uchicago.mauliafirmansyah;

public class Main {
    public static void main(String[] args) throws Exception {
        new Thread(){
            public void run(){
                try {
                    new BookingApp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            public void run(){
                try {
                    new BookingSystem();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            public void run(){
                try {
                    new FleetSystem();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            public void run(){
                try {
                    new PaymentSystem();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(3600 * 1000);
    }
}
