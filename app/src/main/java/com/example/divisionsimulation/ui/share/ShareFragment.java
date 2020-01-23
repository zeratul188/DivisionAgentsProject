package com.example.divisionsimulation.ui.share;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.divisionsimulation.R;

public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;

    final private int BIG = 1234567;

    private Button btnLitezone, btnDarkzone, btnRaid, btnRaidbox, btnReset;
    private TextView txtSpecial, txtNamed, txtGear, txtBrand;

    private int special = 0, named = 0, gear = 0, brand = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        /*final TextView textView = root.findViewById(R.id.text_share);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        btnLitezone = root.findViewById(R.id.btnLitezone);
        btnDarkzone = root.findViewById(R.id.btnDarkzone);
        btnRaid = root.findViewById(R.id.btnRaid);
        btnRaidbox = root.findViewById(R.id.btnRaidbox);
        btnReset = root.findViewById(R.id.btnReset);

        txtSpecial = root.findViewById(R.id.txtSpecial);
        txtNamed = root.findViewById(R.id.txtNamed);
        txtGear = root.findViewById(R.id.txtGear);
        txtBrand = root.findViewById(R.id.txtBrand);

        final Itemlist il = new Itemlist();

        final View dialogView = getLayoutInflater().inflate(R.layout.itemlayout, null);

        final TextView txtName = dialogView.findViewById(R.id.txtName);
        final TextView txtType = dialogView.findViewById(R.id.txtType);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /*
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        */

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                special = 0;
                named = 0;
                gear = 0;
                brand = 0;
                txtSpecial.setText("0");
                txtNamed.setText("0");
                txtGear.setText("0");
                txtBrand.setText("0");
            }
        });

        btnLitezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                txtName.setTextColor(Color.parseColor("#000000"));
                if (percent(1, 1000) <= 10) { //특급 장비
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    txtSpecial.setText(Integer.toString(special));
                    if (percent(1, 2) == 1) { //무기
                        pick = percent(0, il.getSpecialweapon_Length());
                        txtName.setText(il.getSpecialweapon(pick));
                        txtType.setText(il.getSpecialweapon_type(pick));
                    } else { //보호장구
                        pick = percent(0, il.getSheldspecial_Length());
                        txtName.setText(il.getSheldspecial(pick));
                        txtType.setText(il.getSheldspecial_type(pick));
                    }
                } else if (percent(1, 1000) <= 30) { //네임드 장비
                    named++;
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));
                    } else { //sheld
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));
                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                               temp = percent(0, il.getWeaponlist1_Length());
                               txtName.setText(il.getWeaponlist1(temp));
                               txtType.setText(il.getWeapontype(pick));
                               break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        pick = percent(1, 100);
                        if (pick <= 10) { //gear
                            gear++;
                            txtGear.setText(Integer.toString(gear));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            txtName.setText(il.getSheldbrand(pick));
                        }
                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                builder.setPositiveButton("확인", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnDarkzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                txtName.setTextColor(Color.parseColor("#000000"));
                if (percent(1, 1000) <= 15) { //특급 장비
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    txtSpecial.setText(Integer.toString(special));
                    txtName.setText("역병");
                    txtType.setText("경기관총");
                } else if (percent(1, 1000) <= 30) { //네임드 장비
                    named++;
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_dark_Length());
                        txtName.setText(il.getNamedweapon_dark(pick));
                        txtType.setText(il.getNamedweapon_dark_type(pick));
                    } else { //sheld
                        pick = percent(0, il.getNamedsheld_dark_Length());
                        txtName.setText(il.getNamedsheld_dark(pick));
                        txtType.setText(il.getNamedsheld_dark_type(pick));
                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        pick = percent(1, 100);
                        if (pick <= 10) { //gear
                            gear++;
                            txtGear.setText(Integer.toString(gear));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            txtName.setText(il.getSheldbrand(pick));
                        }
                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                builder.setPositiveButton("확인", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnRaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                txtName.setTextColor(Color.parseColor("#000000"));
                if (percent(1, 1000) <= 15) { //특급 장비
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    txtSpecial.setText(Integer.toString(special));
                    pick = percent(0, il.getSpecialweapon_raid_Length());
                    txtName.setText(il.getSpecialweapon_raid(pick));
                    txtType.setText(il.getSpecialweapon_raid_type(pick));
                } else if (percent(1, 1000) <= 40) { //네임드 장비
                    named++;
                    txtNamed.setText(Integer.toString(named));
                    txtName.setTextColor(Color.parseColor("#c99700"));
                    if (percent(1, 2) == 1) { //weapon
                        pick = percent(0, il.getNamedweapon_lite_Length());
                        txtName.setText(il.getNamedweapon_lite(pick));
                        txtType.setText(il.getNamedweapon_lite_type(pick));
                    } else { //sheld
                        pick = percent(0, il.getNamedsheld_lite_Length());
                        txtName.setText(il.getNamedsheld_lite(pick));
                        txtType.setText(il.getNamedsheld_lite_type(pick));
                    }
                } else { //기타 장비
                    if (percent(1,2) == 1) { //weapon
                        brand++;
                        txtBrand.setText(Integer.toString(brand));
                        pick = percent(0, il.getWeapontype_Length());
                        int temp;
                        switch (pick) {
                            case 0: //돌격소총
                                temp = percent(0, il.getWeaponlist1_Length());
                                txtName.setText(il.getWeaponlist1(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 1: //소총
                                temp = percent(0, il.getWeaponlist2_Length());
                                txtName.setText(il.getWeaponlist2(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 2: //지정사수소총
                                temp = percent(0, il.getWeaponlist3_Length());
                                txtName.setText(il.getWeaponlist3(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 3: //기관단총
                                temp = percent(0, il.getWeaponlist4_Length());
                                txtName.setText(il.getWeaponlist4(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 4: //경기관총
                                temp = percent(0, il.getWeaponlist5_Length());
                                txtName.setText(il.getWeaponlist5(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 5: //산탄총
                                temp = percent(0, il.getWeaponlist6_Length());
                                txtName.setText(il.getWeaponlist6(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            case 6: //권총
                                temp = percent(0, il.getWeaponlist7_Length());
                                txtName.setText(il.getWeaponlist7(temp));
                                txtType.setText(il.getWeapontype(pick));
                                break;
                            default:
                                txtName.setText("Error");
                                txtType.setText("Error");
                        }

                    } else { //sheld
                        pick = percent(0, il.getSheldtype_Length());
                        txtType.setText(il.getSheldtype(pick));
                        pick = percent(1, 100);
                        if (pick <= 10) { //gear
                            gear++;
                            txtGear.setText(Integer.toString(gear));
                            pick = percent(0, il.getSheldgear_Length());
                            txtName.setText(il.getSheldgear(pick));
                        } else { //brand
                            brand++;
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getSheldbrand_Length());
                            txtName.setText(il.getSheldbrand(pick));
                        }
                    }
                }

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                builder.setPositiveButton("확인", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnRaidbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pick;
                txtName.setTextColor(Color.parseColor("#000000"));
                String name = "", type = "";
                if (percent(1, 100) <= 10) {
                    txtName.setTextColor(Color.parseColor("#ff3c00"));
                    special++;
                    txtSpecial.setText(Integer.toString(special));
                    name += "독수리를 거느린 자\n";
                    type += "돌격소총\n";
                }
                for (int i = 0; i < 5; i++) {
                    if (percent(1, 1000) <= 10) { //특급 장비
                        txtName.setTextColor(Color.parseColor("#ff3c00"));
                        special++;
                        txtSpecial.setText(Integer.toString(special));
                        pick = percent(0, il.getSpecialweapon_Length());
                        if (i != 4) {
                            name += il.getSpecialweapon(pick)+"\n";
                            type += il.getSpecialweapon_type(pick)+"\n";
                        } else {
                            name += il.getSpecialweapon(pick);
                            type += il.getSpecialweapon_type(pick);
                        }
                        //txtName.setText(il.getSpecialweapon(pick));
                        //txtType.setText(il.getSpecialweapon_type(pick));
                    } else if (percent(1, 1000) <= 30) { //네임드 장비
                        named++;
                        txtNamed.setText(Integer.toString(named));
                        txtName.setTextColor(Color.parseColor("#c99700"));
                        if (percent(1, 2) == 1) { //weapon
                            pick = percent(0, il.getNamedweapon_lite_Length());
                            if (i != 4) {
                                name += il.getNamedweapon_lite(pick)+"\n";
                                type += il.getNamedweapon_lite_type(pick)+"\n";
                            } else {
                                name += il.getNamedweapon_lite(pick);
                                type += il.getNamedweapon_lite_type(pick);
                            }
                            //txtName.setText(il.getNamedweapon_lite(pick));
                            //txtType.setText(il.getNamedweapon_lite_type(pick));
                        } else { //sheld
                            pick = percent(0, il.getNamedsheld_lite_Length());
                            if (i != 4) {
                                name += il.getNamedsheld_lite(pick)+"\n";
                                type += il.getNamedsheld_lite_type(pick)+"\n";
                            } else {
                                name += il.getNamedsheld_lite(pick);
                                type += il.getNamedsheld_lite_type(pick);
                            }
                            //txtName.setText(il.getNamedsheld_lite(pick));
                            //txtType.setText(il.getNamedsheld_lite_type(pick));
                        }
                    } else { //기타 장비
                        if (percent(1,2) == 1) { //weapon
                            brand++;
                            txtBrand.setText(Integer.toString(brand));
                            pick = percent(0, il.getWeapontype_Length());
                            int temp;
                            switch (pick) {
                                case 0: //돌격소총
                                    temp = percent(0, il.getWeaponlist1_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist1(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist1(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist1(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 1: //소총
                                    temp = percent(0, il.getWeaponlist2_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist2(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist2(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist2(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 2: //지정사수소총
                                    temp = percent(0, il.getWeaponlist3_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist3(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist3(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist3(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 3: //기관단총
                                    temp = percent(0, il.getWeaponlist4_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist4(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist4(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist4(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 4: //경기관총
                                    temp = percent(0, il.getWeaponlist5_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist5(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist5(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist5(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 5: //산탄총
                                    temp = percent(0, il.getWeaponlist6_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist6(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist6(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist6(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                case 6: //권총
                                    temp = percent(0, il.getWeaponlist7_Length());
                                    if (i != 4) {
                                        name += il.getWeaponlist7(temp)+"\n";
                                        type += il.getWeapontype(pick)+"\n";
                                    } else {
                                        name += il.getWeaponlist7(temp);
                                        type += il.getWeapontype(pick);
                                    }
                                    //txtName.setText(il.getWeaponlist7(temp));
                                    //txtType.setText(il.getWeapontype(pick));
                                    break;
                                default:
                                    txtName.setText("Error");
                                    txtType.setText("Error");
                            }

                        } else { //sheld
                            pick = percent(0, il.getSheldtype_Length());
                            if (i != 4) type += il.getSheldtype(pick)+"\n";
                            else type += il.getSheldtype(pick);
                            //txtType.setText(il.getSheldtype(pick));
                            pick = percent(1, 100);
                            if (pick <= 10) { //gear
                                gear++;
                                txtGear.setText(Integer.toString(gear));
                                pick = percent(0, il.getSheldgear_Length());
                                if (i != 4) name += il.getSheldgear(pick)+"\n";
                                else name += il.getSheldgear(pick);
                                //txtName.setText(il.getSheldgear(pick));
                            } else { //brand
                                brand++;
                                txtBrand.setText(Integer.toString(brand));
                                pick = percent(0, il.getSheldbrand_Length());
                                if (i != 4) name += il.getSheldbrand(pick)+"\n";
                                else name += il.getSheldbrand(pick);
                                txtName.setText(il.getSheldbrand(pick));
                            }
                        }
                    }
                }

                txtName.setText(name);
                txtType.setText(type);

                if (dialogView.getParent() != null)
                    ((ViewGroup) dialogView.getParent()).removeView(dialogView);
                builder.setView(dialogView);

                builder.setPositiveButton("확인", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return root;
    }

    public int percent(int min, int length) {
        return (int)(Math.random()*BIG)%length + min;
    }
}