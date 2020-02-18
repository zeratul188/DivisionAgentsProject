package com.example.divisionsimulation.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;

class DemageSimulThread extends Thread implements Serializable {
    private double weapondemage, rpm, critical, criticaldemage, headshot, headshotdemage, elitedemage, shelddemage, healthdemage, reloadtime, ammo, aiming;
    private int health, sheld, all_ammo = 0;
    private boolean elite_true = false, pvp_true = false, boom = false, quick_hand = false, cluch_true = false, end = false, bumerang_true = false, bumerang = false, reloaded = false, fire = false;
    private int first_health, first_sheld;
    private double dec_health, dec_sheld, dec_ammo;
    private TimeThread tt;
    private Activity activity;
    private double crazy_dmg, seeker_dmg, push_critical_dmg, eagle_dmg, coefficient;
    private int hit_critical = 0, out_demage, all_dmg = 0;
    private boolean[] options = null;

    private Handler handler = null;

    private CluchThread ct = null;

    private boolean headshot_enable = false;
    private boolean critical_enable = false;

    private ReloadThread rt = null;

    private String log, statue_log = "", ammo_log = "";

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

    public int getSheld() { return this.sheld; }
    public synchronized int getHealth() { return this.health; }

    private void reload() {
        int time = (int)(reloadtime*1000);
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                SimulActivity.progressAmmo.setIndeterminate(true);
            }
        });*/
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimulActivity.txtStatue.setText("재장전 중...");
            }
        });
        if (quick_hand) {
            double handred_time = (double)time / 2;
            if (hit_critical > 30) hit_critical = 30;
            double down_parcent = hit_critical * 5;
            double down_time = handred_time * (down_parcent / 100);
            time -= (int) down_time;
            if (time < 100) time = 100;
            hit_critical = 0;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    SimulActivity.txtQuickhand.setText("0");
                }
            });
        } else if (options[2]) {
            double temp_time = (double)time / 2;
            temp_time *= 1.5;
            time -= (int) temp_time;
            if (time < 100) time = 100;
        }
        if (reloadtime != 0) {
            rt.setTime(time);
            rt.pause(false);
        }
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reloaded = true;
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                SimulActivity.progressAmmo.setIndeterminate(false);
            }
        });*/
    }

    private int demage() {
        /*int diff_demage = (int)(weapondemage*0.1);
        int ransu = (int)(Math.random()*123456)%(diff_demage*2)-diff_demage;
        int real_demage = (int)weapondemage + ransu;*/
        return (int)weapondemage;
    }

    public void run() {
        if (rt != null) rt.start();
        SimulActivity.setHealth(getHealth());
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimulActivity.progressAmmo.setIndeterminate(false);
            }
        });
        first_health = SimulActivity.getHealth();
        first_sheld = sheld;
        int time = (60 * 1000) / (int) rpm;
        int now_ammo = (int) ammo;
        int headshot_ransu, critical_ransu, real_demage;
        double temp_criticaldemage;
        double now_demage;
        double per;
        SimulActivity.txtSheld.setText(Integer.toString(sheld)+"/"+Integer.toString(sheld));
        SimulActivity.txtHealth.setText(Integer.toString(health)+"/"+Integer.toString(health));
        try {
            while (sheld > 0 && !Thread.interrupted() && !end) {
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
                        SimulActivity.changeHeadshot(false);
                        SimulActivity.changeCritical(false);
                        SimulActivity.changeBoom(false);
                        SimulActivity.shelddefaultColor();
                    }
                });
                statue_log = "";
                ammo_log = "";
                now_demage = demage();
                critical_ransu = (int) (Math.random() * 123456) % 1001;
                headshot_ransu = (int) (Math.random() * 123456) % 1001;
                if (bumerang) {
                    now_demage += weapondemage;
                    bumerang = false;
                    statue_log += "(부메랑 추가 데미지!)";
                }
                if (headshot_ransu <= headshot*10) {
                    per = headshotdemage / 100;
                    System.out.println("Headshot Demage : "+headshotdemage);
                    now_demage += weapondemage * per;
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
                            SimulActivity.hitHeadshot();
                            SimulActivity.changeHeadshot(true);
                        }
                    });
                }
                if (options[0]) {
                    critical += 20;
                    if (critical > 60) critical = 60;
                }
                if (critical_ransu <= critical*10) {
                    if (quick_hand && hit_critical < 30) {
                        hit_critical++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
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
                            SimulActivity.changeCritical(true);
                            SimulActivity.hitCritical();
                        }
                    });
                    temp_criticaldemage = criticaldemage;
                    if (options[0]) temp_criticaldemage += 50;
                    if (push_critical_dmg != 0) temp_criticaldemage += push_critical_dmg;
                    per = temp_criticaldemage / 100;
                    now_demage += weapondemage * per;
                    if (bumerang_true) {
                        per = (int)(Math.random()*1234567)%2;
                        if (per == 1) bumerang = true;
                    }
                }
                per = shelddemage/100;
                now_demage *= 1+per;
                if (elite_true == true) {
                    per = elitedemage/100;
                    now_demage += weapondemage * per;
                }
                if (boom) {
                    int ransu = (int)(Math.random()*123456)%100+1;
                    if (ransu <= 5) {
                        now_demage += (demage()*2);
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
                                SimulActivity.hitBoom();
                                SimulActivity.changeBoom(true);
                            }
                        });
                    }
                }
                if (crazy_dmg != 0) {
                    per = crazy_dmg/100;
                    now_demage += weapondemage * per;
                }
                if (eagle_dmg != 0) {
                    per = eagle_dmg/100;
                    now_demage += weapondemage * per;
                }
                if (seeker_dmg != 0) {
                    per = seeker_dmg/100;
                    now_demage *= 1+per;
                }
                if (fire) now_demage += weapondemage * 0.2;
                if (options[1]) now_demage += weapondemage;
                if (pvp_true == true) now_demage *= coefficient;
                real_demage = (int) now_demage;
                if (end) break;
                per = (int)(Math.random()*1234567)%1000+1;
                if (aiming*10 >= per) {
                    sheld -= real_demage;
                    all_dmg += real_demage;
                    log = "-" + real_demage;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.txtNowDemage.setText(log);
                        }
                    });
                    if (hit_critical > 30) {
                        hit_critical--;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.txtNowDemage.setText("");
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
                            SimulActivity.txtHealth.setText(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                            SimulActivity.progressHealth.setProgress((int)dec_health);
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
                        SimulActivity.txtSheld.setText(Integer.toString(sheld)+"/"+first_sheld);
                        SimulActivity.txtAmmo.setText(ammo_log);
                        SimulActivity.txtStatue.setText(statue_log);
                        SimulActivity.txtAllAmmo.setText(Integer.toString(all_ammo));
                        SimulActivity.txtAdddemage.setText(Integer.toString(all_dmg));
                        SimulActivity.progressSheld.setProgress((int)dec_sheld);
                        SimulActivity.progressAmmo.setProgress((int)dec_ammo);
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
                        SimulActivity.changeHeadshot(false);
                        SimulActivity.changeCritical(false);
                        SimulActivity.changeBoom(false);
                        SimulActivity.defaultColor();
                    }
                });
                statue_log = "";
                ammo_log = "";
                now_demage = demage();
                critical_ransu = (int) (Math.random() * 123456) % 1001;
                headshot_ransu = (int) (Math.random() * 123456) % 1001;
                if (bumerang) {
                    now_demage += weapondemage;
                    bumerang = false;
                    statue_log += "(부메랑 추가 데미지!)";
                }
                if (headshot_ransu <= headshot*10) {
                    per = headshotdemage / 100;
                    now_demage += weapondemage * per;
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
                            SimulActivity.hitHeadshot();
                            SimulActivity.changeHeadshot(true);
                        }
                    });
                }
                if (options[0]) {
                    critical += 20;
                    if (critical > 60) critical = 60;
                }
                if (critical_ransu <= critical*10) {
                    if (quick_hand && hit_critical < 30) {
                        hit_critical++;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
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
                            SimulActivity.changeCritical(true);
                            SimulActivity.hitCritical();
                        }
                    });
                    temp_criticaldemage = criticaldemage;
                    if (options[0]) temp_criticaldemage += 50;
                    if (push_critical_dmg != 0) temp_criticaldemage += push_critical_dmg;
                    per = temp_criticaldemage / 100;
                    now_demage += weapondemage * per;
                    if (bumerang_true) {
                        per = (int)(Math.random()*1234567)%2;
                        if (per == 1) bumerang = true;
                    }
                }
                per = healthdemage/100;
                now_demage *= 1+per;
                if (elite_true == true) {
                    per = elitedemage/100;
                    now_demage += weapondemage * per;
                }
                if (boom) {
                    int ransu = (int)(Math.random()*123456)%100+1;
                    if (ransu <= 5) {
                        SimulActivity.hitBoom();
                        now_demage += (demage()*2);
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
                                SimulActivity.changeBoom(true);
                            }
                        });
                    }
                }
                if (crazy_dmg != 0) {
                    per = crazy_dmg/100;
                    now_demage += weapondemage * per;
                }
                if (eagle_dmg != 0) {
                    per = eagle_dmg/100;
                    now_demage += weapondemage * per;
                }
                if (seeker_dmg != 0) {
                    per = seeker_dmg/100;
                    now_demage *= 1+per;
                }
                if (fire) now_demage += weapondemage * 0.2;
                if (options[1]) now_demage += weapondemage;
                if (pvp_true == true) now_demage *= coefficient;
                real_demage = (int) now_demage;
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
                            SimulActivity.txtNowDemage.setText(log);
                        }
                    });
                    if (hit_critical > 30) {
                        hit_critical--;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                SimulActivity.txtQuickhand.setText(Integer.toString(hit_critical));
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SimulActivity.txtNowDemage.setText("");
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
                        SimulActivity.txtHealth.setText(Integer.toString(SimulActivity.getHealth())+"/"+first_health);
                        SimulActivity.txtAmmo.setText(ammo_log);
                        SimulActivity.txtStatue.setText(statue_log);
                        SimulActivity.txtAllAmmo.setText(Integer.toString(all_ammo));
                        SimulActivity.txtAdddemage.setText(Integer.toString(all_dmg));
                        SimulActivity.progressHealth.setProgress((int)dec_health);
                        SimulActivity.progressAmmo.setProgress((int)dec_ammo);
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
                SimulActivity.progressHealth.setProgress(0);
                SimulActivity.progressSheld.setProgress(0);
                SimulActivity.txtSheld.setText("0");
                SimulActivity.txtHealth.setText("0");
                SimulActivity.btnExit.setText("뒤로 가기");
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
            SimulActivity.setExit(true);
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
