package com.example.divisionsimulation.ui.send;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.divisionsimulation.R;

public class KeenerActivity extends AppCompatActivity {

    private ImageView[] btnKeener = new ImageView[8];
    private MediaPlayer mp = null;

    private AlertDialog.Builder builder = null;
    private AlertDialog alertDialog = null;
    private View dialogView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keenerlayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바에 뒤로가기 버튼 추가
        setTitle("아론 키너 음성 기록"); //타이틀 설정

        Intent intent = getIntent(); //인텐트로 전 액티비티에서 가져온 데이터들도 들어있다.

        int temp;
        for (int i = 0; i < btnKeener.length; i++) {
            temp = getResources().getIdentifier("btnKeener"+(i+1), "id", getPackageName());
            btnKeener[i] = findViewById(temp);
            btnKeener[i].setAdjustViewBounds(true);
        }
        /*
        아론 키너의 음성 기록 버튼이 배열로 설정한다.
         */

        btnKeener[0].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //버튼을 한번 눌렀을 경우 작동
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound1); //버튼에 해당하는 음성기록가 담긴 미디어 플레이어를 생성한다.
                mp.start(); //미디어 플레이어를 재생함으로서 미디어 소리로 음성기록이 재생된다.
                builder = new AlertDialog.Builder(KeenerActivity.this); //한글 자막이 보이도록 다이얼로그를 띄운다.
                builder.setTitle("'간단한 계산' 음성 기록 재생 중...").setMessage("죽이는 건 쉬운 일이지.\n난 분명 그 대상이 사람이라는 말은 안했다고.\n상대가 사람이라고 생각하기 시작하면 갑자기 죽이기가 훨씬 더 어려워 지거든.\n하지만 그럴 땐 대차대조표를 떠올리면 편해지지.\n이 사람이 얼마나 방해가 되는지, 잠시 더 살려둬서 얻을 수 있는 이점은 무엇인지 생각하다 보면 간단한 수학 문제처럼 금세 답이 보일거야.\n간단한 수학 문제는 풀이도 간단한 법이지.\n정중앙에 두 개의 총알을 쏘고 세번째는 머리에 쏘는 것처럼 쉽다고.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { //종료를 누르게 되면 작동한다.
                        mp.stop(); //미디어 플레이어를 중지한다.
                        mp.reset(); //미디어 플레이어를 초기화한다.
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) { //종료버튼을 제외한 다른 방법으로 종료하게 되었을 경우 작동한다.
                        mp.stop(); //위와 동일한 방식
                        mp.reset(); //위와 동일한 방식
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) { //미디어 플레이어 음성 기록이 모두 재생되고 중지되었을 경우 작동한다.
                        alertDialog.setTitle("'간단한 계산' 음성 기록 재생 완료"); //제목을 재생 완료로 변경한다. 변경함으로서 음성 기록이 재생이 끝났다는 것을 알려준다.
                    }
                });
                alertDialog = builder.create(); //다이얼로그 설정
                alertDialog.setCancelable(false); //다이얼로그 바깥영역 또는 뒤로가기버튼을 눌렀을 경우에 꺼지는 것을 방지한다. 뒤로가기, 바깥영역을 눌러도 꺼지지 않는다. true면 꺼진다.
                alertDialog.show(); //액티비티에 다이얼로그를 띄운다.
            }
        });
        btnKeener[1].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound2);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'경고' 음성 기록 재생 중...").setMessage("안녕하신가, 아론 키너다.\n날 기억할 수도 기억 못할 수도 있겠지.\n어느 쪽이든 상관없다.\n너희들보다 지금 상황이 더 중요하니까.\n내게는 예전에 애머스트가 그린 플루 제작에 사용했던 바이러스 프린터와 우연히도 그걸 사용할 줄 아는 '비탈리 체르넨코'가 있다.\n그러니 혹시라도 날 쫓겠다면 마음대로 해도 좋지만 내가 무슨 짓을 할지 생각해봐라.\n나도 장담할 수 없지만, 적어도 난 너희 목숨보다 내 자유를 더 소중하게 생각하거든.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'경고' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnKeener[2].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound3);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'고향' 음성 기록 재생 중...").setMessage("집은 사랑하는 사람들이 있는 곳이라는 말이 있다.\n사람들은 내가 냉혹하다고 많이들 비난하던데.\n내가 집도 철도 없는 인간이라는 뜻이겠지.\n어느 쪽이든 그런 말 하는 인간들은 쥐뿔 아는 것도 없으면서 지껄이는 거라고.\n하지만 뉴욕. 왜 돌아 왔을까?\n솔직히 나도 모르겠군. 우리가 사는 이 화려한 신세계가 시작된 곳.\n모든 것의 중심이니 그 중심에 가까이 있고 싶었는지도 모르지.\n아니면 내가 지쳐 온 다른 도시에서 본 쓰레기 같은 모습들에 너무 질려서 그런지도\n뉴욕에는 뉴저지 교외 주택가에서 찾아볼 수 없는 무언가가 있다.\n이 난리를 겪고 있는 와중에도 설명할 수 없는 마법 같은게 존재한다.\n그것 하나만으로도 계속해서 돌아오게 되는 이유가 충분하지.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'고향' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnKeener[3].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound4);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'그린 플루' 음성 기록 재생 중...").setMessage("사람들은 내가 우리 친구 비탈리의 도움을 받아 또 다른 바이러스를 제조해서 방출할까봐 두려워하고 있겠지.\n이해해. 이번엔 뭐라고 부를까? 최악성 그린 플루?\n이름을 상상해 보는 것만으로도 참 즐겁군.\n한번 스스로에게 물어봐.\n바이러스를 퍼트려서 내가 얻을 수 있는 게 뭔지, 가만히 있어도 이미 무너져 가고 있는 세상인데 내가 굳이 힘을 보탤 필요가 있을까?\n오히려 시스템이 알아서 처음 상태로 되돌아가도록 놔두는게 내게 더 이익이지 않을까?\n아니면 불난 집에 부채질해 혼란을 가중할까?\n내가 무엇을 할지 예측할 수 있다고 생각하나?\n그래, 마음껏 예측해라. 왜냐면 아직 나의 다음 행동을 나조차 결정하지 못했으니까.\n아니, 이미 결정했는지도 모르지.\n말하지면 내가 친 연막에서 너희들은 그냥 쳇바퀴 돌듯이 계속 똑같은 멍청한 짓을 반복할 뿐이야.\n언젠간 나에게 직접 물을 날이 오겠지.\n내가 너희들 먼저 죽이지 않는다면 말이야.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'그린 플루' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnKeener[4].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound5);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'기록' 음성 기록 재생 중...").setMessage("내가 왜 이런 메세지를 남기는지 궁금하겠지.\n마음 깊은 곳에서 어쩌면 고해를 하고 싶었는지도. 모르겠군.\n이번에도 아닐 수도 있지만.\n난 지금까지 내가 한 일 중에 단 하나도 자랑스럽게 여기지 않는 게 없다.\n그리고 너희들, 아니 그 누구의 용서를 구할 일도 없어.\n어쩌면 내가 지쳤는지도 모르지.\n남들보다 앞서간다는건 피곤한 일이니까.\n그런 내가 너희들에게 좀 더 난해하고 의미 있는 도전을 해보라고 도움의 손길을 내민 거야.\n물론 그런 짓은 영화 속의 악당이나 하는 짓이지.\n난 그저 내가 가진 강점이 극대화되는 것만으로도 만족해\n아니면 너희들이 그냥 계속 쳇바퀴를 돌기를 바라는 건지도 모르겠군.\n내가 지금까지 한 말에 미묘한 어페가 있었나?\n내가 하려는 말을 알아듣기 어려운가?\n그게 내 의도였는지도 모르지.\n언제나 그렇듯 아닐 수도 있고.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'기록' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnKeener[5].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound6);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'비탈리' 음성 기록 재생 중...").setMessage("비탈리라는 친구를 어떻게 할지 고민 중이야.\n내가 가는 곳마다 끌고 다닐 수는 없는 노릇이니.\n하지만 바이러스 프린터를 제대로 사용하려면 그의 전문 기술이 필요하지.\n물론 이걸 사용할 거라는 가정하에 성립되는 얘기지만, 사용할지 말지는 아무도 모르지.\n나의 다음 행동을 예측하지 말라고 하지 않았나?\n함부로 넘겨 짚었다간 문제만 더 꼬일거야.\n무슨 얘기를 하다 말았더라?\n그래. 비달리, 그자는 말을 잘 듣는 편이야.\n한번인가 도망치려 했지만 그러면 왜 안되는지 내가 알아듣게 잘 타일렀거든. 이제는 내가 시키는 대로 잘해서 모두가 안전해졌지.\n'안전'의 정의는 각자 다르겠지만.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'비탈리' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnKeener[6].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound7);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'인내' 음성 기록 재생 중...").setMessage("지금 쯤 왜 내가 너희들을 찾아가지 않았는지 그 이유가 궁금해 미칠거야.\n쯧, 그 정도 인내심은 있을 줄 알았는데.\n정말 그 정도로 나와 만나고 싶나?\n아니면 내가 다음에 어떤 행동을 할지 알고 싶은건가?\n대응을 할 수 있을 거라는 희망으로 말이지.\n힌트를 하나를 주지\n감히 날 앞서가려고 애쓰지 마\n오래 살고 싶다면 말이다.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        alertDialog.setTitle("'인내' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });
        btnKeener[7].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) { //위와 동일한 방식
                mp = MediaPlayer.create(KeenerActivity.this, R.raw.keenersound8);
                mp.start();
                builder = new AlertDialog.Builder(KeenerActivity.this);
                builder.setTitle("'출입구 열기' 음성 기록 재생 중...").setMessage("어찌 보면 맨해튼에 있던 격리 구역이 사라져서 아쉽기도 해.\n한 1~2분 동안 다시 잠입하는 과정이 꽤 흥미진진한 도전이었을텐데,\n하지만 이제 다 사라졌지.\n배, 바리게이트, 전부다.\n애초에 신뢰하지도 않아 놓고 이제 와서 그들을 탓할 수는 없겠지.");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.stop();
                        mp.reset();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mp.stop();
                        mp.reset();
                    }
                });
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) { //위와 동일한 방식
                        alertDialog.setTitle("'출입구 열기' 음성 기록 재생 완료");
                    }
                });
                alertDialog = builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
