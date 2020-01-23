package com.example.divisionsimulation.ui.share;

class Itemlist {
    private String[] weapontype = {"돌격 소총", "소총", "지정사수소총", "기관단총", "경기관총", "산탄총", "권총"};
    private String[] weaponlist1 = {"경찰용 M4", "AK-M", "군용 AK-M", "블랙마켓 AK-M", "ACR", "ACR-E", "FAL", "FAL SA-58", "FAL SA-58 Para", "CTAR-21", "F2000", "AUG A3-CQC", "P416", "커스텀 P416 G3",
            "G36", "군용 G36", "강화형 G36", "FAMAS 2010", "Mk.16", "전술용 Mk.16", "SOCOM Mk.16", "Carbine 7"}; //돌격 소총
    private String[] weaponlist2 = {"경량형 M4", "LVOA-C", "도심 MDR", "ACR SS", "클래식 M1A", "SOCOM M1A", "M1A CQB", "경찰용 Mk.17", "군용 Mk.17", "USC .45 ACP", "M16A2", "1886", "SIG 716", "SIG 716 CQB"}; //소총
    private String[] weaponlist3 = {"M700", "M700 Carbon", "M700 Tactical", "Covert SRS", "SRS A1", "클래식 M44", "사냥용 M44", "커스텀 M44", "SR-1", "Surplus SVD", "공수부대용 SVD", "SOCOM Mk20 SSR"}; //지정사수소총
    private String[] weaponlist4 = {"전술용 AUG A3P", "강화형 AUG A3P", "AUG A3 PARA XS", "경찰용 T821", "블랙마켓 T821", "경찰용 UMP45", "전술용 UMP-45",
            "MP5A2", "MP5-N", "MP5 ST", "MP7", "MPX", "P90", "개조형 SMG-9", "개조형 SMG-9 A2", "PP-19", "강화형 PP-19", "전술용 Vector SBR 9mm", "Vector SBR 9mm", "Vector SBR .45 ACP", "M1928", "Tommy Gun"}; //기관단총
    private String[] weaponlist5 = {"클래식 M60", "군용 M60 E4", "블랙마켓 M60 E6", "M249 B", "전술용 M249 Para", "군용 Mk46", "군용 L86 LSW", "커스텀 L86 A2", "클래식 RPK-74", "군용 RPK-74", "블랙마켓 RPK-74",
            "MG5", "보병용 MG5", "Stoner LAMG"}; //경기관총
    private String[] weaponlist6 = {"Double Barrel Shotgun", "보급형 M870", "군용 M870", "커스텀 M870 MCS", "Super 90", "해병대용 Super 90", "전술용 Super 90 SBS", "SASG-12", "전술용 SASG-12K", "블랙마켓 SASG-12 S",
            "ACS-12", "SPAS-12", "KSG"}; //산탄총
    private String[] weaponlist7 = {"Snubnosed Diceros", "Diceros", "Diceros Special", "586 매그넘", "경찰용 686 Magnum", "M1911", "전술형 M1911", "M45A1", "1차 투입 요원용 PF45", "커스텀 PF45", "군용 M9", "장교용 M9 A1",
            "Px4 Storm Type F", "Px4 Storm Type T", "X-45", "X-45 택티컬", "93R", "D50", "P320 XCompact", "Double Barrel Sawed Off"};

    private String[] namedweapon_lite = {"예술가의 도구", "코만도", "쿠엘레브레", "지정 타자", "에킴의 장대", "에멜린의 수호자", "보이지 않는 손", "피뢰침", "방화광", "안전거리", "방패 분쇄기", "스왑 체인", "타뷸라 라사",
            "쓰나미"}; //네임드 무기 (라이트존 전용)
    private String[] namedweapon_lite_type = {"소총", "지정사수소총", "산탄총", "지정사수소총", "지정사수소총", "기관단총", "돌격소총", "권총", "돌격소총", "기관단총", "돌격소총", "기관단총", "경기관총", "산탄총"};
    private String[] namedweapon_dark = {"블랙 프라이데이", "코만도", "쿠엘레브레", "에킴의 장대", "보이지 않는 손", "궤도", "미세침", "로큰롤", "안전거리", "스왑 체인", "아파트", "나무꾼", "버지니아인"}; //네임드 무기 (다크존 전용)
    private String[] namedweapon_dark_type = {"경기관총", "지정사수소총", "산탄총", "지정사수소총", "돌격소총", "권총", "권총", "산탄총", "기관단총", "기관단총", "기관단총", "돌격소총", "소총"};

    private String[] specialweapon = {"무자비", "달콤한 꿈"};
    private String[] specialweapon_type = {"소총", "산탄총"};

    private String[] specialweapon_raid = {"무자비", "달콤한 꿈", "독수리를 거느린 자"};
    private String[] specialweapon_raid_type = {"소총", "산탄총", "돌격소총"};

