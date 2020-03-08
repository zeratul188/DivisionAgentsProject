package com.example.divisionsimulation.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;

/*
시뮬레이션의 핵심 스레드이다.
홈 액티비티에서 등록된 데이터를 통해서 자신이 얼마나 데미지가 나오는지 상대 체력을 얼마나 빨리 깎을 수 있는지를 테스트하는 시뮬레이션이다.
시뮬레이션이 정확하지는 않을 수 있으니 양해부탁드립니다.
 */

class DemageSimulThread extends Thread implements Serializable {
    private double weapondemage, rpm, critical, criticaldemage, headshot, headshotdemage, elitedemage, shelddemage, healthdemage, reloadtime, ammo, aiming; //홈 액티비티에서 가져온 데이터들을 저장할 변수들이다.
    private int health, sheld, all_ammo = 0; //체력, 방어도, 사용한 탄약 수
    private boolean elite_true = false, pvp_true = false, boom = false, quick_hand = false, cluch_true = false, end = false, bumerang_true = false, bumerang = false, reloaded = false, fire = false; //기타 탤런트 여부
    private int first_health, first_sheld; //최대 체력, 최대 방어도
    private double dec_health, dec_sheld, dec_ammo; //체력, 방어도, 남은 탄약 수에 대한 진행도이다.
    private TimeThread tt; //시뮬레이션 동안 진행되는 타이머 스레드를 저장하는 변수이다.
    private Activity activity; //시뮬액티비티를 나타낼 변수이다.
    private double crazy_dmg, seeker_dmg, push_critical_dmg, eagle_dmg, coefficient, front_dmg; //탤런트에 해당되는 데미지 비율을 저장하는 변수이다.
    private int hit_critical = 0, out_demage, all_dmg = 0; //빠른 손의 히트 수, 등등이다.
    private boolean[] options = null; //카멜레온의 하위 옵션 3개를 저장할 배열 변수이다.
    private double new_weapondemage; //탤런트 등으로 인한 무기 데미지 상승을 저장할 변수이다.

    private Handler handler = null; //상위 액티비티 UI를 수정할 핸들러를 가져온다.

    private CluchThread ct = null; //상대 클러치 여부에 따라 스레드를 사용할 변수이다.
    private SimulActivity sa = null;

    private String[] listDemage = new String[7];
    private boolean[] on_headshot_list = new boolean[7];
    private boolean[] on_critical_list = new boolean[7];
    private boolean[] on_boom_list = new boolean[7];

    //private boolean headshot_enable = false;
    //private boolean critical_enable = false;

    private ReloadThread rt = null; //재장전 스레드이다. 재장전할 때 게이지가 차는 것을 보여주는 스레드이다.

    private String log, statue_log = "", ammo_log = ""; //액티비티에 표현할 문자열을 저장하는 변수이다.

    private boolean on_headshot = false, on_critical = false, on_boom = false;

    /*final Handler headshot_handle = new Handler(){

        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
            if (headshot_enable) SimulActivity.changeHeadshot(true);
            else SimulActivity.changeHeadshot(false);
        }
    };

    final Handler critical_handle = new Handler() {

        public void handleMessage(Message msg) {
            if (critical_enable) SimulActivity.changeCritical(true);
            else SimulActivity.changeCritical(false);
        }
    };*/

    public void setSimulActivity(SimulActivity sa) { this.sa = sa; }

    public void setTimeThread(TimeThread tt) {
        this.tt = tt;
    }
    public void setCluchThread(CluchThread ct) { this.ct = ct; }
    public void setReloadThread(ReloadThread rt) { this.rt = rt; }

