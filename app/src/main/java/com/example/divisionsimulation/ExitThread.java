package com.example.divisionsimulation;

class ExitThread extends Thread{

    private int time = 10;
    private MainActivity ma = null;
    private boolean stop = false;

    public ExitThread(MainActivity ma) { this.ma = ma; }
    public void stopThread(boolean stop) { this.stop = stop; }

    public void run() {
        while (time > -1 && !stop) {
            //MainActivity.txtInfo.setText(time+"초 뒤에 자동으로 종료됩니다.");
            ma.setTxtInfo(time+"초 뒤에 자동으로 종료됩니다.");
            if (stop) break;
            if (time == 0) ma.finish();
            time--;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}