    private String[] sheldtype = {"마스크", "백팩", "조끼", "장갑", "권총집", "무릎 보호대"};
    private String[] sheld_brand = {"5.11 Tactical", "길라 가드", "더글라스 & 하딩", "리히터 & 카이저 유한회사", "무라카미 산업", "배저 터프", "소콜로프 상사", "아이랄디 홀딩", "알프스 정상 군수산업", "얄 기어",
            "오버로드 군수산업", "와이번 웨어", "페트로프 방위 그룹", "펜리르 그룹 AB", "프로비던스 방위산업", "차이나 경공업 기업", "체스카 비로바 SRO"}; //브랜드 장비
    private String[] sheld_gear = {"긴급 지휘명령", "진정한 애국자", "하드 와이어드 강화장치", "에이스 & 에이트", "창끝", "협상가의 딜레마"}; //기어 장비
    private String[] sheld_special = {"BTSU 데이터 장갑", "소여의 무릎 보호대"}; //특급 장비
    private String[] sheld_special_type = {"장갑", "무릎 보호대"};

    private String[] namedsheld_lite = {"청부업자의 장갑", "굳은 악수", "용광로", "여우의 기도", "밤의 감시자", "물리식 기계 수리기", "훌륭한 귀감", "펀치드렁크", "전략적 조정", "무관심의 극치"};
    private String[] namedsheld_lite_type = {"장갑", "장갑", "권총집", "무릎 보호대", "마스크", "백팩", "조끼", "마스크", "백팩", "조끼"};
    private String[] namedsheld_dark = {"날카로운 발톱", "Deathgrips", "황제의 근위병", "흉포한 평정심", "굳은 악수", "밤의 감시자", "전략적 조정", "희생양", "공허한 사내", "무관심의 극치"};
    private String[] namedsheld_dark_type = {"권총집", "장갑", "무릎 보호대", "조끼", "장갑", "마스크", "백팩", "백팩", "마스크", "조끼"};

    public String getSpecialweapon_type(int index) { return specialweapon_type[index]; }
    public int getSpecialweapon_type_Length() { return specialweapon_type.length; }
    public String getSheldspecial_type(int index) { return sheld_special_type[index]; }
    public int getSheldspecial_type_Length() { return sheld_special_type.length; }

    public String getSpecialweapon_raid(int index) { return specialweapon_raid[index]; }
    public int getSpecialweapon_raid_Length() { return specialweapon_raid.length; }
    public String getSpecialweapon_raid_type(int index) { return specialweapon_raid_type[index]; }

    public String getWeapontype(int index) { return weapontype[index]; }
    public int getWeapontype_Length() { return weapontype.length; }
    public String getWeaponlist1(int index) { return weaponlist1[index]; }
    public int getWeaponlist1_Length() { return weaponlist1.length; }
    public String getWeaponlist2(int index) { return weaponlist2[index]; }
    public int getWeaponlist2_Length() { return weaponlist2.length; }
    public String getWeaponlist3(int index) { return weaponlist3[index]; }
    public int getWeaponlist3_Length() { return weaponlist3.length; }
    public String getWeaponlist4(int index) { return weaponlist4[index]; }
    public int getWeaponlist4_Length() { return weaponlist4.length; }
    public String getWeaponlist5(int index) { return weaponlist5[index]; }
    public int getWeaponlist5_Length() { return weaponlist5.length; }
    public String getWeaponlist6(int index) { return weaponlist6[index]; }
    public int getWeaponlist6_Length() { return weaponlist6.length; }
    public String getWeaponlist7(int index) { return weaponlist7[index]; }
    public int getWeaponlist7_Length() { return weaponlist7.length; }

    public String getNamedweapon_lite(int index) { return namedweapon_lite[index]; }
    public int getNamedweapon_lite_Length() { return namedweapon_lite.length; }
    public String getNamedweapon_dark(int index) { return namedweapon_dark[index]; }
    public int getNamedweapon_dark_Length() { return namedweapon_dark.length; }
    public String getNamedweapon_lite_type(int index) { return namedweapon_lite_type[index]; }
    public int getNamedweapon_lite_type_Length() { return namedweapon_lite_type.length; }
    public String getNamedweapon_dark_type(int index) { return namedweapon_dark_type[index]; }
    public int getNamedweapon_dark_type_Length() { return namedweapon_dark_type.length; }

    public String getNamedsheld_lite(int index) { return namedsheld_lite[index]; }
    public int getNamedsheld_lite_Length() { return namedsheld_lite.length; }
    public String getNamedsheld_lite_type(int index) { return namedsheld_lite_type[index]; }
    public int getNamedsheld_lite_type_Length() { return namedsheld_lite_type.length; }
    public String getNamedsheld_dark(int index) { return namedsheld_dark[index]; }
    public int getNamedsheld_dark_Length() { return namedsheld_dark.length; }
    public String getNamedsheld_dark_type(int index) { return namedsheld_dark_type[index]; }
    public int getNamedsheld_dark_type_Length() { return namedsheld_dark_type.length; }

    public String getSpecialweapon(int index) { return specialweapon[index]; }
    public int getSpecialweapon_Length() { return specialweapon.length; }

    public String getSheldtype(int index) { return sheldtype[index]; }
    public int getSheldtype_Length() { return sheldtype.length; }
    public String getSheldbrand(int index) { return sheld_brand[index]; }
    public int getSheldbrand_Length() { return sheld_brand.length; }
    public String getSheldgear(int index) { return sheld_gear[index]; }
    public int getSheldgear_Length() { return sheld_gear.length; }
    public String getSheldspecial(int index) { return sheld_special[index]; }
    public int getSheldspecial_Length() { return sheld_special.length; }
}