    public void setWeapondemage(double weapondemage) { this.weapondemage = weapondemage; }
    public void setRPM(double rpm) { this.rpm = rpm; }
    public void setCritical(double critical) { this.critical = critical; }
    public void setCriticaldemage(double criticaldemage) { this.criticaldemage = criticaldemage; }
    public void setHeadshot(double headshot) { this.headshot = headshot; }
    public void setHeadshotdemage(double headshotdemage) { this.headshotdemage = headshotdemage; }
    public void setElitedemage(double elitedemage) { this.elitedemage = elitedemage; }
    public void setShelddemage(double shelddemage) { this.shelddemage = shelddemage; }
    public void setHealthdemage(double healthdemage) { this.healthdemage = healthdemage; }
    public void setReloadtime(double reloadtime) { this.reloadtime = reloadtime; }
    public void setAmmo(double ammo) { this.ammo = ammo; }
    public synchronized void setHealth(int health) { this.health = health; }
    public void setSheld(int sheld) { this.sheld = sheld; }
    public void setElite_true(boolean elite_true) { this.elite_true = elite_true; }
    public void setPVP_true(boolean pvp_true) { this.pvp_true = pvp_true; }
    public void setCrazy_dmg(int crazy_dmg) { this.crazy_dmg = (double)crazy_dmg; }
    public void setSeeker_dmg(int seeker_dmg) { this.seeker_dmg = (double)seeker_dmg; }
    public void setBoom(boolean boom) { this.boom = boom; }
    public void setPush_critical_dmg(int push_critical_dmg) { this.push_critical_dmg = push_critical_dmg; }
    public void setEagle_dmg(int eagle_dmg) { this.eagle_dmg = eagle_dmg; }
    public void setQuick_hand(boolean quick_hand) { this.quick_hand = quick_hand; }
    public void setCluch_true(boolean cluch_true) { this.cluch_true = cluch_true; }
    public void setEnd(boolean end) { this.end = end; }
    public void setAiming(double aiming) { this.aiming = aiming; }
    public void setBumerang_true(boolean bumerang_true) { this.bumerang_true = bumerang_true; }
    public void setActivity(Activity activity) { this.activity = activity; }
    public void setHandler(Handler handler) { this.handler = handler; }
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; }
    public void setOptions(boolean[] options) { this.options = options; }
    public void setFire(boolean fire) { this.fire = fire; }
    public void setFront_dmg(int front_dmg) { this.front_dmg = front_dmg; }

    public int getSheld() { return this.sheld; }
    public synchronized int getHealth() { return this.health; }

    public void boolReset() {
        on_boom = false;
        on_critical = false;
        on_headshot = false;
        on_boom_list[on_boom_list.length-1] = false;
        on_critical_list[on_critical_list.length-1] = false;
        on_headshot_list[on_headshot_list.length-1] = false;
    }

    /*
    위 메소드들은 다른 클래스에서 이 스레드에 대한 변수 수정시 사용되는 메소드들이다.
     */

    private void reload() { //재장전 시 사용되는 메소드이다.
        int time = (int)(reloadtime*1000); //재장전 시간을 저장하는 변수이다.
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                SimulActivity.progressAmmo.setIndeterminate(true);
            }
        });*/
        handler.post(new Runnable() {
            @Override
            public void run() {
                //SimulActivity.txtStatue.setText("재장전 중...");
                sa.setTxtStatue("재장전 중..."); //액티비티에 재장전 상태를 나타낸다.
            }
        });
        if (quick_hand) { //빠른 손 탤런트 여부에 따라 작동한다.
            double handred_time = (double)time / 2; //100% 기준은 재장전 시간의 절반으로 기준으로 잡는다.
            if (hit_critical > 30) hit_critical = 30; //빠른손 히트 수는 최대 30으로 맞춘다.
            double down_parcent = hit_critical * 5; //빠른 손 히트 수 1개마다 재장전 속도를 5%으로 맞춘다.
            double down_time = handred_time * (down_parcent / 100); //빠른 손 히트 수에 따라 줄어들 재장전 시간을 결정한다.
            time -= (int) down_time; //총 재장전 시간에서 빠른 손으로 인해 감소될 시간을 빼준다.
            if (time < 100) time = 100; //만약 총 재장전 시간이 0.1초 미만으로 내려갈 경우 자동으로 0.1초로 맞춰준다. (최소 0.1초)
            hit_critical = 0; //빠른 손을 사용했으므로 다시 빠른 손 히트 수를 0으로 초기화해준다.
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //SimulActivity.txtQuickhand.setText("0");
                    sa.setTxtQuickhand("0"); //화면에 빠른 손이 초기화된 것을 갱신시켜준다.
                }
            });
        } else if (options[2]) { //카멜레온 레그샷으로 인해 재장전 시간 100% 주는 것을 구현한다. (단, 빠른 손이랑 같이 사용할 수는 없으므로 else if로 하였다.)
            double temp_time = (double)time / 2; //위와 동일
            temp_time *= 1.5; //재장전 시간을 150%를 줄인다.
            time -= (int) temp_time; //위와 동일
            if (time < 100) time = 100; // 최소 0.1초보다 낮을 수 없게 한다.
        }
        if (reloadtime != 0) { //재장전 시간이 0초일 경우 재장전 스레드를 사용할 필요가 없으므로 0초가 아닐 경우에만 재장전 스레드를 사용하게 해준다.
            rt.setTime(time); //재장전 스레드의 재장전 시간을 설정해준다.
            rt.pause(false); //재장전 스레드의 일시정지를 풀어준다.
        }
        try {
            Thread.sleep(time); //재장전 시간만큼 스레드를 일시 정지시켜준다.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reloaded = true; //재장전되었다는 것을 설정한다.
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                SimulActivity.progressAmmo.setIndeterminate(false);
            }
        });*/
    }

    private int demage() { //무기 데미지를 내보내는 메소드이다.
        /*int diff_demage = (int)(weapondemage*0.1);
        int ransu = (int)(Math.random()*123456)%(diff_demage*2)-diff_demage;
        int real_demage = (int)weapondemage + ransu;*/
        return (int)weapondemage;
    }

    public void run() { //스레드가 시작할 경우 자동으로 실행된다.
        if (rt != null) rt.start(); //재장전 스레드가 null이 아닌 이상 재장전 스레드도 실행한다.
        SimulActivity.setHealth(getHealth()); //입력받은 체력을 체력을 담당하는 액티비티로 보내준다.
        handler.post(new Runnable() {
            @Override
            public void run() {
                //SimulActivity.progressAmmo.setIndeterminate(false);
                sa.settingIndeterminate_Ammo(false);//탄약 프로그래스가 무한 로딩 상태가 아니도록 설정한다.
            }
        });
        first_health = SimulActivity.getHealth(); //다른 스레드에서 사용할 최대 체력에 100% 차 있는 체력을 대입한다.
        first_sheld = sheld; //위와 동일한 방식으로 방어도를 대입한다.
        int time = (60 * 1000) / (int) rpm; // 발당 딜레이를 계산해준다.
        int now_ammo = (int) ammo; //현재 탄약 수를 탄창당 들어있는 탄약 수로 저장한다.
        int headshot_ransu, critical_ransu, real_demage; //헤드샷, 치명타 난수를 지정하고 결정적으로 들어갈 총 데미지 변수를 생성한다.
        double temp_criticaldemage; //임시 치명타 데미지를 저장하는 변수
        double now_demage; //double 타입을 가진 총 데미지 변수
        double per; //난수를 저장할 변수
        for (int i = 0; i < listDemage.length; i++) {
            final int final_index = i;
            on_critical_list[i] = false;
            on_boom_list[i] = false;
            on_headshot_list[i] = false;
            listDemage[i] = "0";
            handler.post(new Runnable() {
                @Override
                public void run() {
                    sa.setTxtListDemage(final_index, listDemage[final_index]);
                }
            });
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                //SimulActivity.txtSheld.setText(Integer.toString(sheld)+"/"+Integer.toString(sheld));
                //SimulActivity.txtHealth.setText(Integer.toString(health)+"/"+Integer.toString(health));
                sa.setTxtSheld(Integer.toString(sheld)+"/"+Integer.toString(sheld));
                sa.setTxtHealth(Integer.toString(health)+"/"+Integer.toString(health));
            }
        });
        try {
            while (sheld > 0 && !Thread.interrupted() && !end) {
                for (int i = 0; i < listDemage.length-1; i++) {
                    final int final_index = i;
                    listDemage[i] = listDemage[i+1];
                    on_boom_list[i] = on_boom_list[i+1];
                    on_critical_list[i] = on_critical_list[i+1];
                    on_headshot_list[i] = on_headshot_list[i+1];
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.setTxtListDemage(final_index, listDemage[final_index]);
                            if (on_boom_list[final_index]) sa.hitboom_list(final_index);
                            else if (on_critical_list[final_index]) sa.hitCritical_list(final_index);
                            else if (on_headshot_list[final_index]) sa.hitHeadshot_list(final_index);
                            else sa.shelddefaultColor_list(final_index);
                        }
                    });
                }
                boolReset();
                /*activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimulActivity.changeHeadshot(false);
                        SimulActivity.changeCritical(false);
                        SimulActivity.changeBoom(false);
                    }
                });*/
                if (reloaded) {
                    reloaded = false;
                    rt.pause(true);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.changeHeadshot(false);
                        sa.changeCritical(false);
                        sa.changeBoom(false);
                        sa.shelddefaultColor();
                    }
                });
                statue_log = "";
                ammo_log = "";
                now_demage = demage();
                new_weapondemage = demage();
                critical_ransu = (int) (Math.random() * 123456) % 1001;
                headshot_ransu = (int) (Math.random() * 123456) % 1001;
                if (crazy_dmg != 0) {
                    per = crazy_dmg/100;
                    new_weapondemage += weapondemage * per;
                }
                if (eagle_dmg != 0) {
                    per = eagle_dmg/100;
                    new_weapondemage += weapondemage * per;
                }
                if (fire) new_weapondemage += weapondemage * 0.2;
                if (options[1]) new_weapondemage += weapondemage;
                if (front_dmg > 0) new_weapondemage += weapondemage/2;
                if (bumerang) {
                    new_weapondemage += weapondemage;
                    bumerang = false;
                    statue_log += "(부메랑 추가 데미지!)";
                }
                now_demage = new_weapondemage;
                if (headshot_ransu <= headshot*10) {
                    on_headshot = true;
                    per = headshotdemage / 100;
                    System.out.println("Headshot Demage : "+headshotdemage);
                    now_demage += new_weapondemage * per;
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.hitHeadshot();
                            SimulActivity.changeHeadshot(true);
                        }
                    });*/
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.hitHeadshot();
                            sa.changeHeadshot(true);
                        }
                    });
                }
                if (options[0]) {
                    critical += 20;
                    if (critical > 60) critical = 60;
                }
                if (critical_ransu <= critical*10) {
                    on_critical = true;
                    if (quick_hand && hit_critical < 30) {
                        hit_critical++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                                sa.setTxtQuickhand(Integer.toString(hit_critical));
                            }
                        });
                    }
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.changeCritical(true);
                        }
                    });*/
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.changeCritical(true);
                            sa.hitCritical();
                        }
                    });
                    temp_criticaldemage = criticaldemage;
                    if (options[0]) temp_criticaldemage += 50;
                    if (push_critical_dmg != 0) temp_criticaldemage += push_critical_dmg;
                    per = temp_criticaldemage / 100;
                    now_demage += new_weapondemage * per;
                    if (bumerang_true) {
                        per = (int)(Math.random()*1234567)%2;
                        if (per == 1) bumerang = true;
                    }
                }
                per = shelddemage/100;
                now_demage *= 1+per;
                if (elite_true == true) {
                    per = elitedemage/100;
                    now_demage += new_weapondemage * per;
                }
                if (boom) {
                    int ransu = (int)(Math.random()*123456)%100+1;
                    if (ransu <= 5) {
                        on_boom = true;
                        now_demage += (new_weapondemage*2);
                        statue_log += "(무자비 폭발탄!!)";
                        /*activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SimulActivity.hitBoom();
                                SimulActivity.changeBoom(true);
                            }
                        });*/
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sa.hitBoom();
                                sa.changeBoom(true);
                            }
                        });
                    }
                }
                if (seeker_dmg != 0) {
                    per = seeker_dmg/100;
                    now_demage *= 1+per;
                }
                if (pvp_true == true) now_demage *= coefficient;
                real_demage = (int) now_demage;
                if (on_boom) on_boom_list[listDemage.length-1] = true;
                else if (on_critical) on_critical_list[listDemage.length-1] = true;
                else if (on_headshot) on_headshot_list[listDemage.length-1] = true;
                listDemage[6] = Integer.toString(real_demage);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.setTxtListDemage(6, listDemage[6]);
                        if (on_boom_list[6]) sa.hitboom_list(6);
                        else if (on_critical_list[6]) sa.hitCritical_list(6);
                        else if (on_headshot_list[6]) sa.hitHeadshot_list(6);
                        else sa.shelddefaultColor_list(6);
                    }
                });
                /*if (on_boom) sa.hitboom_list(listDemage.length-1);
                else if (on_critical) sa.hitCritical_list(listDemage.length-1);
                else if (on_headshot) sa.hitboom_list(listDemage.length-1);
                else sa.shelddefaultColor_list(listDemage.length-1);*/
                if (end) break;
                per = (int)(Math.random()*1234567)%1000+1;
                if (aiming*10 >= per) {
                    sheld -= real_demage;
                    all_dmg += real_demage;
                    log = "-" + real_demage;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText(log);
                            sa.setTxtNowDemage(log);
                        }
                    });
                    if (hit_critical > 30) {
                        hit_critical--;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                                sa.setTxtQuickhand(Integer.toString(hit_critical));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText("");
                            sa.setTxtNowDemage("");
                        }
                    });
                }
                if (sheld < 0) {
                    out_demage = sheld * (-1);
                    int temp = SimulActivity.getHealth() - out_demage;
                    SimulActivity.setHealth(temp);
                    dec_health = ((double)SimulActivity.getHealth() / (double)first_health) * 10000;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtHealth.setText(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                            //SimulActivity.progressHealth.setProgress((int)dec_health);
                            sa.setTxtHealth(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                            sa.setProgressHealth((int)dec_health);
                        }
                    });
                    sheld = 0;
                }
                if (!bumerang) now_ammo--;
                all_ammo++;
                ammo_log = Integer.toString(now_ammo);
                if (critical_ransu <= (int) critical*10) statue_log += "(치명타!!)";
                if (headshot_ransu <= (int) headshot*10) statue_log += "(헤드샷!!)";
                dec_sheld = ((double)sheld / (double)first_sheld) * 10000;
                dec_ammo = ((double)now_ammo / (double)ammo) * 10000;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //SimulActivity.txtSheld.setText(Integer.toString(sheld)+"/"+first_sheld);
                        sa.setTxtSheld(Integer.toString(sheld)+"/"+first_sheld);
                        //SimulActivity.txtAmmo.setText(ammo_log);
                        sa.setTxtAmmo(ammo_log);
                        //SimulActivity.txtStatue.setText(statue_log);
                        sa.setTxtStatue(statue_log);
                        //SimulActivity.txtAllAmmo.setText(Integer.toString(all_ammo));
                        sa.setTxtAllAmmo(Integer.toString(all_ammo));
                        //SimulActivity.txtAdddemage.setText(Integer.toString(all_dmg));
                        sa.setTxtAdddemage(Integer.toString(all_dmg));
                        //SimulActivity.progressSheld.setProgress((int)dec_sheld);
                        sa.setProgressSheld((int)dec_sheld);
                        //SimulActivity.progressAmmo.setProgress((int)dec_ammo);
                        sa.setProgressAmmo((int)dec_ammo);
                    }
                });
                if (now_ammo == 0 && sheld != 0) {
                    reload();
                    now_ammo += (int) ammo;
                } else {
                    try {
                        this.sleep(time);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            int temp_health;
            if (cluch_true) {
                ct.setFirst_health(health);
                ct.start();
            }
            while (SimulActivity.getHealth() > 0 && !Thread.interrupted() && !end) {
                for (int i = 0; i < listDemage.length-1; i++) {
                    final int final_index = i;
                    listDemage[i] = listDemage[i+1];
                    on_boom_list[i] = on_boom_list[i+1];
                    on_critical_list[i] = on_critical_list[i+1];
                    on_headshot_list[i] = on_headshot_list[i+1];
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.setTxtListDemage(final_index, listDemage[final_index]);
                            if (on_boom_list[final_index]) sa.hitboom_list(final_index);
                            else if (on_critical_list[final_index]) sa.hitCritical_list(final_index);
                            else if (on_headshot_list[final_index]) sa.hitHeadshot_list(final_index);
                            else sa.defaultColor_list(final_index);
                        }
                    });
                }
                boolReset();
                /*activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimulActivity.changeHeadshot(false);
                        SimulActivity.changeCritical(false);
                        SimulActivity.changeBoom(false);
                    }
                });*/
                if (reloaded) {
                    reloaded = false;
                    rt.pause(true);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.changeHeadshot(false);
                        sa.changeCritical(false);
                        sa.changeBoom(false);
                        sa.defaultColor();
                    }
                });
                statue_log = "";
                ammo_log = "";
                now_demage = demage();
                new_weapondemage = demage();
                if (crazy_dmg != 0) {
                    per = crazy_dmg/100;
                    new_weapondemage += weapondemage * per;
                }
                if (eagle_dmg != 0) {
                    per = eagle_dmg/100;
                    new_weapondemage += weapondemage * per;
                }
                if (fire) new_weapondemage += weapondemage * 0.2;
                if (options[1]) new_weapondemage += weapondemage;
                if (front_dmg > 0) new_weapondemage += weapondemage/2;
                critical_ransu = (int) (Math.random() * 123456) % 1001;
                headshot_ransu = (int) (Math.random() * 123456) % 1001;
                if (bumerang) {
                    new_weapondemage += weapondemage;
                    bumerang = false;
                    statue_log += "(부메랑 추가 데미지!)";
                }
                now_demage = new_weapondemage;
                if (headshot_ransu <= headshot*10) {
                    on_headshot = true;
                    per = headshotdemage / 100;
                    now_demage += new_weapondemage * per;
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.hitHeadshot();
                            SimulActivity.changeHeadshot(true);
                        }
                    });*/
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.hitHeadshot();
                            sa.changeHeadshot(true);
                        }
                    });
                }
                if (options[0]) {
                    critical += 20;
                    if (critical > 60) critical = 60;
                }
                if (critical_ransu <= critical*10) {
                    on_critical = true;
                    if (quick_hand && hit_critical < 30) {
                        hit_critical++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                                sa.setTxtQuickhand(Integer.toString(hit_critical));
                            }
                        });
                    }
                    /*activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.changeCritical(true);
                        }
                    });*/
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.changeCritical(true);
                            sa.hitCritical();
                        }
                    });
                    temp_criticaldemage = criticaldemage;
                    if (options[0]) temp_criticaldemage += 50;
                    if (push_critical_dmg != 0) temp_criticaldemage += push_critical_dmg;
                    per = temp_criticaldemage / 100;
                    now_demage += new_weapondemage * per;
                    if (bumerang_true) {
                        per = (int)(Math.random()*1234567)%2;
                        if (per == 1) bumerang = true;
                    }
                }
                per = healthdemage/100;
                now_demage *= 1+per;
                if (elite_true == true) {
                    per = elitedemage/100;
                    now_demage += new_weapondemage * per;
                }
                if (boom) {
                    int ransu = (int)(Math.random()*123456)%100+1;
                    if (ransu <= 5) {
                        on_boom = true;
                        now_demage += (new_weapondemage*2);
                        statue_log += "(무자비 폭발탄!!)";
                        /*activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SimulActivity.changeBoom(true);
                            }
                        });*/
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sa.hitBoom();
                                sa.changeBoom(true);
                            }
                        });
                    }
                }
                if (seeker_dmg != 0) {
                    per = seeker_dmg/100;
                    now_demage *= 1+per;
                }
                if (pvp_true == true) now_demage *= coefficient;
                real_demage = (int) now_demage;
                if (on_boom) on_boom_list[listDemage.length-1] = true;
                else if (on_critical) on_critical_list[listDemage.length-1] = true;
                else if (on_headshot) on_headshot_list[listDemage.length-1] = true;
                listDemage[6] = Integer.toString(real_demage);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.setTxtListDemage(6, listDemage[6]);
                        if (on_boom_list[6]) sa.hitboom_list(6);
                        else if (on_critical_list[6]) sa.hitCritical_list(6);
                        else if (on_headshot_list[6]) sa.hitHeadshot_list(6);
                        else sa.defaultColor_list(6);
                    }
                });
                if (end) break;
                per = (int)(Math.random()*1234567)%1000+1;
                if (aiming*10 >= per) {
                    temp_health = SimulActivity.getHealth() - real_demage;
                    SimulActivity.setHealth(temp_health);
                    all_dmg += real_demage;
                    log = "-" + real_demage;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText(log);
                            sa.setTxtNowDemage(log);
                        }
                    });
                    if (hit_critical > 30) {
                        hit_critical--;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                                sa.setTxtQuickhand(Integer.toString(hit_critical));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText("");
                            sa.setTxtNowDemage("");
                        }
                    });
                }
                if (SimulActivity.getHealth() <= 0) {
                    SimulActivity.setHealth(0);
                    break;
                }
                if (!bumerang) now_ammo--;
                all_ammo++;
                ammo_log = Integer.toString(now_ammo);
                if (critical_ransu <= (int) critical*10) statue_log += "(치명타!!)";
                if (headshot_ransu <= (int) headshot*10) statue_log += "(헤드샷!!)";
                dec_health = ((double)SimulActivity.getHealth() / (double)first_health) * 10000;
                dec_ammo = ((double)now_ammo / (double)ammo) * 10000;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //SimulActivity.txtHealth.setText(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                        sa.setTxtHealth(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                        //SimulActivity.txtAmmo.setText(ammo_log);
                        sa.setTxtAmmo(ammo_log);
                        //SimulActivity.txtStatue.setText(statue_log);
                        sa.setTxtStatue(statue_log);
                        //SimulActivity.txtAllAmmo.setText(Integer.toString(all_ammo));
                        sa.setTxtAllAmmo(Integer.toString(all_ammo));
                        //SimulActivity.txtAdddemage.setText(Integer.toString(all_dmg));
                        sa.setTxtAdddemage(Integer.toString(all_dmg));
                        //SimulActivity.progressHealth.setProgress((int)dec_health);
                        sa.setProgressHealth((int)dec_health);
                        //SimulActivity.progressAmmo.setProgress((int)dec_ammo);
                        sa.setProgressAmmo((int)dec_ammo);
                    }
                });
                if (now_ammo == 0 && SimulActivity.getHealth() != 0) {
                    reload();
                    now_ammo += (int) ammo;
                } else {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            SimulActivity.setHealth(0);
            sheld = 0;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                //SimulActivity.progressHealth.setProgress(0);
                sa.setProgressHealth(0);
                //SimulActivity.progressSheld.setProgress(0);
                sa.setProgressSheld(0);
                //SimulActivity.txtSheld.setText("0");
                sa.setTxtSheld("0");
                //SimulActivity.txtHealth.setText("0");
                sa.setTxtHealth("0");
                //SimulActivity.btnExit.setText("뒤로 가기");
                sa.setBtnExitText("뒤로 가기");
            }
        });
        tt.setStop(true);
        if (cluch_true) ct.setStop(true);
        if (!end) {
            /*activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "시뮬레이션이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });*/
            rt.stopThread();
            sa.setExit(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "시뮬레이션이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        System.out.println("(DemageSimulThread) 정상적으로 종료됨");
    }
}
