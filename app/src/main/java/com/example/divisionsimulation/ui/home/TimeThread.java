package com.example.divisionsimulation.ui.home;

class TimeThread extends Thread {
    private int seconds = 0;
    private boolean stop = false;

    public void setStop(boolean stop) {
        if (stop) this.stop = true;
        else this.stop = false;
    }

    public void run() {
        while (!stop) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            seconds++;
            SimulActivity.txtTime.setText(seconds+"초");
        }

        System.out.println("(TimeThread) 정상적으로 종료됨");
    }
}
