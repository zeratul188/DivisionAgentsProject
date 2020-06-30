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
    private int focus = 0, scrifice = 0, sympathy = 0, overwatch = 0, intimidate = 0, wicked = 0, companion = 0, composure = 0, killer = 0;
    private int unstoppable = 0, versatile = 0, vigilance = 0;
    private boolean focus_checked = false, optimist = false, perfect_optimist = false;

    private Handler handler = null; //상위 액티비티 UI를 수정할 핸들러를 가져온다.

    private CluchThread ct = null; //상대 클러치 여부에 따라 스레드를 사용할 변수이다.
    private SimulActivity sa = null;
    private boolean hitted = false;

    private boolean obel = false;
    private int obel_count = 0;

    private String[] listDemage = new String[11]; //데미지 수치들을 저장할 배열 변수 한 줄당 배열 1개씩 차지한다.
    private boolean[] on_headshot_list = new boolean[11]; //각 줄마다 헤드샷 여부를 저장한다.
    private boolean[] on_critical_list = new boolean[11]; //각 줄마다 치명타 여부를 저장한다.
    private boolean[] on_boom_list = new boolean[11]; //각 줄마다 무자비 폭발탄 여부를 저장한다.

    //private boolean headshot_enable = false;
    //private boolean critical_enable = false;

    private ReloadThread rt = null; //재장전 스레드이다. 재장전할 때 게이지가 차는 것을 보여주는 스레드이다.

    private String log, statue_log = "", ammo_log = ""; //액티비티에 표현할 문자열을 저장하는 변수이다.

    private boolean on_headshot = false, on_critical = false, on_boom = false; //현재 탄환의 헤드샷, 치명타, 무자비 폭발탄 발동 여부를 저장한다.

    private int img_ransu, aim_ransu; //무작위 이미지를 선택할 변수이다.

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

    public void setSimulActivity(SimulActivity sa) { this.sa = sa; } //SimulActivity의 액티비티를 가져온다.

    public void setTimeThread(TimeThread tt) {
        this.tt = tt;
    } //타이머 스레드를 불러온다.
    public void setCluchThread(CluchThread ct) { this.ct = ct; } //클러치 스레드를 불러온다.
    public void setReloadThread(ReloadThread rt) { this.rt = rt; } //재장전 스레드를 불러온다.

    public void setWeapondemage(double weapondemage) { this.weapondemage = weapondemage; } //무기 데미지를 가져온다.
    public void setRPM(double rpm) { this.rpm = rpm; } //RPM을 가져온다.
    public void setCritical(double critical) { this.critical = critical; } //치명타 확률을 가져온다.
    public void setCriticaldemage(double criticaldemage) { this.criticaldemage = criticaldemage; } //치명타 데미지를 가져온다.
    public void setHeadshot(double headshot) { this.headshot = headshot; } //헤드샷 확률을 가져온다.
    public void setHeadshotdemage(double headshotdemage) { this.headshotdemage = headshotdemage; } //헤드샷 데미지를 가져온다.
    public void setElitedemage(double elitedemage) { this.elitedemage = elitedemage; } //정예 대상 데미지를 가져온다.
    public void setShelddemage(double shelddemage) { this.shelddemage = shelddemage; } //방어도 대상 데미지를 가져온다.
    public void setHealthdemage(double healthdemage) { this.healthdemage = healthdemage; } //생명력 대상 데미지를 가져온다.
    public void setReloadtime(double reloadtime) { this.reloadtime = reloadtime; } //재장전 시간을 가져온다.
    public void setAmmo(double ammo) { this.ammo = ammo; } //탄창의 탄환 수를 가져온다.
    public synchronized void setHealth(int health) { this.health = health; } //시뮬의 체력을 저장한다.
    public void setSheld(int sheld) { this.sheld = sheld; } //시뮬의 방어도를 저장한다.
    public void setElite_true(boolean elite_true) { this.elite_true = elite_true; } //정예 여부를 가져온다.
    public void setPVP_true(boolean pvp_true) { this.pvp_true = pvp_true; } //PVP 여부를 가져온다.
    public void setCrazy_dmg(int crazy_dmg) { this.crazy_dmg = (double)crazy_dmg; } //광분의 데미지 수치를 가져온다.
    public void setSeeker_dmg(int seeker_dmg) { this.seeker_dmg = (double)seeker_dmg; } //감시병의 여부를 가져온다.
    public void setBoom(boolean boom) { this.boom = boom; } //무자비 수납 여부를 가져온다.
    public void setPush_critical_dmg(int push_critical_dmg) { this.push_critical_dmg = push_critical_dmg; } //중압감의 치명타 데미지 수치를 가져온다.
    public void setEagle_dmg(int eagle_dmg) { this.eagle_dmg = eagle_dmg; } //독수리 집념 데미지 수치를 가져온다.
    public void setQuick_hand(boolean quick_hand) { this.quick_hand = quick_hand; } //빠른 손 여부를 가져온다.
    public void setCluch_true(boolean cluch_true) { this.cluch_true = cluch_true; } //클러치 여부를 가져온다.
    public void setEnd(boolean end) { this.end = end; } //종료시킬지 말지를 결정하는 메소드이다. true가 되면 스레드는 종료된다.
    public void setAiming(double aiming) { this.aiming = aiming; } //명중률을 설정한다.
    public void setBumerang_true(boolean bumerang_true) { this.bumerang_true = bumerang_true; } //부메랑 여부를 가져온다.
    public void setActivity(Activity activity) { this.activity = activity; } //액티비티를 가져온다.
    public void setHandler(Handler handler) { this.handler = handler; } //전 액티비티에서 핸들러를 가져온다. UI 변경 시 사용한다. (스레드에서 UI변경이 어렵기 때문에 핸들러를 사용한다.)
    public void setCoefficient(double coefficient) { this.coefficient = coefficient; } //무기 계수를 설정한다. (PVP 전용)
    public void setOptions(boolean[] options) { this.options = options; } //카멜레온 각각 탤런트 발동 여부를 가져온다.
    public void setFire(boolean fire) { this.fire = fire; } //불꽃 여부를 가져온다.
    public void setFront_dmg(int front_dmg) { this.front_dmg = front_dmg; } //완벽한 근접전의 대가 여부를 가져온다.
    public void setFocus(int focus) { this.focus = focus; }
    public void setFocusChecked(boolean focus_checked) { this.focus_checked = focus_checked; }
    public void setScrifice(int scrifice) { this.scrifice = scrifice; }
    public void setSympathy(int sympathy) { this.sympathy = sympathy; }
    public void setOverwatch(int overwatch) { this.overwatch = overwatch; }
    public void setIntimidate(int intimidate) { this.intimidate = intimidate; }
    public void setObel(boolean obel) { this.obel = obel; }
    public void setWicked(int wicked) { this.wicked = wicked; }
    public void setCompanion(int companion) { this.companion = companion; }
    public void setComposure(int composure) { this.composure = composure; }
    public void setUnstoppable(int unstoppable) { this.unstoppable = unstoppable; }
    public void setVersatile(int versatile) { this.versatile = versatile; }
    public void setVigilance(int vigilance) { this.vigilance = vigilance; }
    public void setKiller(int killer) { this.killer = killer; }
    public void setOptimist(boolean optimist) { this.optimist = optimist; }
    public void setPerfectOptimist(boolean perfect_optimist) { this.perfect_optimist = perfect_optimist; }

    public int getSheld() { return this.sheld; } //방어도를 가져온다.
    public synchronized int getHealth() { return this.health; } //생명력을 가져온다.

    public void boolReset() { //탄환마다 헤드샷, 치명타, 폭발탄 여부가 달라질 수 밖에 없으므로 다시 초기화해주는 메소드이다.
        on_boom = false; //폭발탄 여부를 초기화한다.
        on_critical = false; //치명타 여부를 초기화한다.
        on_headshot = false; //헤드샷 여부를 초기화한다.
        on_boom_list[on_boom_list.length-1] = false; //마지막 줄의 폭발탄 여부를 초기화한다.
        on_critical_list[on_critical_list.length-1] = false; //마지막 줄의 치명타 여부를 초기화한다.
        on_headshot_list[on_headshot_list.length-1] = false; //마지막 줄의 헤드샷 여부를 초기화한다.
    }

    /*
    위 메소드들은 다른 클래스에서 이 스레드에 대한 변수 수정시 사용되는 메소드들이다.
     */

    private void reload() { //재장전 시 사용되는 메소드이다.
        obel_count = 0;
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

    private int demage(int now_ammo) { //무기 데미지를 내보내는 메소드이다.
        /*int diff_demage = (int)(weapondemage*0.1);
        int ransu = (int)(Math.random()*123456)%(diff_demage*2)-diff_demage;
        int real_demage = (int)weapondemage + ransu;*/
        double result = weapondemage;
        if (optimist) result *= 1 + (double)calOptimist(now_ammo, (int)ammo)/100.0;
        return (int)result; //무기 데미지를 반환한다.
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
        for (int i = 0; i < listDemage.length; i++) { //스레드 시작 시 전부 초기화를 한다.
            final int final_index = i; //핸들러에서는 final 변수가 필요하므로 final_index에 저장하여 사용한다.
            on_critical_list[i] = false;
            on_boom_list[i] = false;
            on_headshot_list[i] = false;
            listDemage[i] = "0";
            /*
            위는 초기화하는 과정이다.
             */
            handler.post(new Runnable() {
                @Override
                public void run() {
                    sa.setTxtListDemage(final_index, listDemage[final_index]); //초기화한 값들을 UI에 적용시킨다.
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

        if (killer != 0) criticaldemage += killer;

        try {
            while (sheld > 0 && !Thread.interrupted() && !end) { //방어도가 소진되거나 스레드가 인터럽트되거나 종료시키게 된다면 자동으로 종료되게 된다.
                per = (int)(Math.random()*1234567)%1000+1; //명중률에 해당하는 1~1000까지의 난수를 생성한다. (명중률이 소수점 1자리까지 있으므로 소수점까지 포함하여 1000으로 잡는다.)
                if (aiming*10 >= per) hitted = true;
                for (int i = 0; i < listDemage.length-1; i++) {
                    final int final_index = i; //위와 동일
                    listDemage[i] = listDemage[i+1];
                    on_boom_list[i] = on_boom_list[i+1];
                    on_critical_list[i] = on_critical_list[i+1];
                    on_headshot_list[i] = on_headshot_list[i+1];
                    /*
                    탄환이 새로 발사될 때마다 한줄씩 올라가므로 배열도 한칸씩 밀어준다.
                    단, 마지막 줄에서는 밀릴 것이 없기 때문에 마지막은 제외된다.
                     */
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.setTxtListDemage(final_index, listDemage[final_index]);
                            if (on_boom_list[final_index]) sa.hitboom_list(final_index);
                            else if (on_critical_list[final_index]) sa.hitCritical_list(final_index);
                            else if (on_headshot_list[final_index]) sa.hitHeadshot_list(final_index);
                            else sa.shelddefaultColor_list(final_index);
                            //변경된 값들을 UI에 적용시킨다.
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
                if (reloaded) { //재장된 되었었으면 작동한다.
                    reloaded = false; //다시 재장전 여부를 초기화한다.
                    rt.pause(true); //재장전때만 작동하는 재장전 스레드를 일시정지 시킨다.
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.changeHeadshot(false);
                        sa.changeCritical(false);
                        sa.changeBoom(false);
                        sa.shelddefaultColor();
                        //탄환이 다시 발사되는 시점이므로 치명타, 헤드샷 등을 초기화한다.
                    }
                });
                statue_log = ""; //상태메시지를 초기화한다.
                ammo_log = ""; //탄약 수를 초기화한다.
                now_demage = demage(now_ammo); //현재 데미지에 무기 데미지를 적용시킨다.
                new_weapondemage = demage(now_ammo); //위와 동일
                critical_ransu = (int) (Math.random() * 123456) % 1001; //치명타 확률에 적용될 소수점 첫째까지이므로 1000까지 무작위 난수로 잡는다.
                headshot_ransu = (int) (Math.random() * 123456) % 1001; //위와 동일하게 헤드샷 확률에 적용될 무작위 난수를 잡는다.
                if (crazy_dmg != 0) { //광분의 수치가 0이면 꺼져있거나 방어도가 100%일 경우이다. 그 외이면 광분이 켜져 있고 방어도가 일부 또는 전부 소진되어 있는 상태이다.
                    per = crazy_dmg/100; //40%데미지면 0.4로 변경시켜준다. 나중에 무기 데미지에 적용한다.
                    new_weapondemage += weapondemage * per; //현재 데미지에 적용시켜 넣는다.
                }
                if (eagle_dmg != 0) { //집념의 여부에 따라 작동한다. 0보다 크면 작동한다. (0 또는 35이다.)
                    per = eagle_dmg/100; //위와 동일
                    new_weapondemage += weapondemage * per; //위와 동일
                }
                if (fire) new_weapondemage += weapondemage * 0.2; //불꽃 여부에 따라 20% 무기 데미지에 추가하여 현재 데미지에 추가한다.
                if (options[1]) new_weapondemage += weapondemage; //위와 동일하게 카멜레온 바디샷 기준에 맞춰 100% 무기 데미지를 추가한다.
                if (front_dmg > 0) new_weapondemage += weapondemage/2; //위와 동일하게 완벽한 근접전의 대가를 적용시킨다. (무기 데미지의 50%)
                if (bumerang) { //부메랑의 여부에 따라 작동한다.
                    new_weapondemage += weapondemage; //부메랑 추가 데미지가 무기데미지의 100%이므로 추가한다.
                    bumerang = false; //부메랑이 한번 작동하여 데미지가 추가되었으므로 초기화한다.
                    statue_log += "(부메랑 추가 데미지!)"; //상태메시지에 부메랑 발동 여부를 추가한다.
                }
                now_demage = new_weapondemage;
                if (headshot_ransu <= headshot*10) { //헤드샷 확률이 난수보다 클 경우에 작동한다.
                    on_headshot = true; //헤드샷 참으로 바꾼다.
                    per = headshotdemage / 100; //헤드샷 데미지가 예를 들어 50%면 50%만큼이므로 100으로 나눠 0.5로 바꿔준다.
                    //System.out.println("Headshot Demage : "+headshotdemage);
                    now_demage += new_weapondemage * per; //현재 데미지에 헤드샷 데미지를 추가한다.
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
                            sa.hitHeadshot(); //헤드샷시 현재 데미지 색상이 빨간색으로 변하게 해준다.
                            sa.changeHeadshot(true); //헤드샷 아이콘이 보이도록 한다.
                        }
                    });
                }
                if (options[0]) { //카멜레온 헤드샷 버프 적용시 작동한다. (다른 것과 중복 가능)
                    critical += 20; //치명타 확률을 20% 증가시킨다.
                    if (critical > 60) critical = 60; //치명타 확률의 최대치가 60%이므로 60%가 넘어가게 되면
                }
                if (critical_ransu <= critical*10) { //치명타 확률이 난수보다 작거나 같을 경우 작동한다.
                    if (hitted && obel && obel_count < 15) obel_count++;
                    on_critical = true; //치명타를 참으로 바꾼다.
                    if (hitted && quick_hand && hit_critical < 30) { //빠른손이 적용되어 있고 빠른 손 히트 수가 30 미만일 경우에만 작동한다. 빠른 손 히트 수는 30이 최대치이기 때문이다.
                        hit_critical++; //치명타가 작동했으므로 빠른 손 히트 수를 1개 증가시킨다.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                                sa.setTxtQuickhand(Integer.toString(hit_critical)); //빠른 손 히트 수가 늘어났으므로 UI에 갱신시켜준다.
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
                            sa.changeCritical(true); //치명타 아이콘을 보여준다.
                            sa.hitCritical(); //치명타 색깔은 주황색으로 데미지를 보여준다.
                        }
                    });
                    temp_criticaldemage = criticaldemage; //임시 크리티컬 데미지를 저장시킨다.
                    if (options[0]) temp_criticaldemage += 50; //카멜레온 헤드샷 버프로 인해 치명타 데미지 50%를 증가한다.
                    if (push_critical_dmg != 0) temp_criticaldemage += push_critical_dmg; //중압감의 효과로 소진된 방어도에 따라 치명타 데미지를 추가한다.
                    per = temp_criticaldemage / 100; //0.? 단위로 바꿔준다.
                    now_demage += new_weapondemage * per; //현재 데미지에 치명타 데미지를 추가시킨다. (중압감, 카멜레온 헤드샷 버프 포함)
                    if (bumerang_true) { //부메랑이 적용되면 작동한다.
                        per = (int)(Math.random()*1234567)%2; //부메랑 작동 확률이 50%이므로 난수를 0 또는 1로 잡아 확률 50%를 만들어준다.
                        if (per == 1) bumerang = true; //난수가 1일 경우 부메랑을 작동시킨다. (난수가 1 또는 0이므로 50% 확률이 된다.)
                    }
                }
                per = shelddemage/100; //방어도 데미지를 0.?로 바꿔준다.
                now_demage *= 1+per; //현재 데미지에 추가시킨다.
                if (elite_true == true) { //정예 대상 여부가 참이면 작동한다.
                    per = elitedemage/100; //정예 대상 데미지를 0.?단위로 바꿔준다.
                    now_demage += new_weapondemage * per; //현재 데미지에 정예 대상 데미지를 추가시킨다.
                }
                if (boom) { //무자비가 수납되어 있으면 작동한다.
                    int ransu = (int)(Math.random()*123456)%100+1; //1~100 중 무작위 난수를 생성한다.
                    if (ransu <= 5) { //난수가 5 이하일 경우 작동한다.(확률 : 5%)
                        on_boom = true; //무자비 폭발탄 작동됨을 참으로 바꾼다.
                        now_demage += (new_weapondemage*2); //무기 데미지의 100%를 추가시킨다.
                        statue_log += "(무자비 폭발탄!!)"; //상태메시지에 무자비 폭발탄을 추가한다.
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
                                sa.hitBoom(); //무자비 폭발탄은 폭발데미지이므로 노란색으로 바꾼다.
                                sa.changeBoom(true); //무자비 폭발탄 아이콘을 보여준다.
                            }
                        });
                    }
                }
                if (seeker_dmg != 0) { //감시병 여부에 따라 작동한다. 0이면 꺼져있는 것이며 20이면 감시병이 켜져있다는 것이다.
                    per = seeker_dmg/100; //감시병 데미지를 0.?로 바꿔준다. 감시병 데미지가 종합데미지의 20% 추가이므로 0.2가 된다.
                    now_demage *= 1+per; //무기데미지가 아닌 종합데미지에 0.2를 곱해준다.
                }
                now_demage = talentCal(now_demage);
                if (pvp_true == true) now_demage *= coefficient; //pvp 대상은 데미지가 낮아져야 하므로 무기별 계수를 받아 현재데미지에 곱해준다. (계수들은 전부 1보다 작다.)
                real_demage = (int) now_demage; //데미지는 정수로 빠지므로 double 타입을 int 타입인 변수에 저장시킨다.
                if (on_boom) on_boom_list[listDemage.length-1] = true; //마지막 데미지 수치에 무자비 폭발탄이 참일 경우 참으로 바꿔준다.
                else if (on_critical) on_critical_list[listDemage.length-1] = true; //위와 동일한 방식
                else if (on_headshot) on_headshot_list[listDemage.length-1] = true; //위와 동일한 방식
                listDemage[10] = Integer.toString(real_demage); //마지막 데미지 수치를 현재 탄환의 데미지로 문자열로 변환한 다음 저장한다.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.setTxtListDemage(10, listDemage[10]);
                        if (on_boom_list[10]) sa.hitboom_list(10);
                        else if (on_critical_list[10]) sa.hitCritical_list(10);
                        else if (on_headshot_list[10]) sa.hitHeadshot_list(10);
                        else sa.shelddefaultColor_list(10);
                    }
                });
                /*
                마지막 데미지 수치에 적용된 값들을 UI에 갱신시킨다.
                 */
                /*if (on_boom) sa.hitboom_list(listDemage.length-1);
                else if (on_critical) sa.hitCritical_list(listDemage.length-1);
                else if (on_headshot) sa.hitboom_list(listDemage.length-1);
                else sa.shelddefaultColor_list(listDemage.length-1);*/
                if (end) break; //종료 명령을 받으면 종료시킨다.
                if (hitted) { //명중률 확률이 난수보다 클 경우 작동한다.
                    if (on_headshot) { //헤드샷이 적용되었을 경우 작동한다.
                        img_ransu = (int)(Math.random()*1234567)%2+2; //2 또는 3인 난수를 생성한다. (확률 : 50%)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sa.setImgTake(img_ransu); //타격 이미지를 보여준다. (2 또는 3이므로 빨간색 타켓 이미지를 보여준다.)
                            }
                        });
                    } else {
                        img_ransu = (int)(Math.random()*1234567)%2; //0 또는 1인 난수를 생성한다. (확률 : 50%)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sa.setImgTake(img_ransu); //타격 이미지를 보여준다. (0 또는 1이므로 하얀색 타켓 이미지를 보여준다.)
                            }
                        });
                    }
                    sheld -= real_demage; //방어도에서 현재 데미지를 뺀다.
                    all_dmg += real_demage; //누적 데미지에 현재 데미지를 추가한다.
                    log = "-" + real_demage; //기록에 현재 데미지를 추가한다. (자동으로 문자열로 추가된다.)
                    aim_ransu = (int)(Math.random()*1234567)%10+1; //1~10까지의 무작위 난수를 생성한다.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText(log);
                            sa.setTxtNowDemage(log); //기록을 현재 데미지 UI에 갱신한다. 현재 데미지를 출력된다.
                            sa.setImgAim(aim_ransu, true); //명중률 맞을 때 이미지가 10개이므로 무작위 난수를 받고 타격 되었으므로 참값을 받는다.
                        }
                    });
                    if (hit_critical > 30) { //빠른 손이 30보다 클 경우에 작동한다.
                        hit_critical--; //30 이상이면 30을 유지해야 하므로 증가한 히트수를 줄여준다.
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                                sa.setTxtQuickhand(Integer.toString(hit_critical)); //변경된 히트수를 갱신시킨다.
                            }
                        });
                    }
                } else {
                    aim_ransu = (int)(Math.random()*1234567)%5+1; //1~5까지의 무작위 난수를 생성한다.
                    listDemage[10] = ""; //빗나갔으므로 데미지 수치를 보여주지 않는다.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText("");
                            sa.setTxtNowDemage("");
                            sa.setImgTake(4);
                            sa.setTxtListDemage(10, "");
                            sa.setImgAim(aim_ransu, false);
                        }
                    });
                    /*
                    변경된 값들을 UI에 갱신시켜준다.
                     */
                }
                if (sheld < 0) { //방어도가 0보다 작아졌을 경우 작동한다.
                    out_demage = sheld * (-1); //방어도가 소진되었고 그 데미지가 남은 방어도보다 컸으면 그만큼 생명력에서 빼야하므로 마이너스가 된 방어도를 다시 양수로 바꿔 다른 변수에 저장한다.
                    int temp = SimulActivity.getHealth() - out_demage; //총 생명력에서 남은 데미지를 뺀다.
                    SimulActivity.setHealth(temp); //SimulActivity에서 생명력을 담당하므로 갱신시켜준다.
                    dec_health = ((double)SimulActivity.getHealth() / (double)first_health) * 10000; //프로그레스 바의 진행도를 설정할 변수에 변경된 생명력 진행도를 저장한다.
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtHealth.setText(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                            //SimulActivity.progressHealth.setProgress((int)dec_health);
                            sa.setTxtHealth(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                            sa.setProgressHealth((int)dec_health);
                        }
                    });
                    /*
                    변경된 값들을 UI에 갱신시켜준다.
                     */
                    sheld = 0; //방어도가 마이너스가 될 수 없으므로 방어도를 0으로 바꿔준다.
                }
                if (!bumerang) now_ammo--; //부메랑이 적용되어 있으면 탄환이 소모되지 않으므로 줄어들지 않고 적용되어 있지 않으면 남은 탄환 수를 줄인다.
                all_ammo++; //사용한 탄환 수를 추가시킨다.
                ammo_log = Integer.toString(now_ammo); //남은 탄약 수 문자열에 현재 남은 탄약 수를 문자열로 바꿔 저장한다.
                if (critical_ransu <= (int) critical*10) statue_log += "(치명타!!)"; //치명타일 경우 상태메시지에 추가한다.
                if (headshot_ransu <= (int) headshot*10) statue_log += "(헤드샷!!)"; //위와 동일한 방식
                dec_sheld = ((double)sheld / (double)first_sheld) * 10000; //방어도 진행도를 설정한다.
                dec_ammo = ((double)now_ammo / (double)ammo) * 10000; //남은 탄약수 진행도를 설정한다.
                hitted = false;
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
                /*
                변경된 값들을 UI에 갱신시켜준다.
                 */
                if (now_ammo == 0 && sheld != 0) { //방어도가 남아있고 남은 탄약 수가 없으면 작동한다.
                    reload(); //재장전을 한다. (재장전 메소드 실행)
                    now_ammo += (int) ammo; //현재 탄약 수에 기존 탄약 수를 추가한다.
                } else {
                    try {
                        this.sleep(time); //RPM으로 인한 탄약간 딜레이만큼 스레드를 일시 정지한다.
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            int temp_health; //임시 생명력을 저장할 변수를 생성한다.
            if (cluch_true) { //클러치 여부가 적용되어 있으면 작동한다.
                ct.setFirst_health(health); //클러치 스레드에 최초 생명력을 저장한다.
                ct.start(); //클러치 스레드를 작동시킨다.
            }
            /*
            아래 생명력 관련된 구절은 위 방어도 구절과 거의 동일하므로 주석은 생략한다.
            방어도를 생명력으로 바꾼 것 외에는 변경점이 거의 없다.
             */
            while (SimulActivity.getHealth() > 0 && !Thread.interrupted() && !end) {
                per = (int)(Math.random()*1234567)%1000+1; //명중률에 해당하는 1~1000까지의 난수를 생성한다. (명중률이 소수점 1자리까지 있으므로 소수점까지 포함하여 1000으로 잡는다.)
                if (aiming*10 >= per) hitted = true; //명중했으면 hiteed를 참으로 바꾼다. 명중했을때만 적용되는 것을 작동시키는데 사용된다.
                for (int i = 0; i < listDemage.length-1; i++) { //데미지 목록이 표시될 수 있는 만큼 반복시킨다.
                    final int final_index = i; //handler안에 있는 내부 메소드에 사용할 인덱스를 생성한다. final로 사용해야 내부 메소드에서도 사용이 가능하다. (또는 전역변수로 생성해도 된다.)
                    listDemage[i] = listDemage[i+1]; //탄약이 사용될때마다 1줄씩 올려줘야하므로 i+1번째 변수값을 i번째 변수값으로 옮겨준다. 예를 들어 3번째 변수값을 2번재 변수값에 넣는다.
                    on_boom_list[i] = on_boom_list[i+1]; //위와 동일
                    on_critical_list[i] = on_critical_list[i+1]; //위와 동일
                    on_headshot_list[i] = on_headshot_list[i+1]; //위와 동일
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sa.setTxtListDemage(final_index, listDemage[final_index]); //액티비티에 적용시킨다.
                            if (on_boom_list[final_index]) sa.hitboom_list(final_index); //폭발물(무자비 폭발탄)일 경우 글자색을 노란색으로 변경해준다.
                            else if (on_critical_list[final_index]) sa.hitCritical_list(final_index); //위와 동일한 방식
                            else if (on_headshot_list[final_index]) sa.hitHeadshot_list(final_index); //위와 동일한 방식
                            else sa.defaultColor_list(final_index); //치명타, 헤드샷, 폭발물 전부 아니라면 기본색(흰색)으로 변경한다.
                        }
                    });
                }
                boolReset(); //헤드샷, 치명타, 폭발탄 등 여부를 초기화시켜준다.
                /*activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimulActivity.changeHeadshot(false);
                        SimulActivity.changeCritical(false);
                        SimulActivity.changeBoom(false);
                    }
                });*/
                if (reloaded) { //제장전 되었을 경우 작동한다.
                    reloaded = false; //재장전 여부를 거짓으로 초기화한다.
                    rt.pause(true); //재장전 스레드를 일시정지한다.
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() { //현재 데미지 색을 초기화하는 과정
                        sa.changeHeadshot(false);
                        sa.changeCritical(false);
                        sa.changeBoom(false);
                        sa.defaultColor();
                    }
                });
                statue_log = ""; //상태 메시지를 초기화한다.
                ammo_log = ""; //탄약 메시지를 초기화한다.
                now_demage = demage(now_ammo);
                new_weapondemage = demage(now_ammo);
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
                    if (hitted && obel && obel_count < 15) obel_count++;
                    on_critical = true;
                    if (hitted && quick_hand && hit_critical < 30) {
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
                now_demage = talentCal(now_demage);
                if (pvp_true == true) now_demage *= coefficient;
                real_demage = (int) now_demage;
                if (on_boom) on_boom_list[listDemage.length-1] = true;
                else if (on_critical) on_critical_list[listDemage.length-1] = true;
                else if (on_headshot) on_headshot_list[listDemage.length-1] = true;
                listDemage[10] = Integer.toString(real_demage);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sa.setTxtListDemage(10, listDemage[10]);
                        if (on_boom_list[10]) sa.hitboom_list(10);
                        else if (on_critical_list[10]) sa.hitCritical_list(10);
                        else if (on_headshot_list[10]) sa.hitHeadshot_list(10);
                        else sa.defaultColor_list(10);
                    }
                });
                if (end) break;
                if (hitted) {
                    if (on_headshot) {
                        img_ransu = (int)(Math.random()*1234567)%2+2;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sa.setImgTake(img_ransu);
                            }
                        });
                    } else {
                        img_ransu = (int)(Math.random()*1234567)%2;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sa.setImgTake(img_ransu);
                            }
                        });
                    }
                    temp_health = SimulActivity.getHealth() - real_demage;
                    SimulActivity.setHealth(temp_health);
                    all_dmg += real_demage;
                    log = "-" + real_demage;
                    aim_ransu = (int)(Math.random()*1234567)%10+1;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText(log);
                            sa.setTxtNowDemage(log);
                            sa.setImgAim(aim_ransu, true);
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
                    aim_ransu = (int)(Math.random()*1234567)%5+1;
                    listDemage[10] = "";
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //SimulActivity.txtNowDemage.setText("");
                            sa.setTxtNowDemage("");
                            sa.setImgTake(4);
                            sa.setTxtListDemage(10, "");
                            sa.setImgAim(aim_ransu, false);
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
                hitted = false;
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
        tt.setStop(true); //방어도, 생명력이 전부 소진되어 적이 퇴치되었으므로 타이머 스레드를 정지시킨다.
        if (cluch_true) ct.setStop(true); //클러치 여부가 적용되어 있다면 클러치 스레드도 정지시킨다.
        rt.stopThread(); //재장전 스레드를 정지시킨다.
        if (!end) { //강제 종료된 것이 아니라면 작동한다.
            /*activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "시뮬레이션이 종료되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });*/
            sa.setExit(true);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "시뮬레이션이 종료되었습니다.", Toast.LENGTH_SHORT).show(); //토스트로 시뮬이 정상적으로 종료되었다는 것을 알려준다.
                }
            });
        }
        System.out.println("(DemageSimulThread) 정상적으로 종료됨");
    }

    private double talentCal(double now_demage) {
        double per, result = now_demage;
        int sum = 0;
        if (focus_checked) sum += focus;
        if (scrifice != 0) sum += scrifice;
        if (sympathy != 0) sum += sympathy;
        if (overwatch != 0) sum += overwatch;
        if (intimidate != 0) sum += intimidate;
        if (obel) sum += obel_count;
        if (wicked != 0) sum += wicked;
        if (companion != 0) sum += companion;
        if (composure != 0) sum += composure;
        if (unstoppable != 0) sum += unstoppable;
        if (versatile != 0) sum += versatile;
        if (vigilance != 0) sum += vigilance;
        per = (double)sum/100.0;
        result *= 1+per;
        return result;
    }

    private int calOptimist(int ammo, int max_ammo) {
        double percent = ((double)ammo/(double)max_ammo)*100.0;
        int bonus;
        if (perfect_optimist) bonus = 4;
        else bonus = 3;
        int cal;
        if (percent >= 0 && percent < 10) cal = bonus*9;
        else if (percent >= 10 && percent < 20) cal = bonus*8;
        else if (percent >= 20 && percent < 30) cal = bonus*7;
        else if (percent >= 30 && percent < 40) cal = bonus*6;
        else if (percent >= 40 && percent < 50) cal = bonus*5;
        else if (percent >= 50 && percent < 60) cal = bonus*4;
        else if (percent >= 60 && percent < 70) cal = bonus*3;
        else if (percent >= 70 && percent < 80) cal = bonus*2;
        else if (percent >= 80 && percent < 90) cal = bonus*1;
        else cal = 0;
        return cal;
    }
}
