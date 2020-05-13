package com.example.divisionsimulation.ui.share;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.divisionsimulation.MainActivity;

class DarkZoneTimerThread extends Thread {
    private int minute = 0, second = 0; //분, 초를 저장하는 변수
    private int sum_second = 0, now_sum_second = 0; //임시 변수
    private boolean stop = false, outputing = false, rogue = false, input_rogue = false; //종료, 이송, 로그 탈췯됨 여부, 로그 등장 여부
    private double process; //진행도를 임시로 저장할 변수
    private Handler handler = null; //UI 변경 시 필요한 핸들러
    private Activity activity = null; //토스트를 어디에 띄우기 위한 액티비티
    private ShareFragment sf = null; //스레드를 사용하는 액티비티
    private int rogue_percent = 0; //로그 출몰 확률을 저장하는 변수

    public DarkZoneTimerThread(Handler handler, Activity activity, ShareFragment sf) { //생성자
        this.handler = handler; //핸들러를 가져옴
        this.activity = activity; //액티비티를 가져옴
        this.sf = sf; //ShareFragment를 가져옴
    }
    public void stopThread() { stop = true; } //스레드를 중지시킨다.
    public void setMinute(int minute) { this.minute = minute; } //분을 서정한다.
    public void setSecond(int second) { this.second = second; } //초를 설정한다.
    public void setOutputing(boolean outputing) { this.outputing = outputing; } //이송 헬기 도착 여부를 설정한다.
    public void setInput_rogue(boolean input_rogue) { this.input_rogue = input_rogue; } //로그 출몰 여부를 설정한다.
    public boolean getInput_rogue() { return input_rogue; } //로그 출몰 여부를 내보낸다.
    public void setRogue(boolean rogue) { this.rogue = rogue; } //로그로 인한 탈취 여부를 설정한다.
    public void setRoguePercent(int rogue_percent) { this.rogue_percent = rogue_percent; } //로그 확률을 설정한다.

    public int randomRogue(int min, int length) { //매 초마다 로그 등장할 확률을 램덤으로 반환한다.
        return (int)(Math.random()*1234567)%length+min;
    };

    public void run() {
        sum_second = (minute*60)+second; //이송까지 남은 시간 최대치를 저장한다. (전체X 이송헬기 오기 전까지 따로, 이송헬기 오고나서 따로)
        now_sum_second = (minute*60)+second-1; //현재 남은 시간을 저장
        while (((minute != 0 || second != 0) || now_sum_second != -1) && !stop && !rogue) { //로그로 인해 탈취당하거나 분, 초가 0이 되었거나 남은 시간이 없을 경우 종료된다. 그 전까지는 무한 반복 상태이다.
            process = ((double)now_sum_second/(double)sum_second)*10000; //현재 남은 시간을 통해 현재 진행도를 저장한다.

            if (minute != 0) { //분이 0이 아닐 경우 작동
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //ShareFragment.txtTimer.setText(minute+"분 "+second+"초");
                        sf.setTxtTimer(minute+"분 "+second+"초"); //분, 초 모두 출력한다.
                        sf.setProgressTimer(10000-(int)process); // 진행도 설정
                    }
                });
            } else { //분이 0이 될 경우 출력할 필요가 없으므로 초 단위만 보여준다.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //ShareFragment.txtTimer.setText(second+"초");
                        sf.setTxtTimer(second+"초"); //초만 보여준다.
                        sf.setProgressTimer(10000-(int)process); //위와 동일
                    }
                });
            }
            /*handler.post(new Runnable() {
                @Override
                public void run() {
                    ShareFragment.progressTimer.setProgress(10000-(int)process);
                }
            });*/

            now_sum_second --; //남은 시간을 1초 감소한다.
            second --; //초를 1 감소시킨다.

            if (second < 0) { //초가 0 미만이 될 경우 작동
                if (minute == 0) { //분이 0 이 될 경우 작동
                    break; //반복문을 종료시킨다.
                } else { //분이 0을 초과하였을 경우 작동
                    minute --; //분을 1 감소시킨다.
                    second = 59; //초를 59초 증가시킨다.
                }
            }

            if (input_rogue && randomRogue(1, 100) <= 5) { //1~100까지 램던 난수가 5 이하가 되면 작동한다. (로그 등장 이후)
                sf.deleteDZitem(); //다크존 아이템 초기화
                rogue = true; //로그 탈취를 참으로 변경
            }
            if ((randomRogue(1, 1000) <= rogue_percent) && !input_rogue) { //위와 동일한 방식 (로그 등장 이전)
                input_rogue = true; //로그 등장 여부를 참으로 변경
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "주위에 로그 요원이 있습니다.", Toast.LENGTH_LONG).show(); //주위에 로그 요원이 있다는 것을 토스트로 알려줌.
                    }
                });
            }

            if (rogue) break; //로그로 인해 탈취되었으면 종료시킨다.

            if (now_sum_second != -1) { //초가 0보다 떨어지면 작동을 하지 않는다.
                try {
                    Thread.sleep(1000); //1초 딜레이를 준다.
                } catch (Exception e) {
                    e.getStackTrace(); //sleep 에러가 뜨면 콘솔에 에러 메시지를 출력한다.
                }
            }
        }
        if (!rogue) { //로그로 인해 탈취가 되지 않았으면 작동한다.
            if (!stop) { //중간 종료를 하지 않았으면 작동한다.
                if (!outputing) { //이송 헬기가 도착하기 이전이면 작동한다.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "헬기가 도착했습니다.", Toast.LENGTH_SHORT).show(); //헬기가 도착했다고 토스트를 통해 화면에 보여준다.
                        }
                    });
                } else { //이송이 마치면 작동한다.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "이송을 완료했습니다.", Toast.LENGTH_SHORT).show(); //위와 동일한 방식
                        }
                    });
                }
            } else { //중간에 종료를 하게 되면 작동한다.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "목표를 정지합니다.", Toast.LENGTH_SHORT).show(); //위와 동일한 방식
                    }
                });
            }
            if (!outputing) sf.playOutputDZ(); //헬기가 도착하기 이전이면 작동한다. 헬기 도착 이후 스레드를 실행한다.
            if (outputing) sf.dialogOpen(); //헬기가 도착하고나서 종료되면 작동한다. 이송완료되었다는 다이얼로그를 띄운다.
        }
    }
}
