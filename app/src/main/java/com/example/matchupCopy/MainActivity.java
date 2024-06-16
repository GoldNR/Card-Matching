package com.example.matchupCopy;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private ImageButton[] cards;
    private int[] imgs, facingBack = new int[12]; // 0 value means facing back, otherwise facing front.
    private int back;
    private final int[] click = {0},
                        lastClick = {-1},
                        mp = {0};
    private final boolean[] turn = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cards = new ImageButton[]{
                findViewById(R.id.imageButton1), findViewById(R.id.imageButton2),
                findViewById(R.id.imageButton3), findViewById(R.id.imageButton4),
                findViewById(R.id.imageButton5), findViewById(R.id.imageButton6),
                findViewById(R.id.imageButton7), findViewById(R.id.imageButton8),
                findViewById(R.id.imageButton9), findViewById(R.id.imageButton10),
                findViewById(R.id.imageButton11), findViewById(R.id.imageButton12)
        };

        imgs = new int[]{
                R.drawable.confident,   R.drawable.cute,        R.drawable.excuse,
                R.drawable.laugh,       R.drawable.messed,      R.drawable.scare,
                R.drawable.confident,   R.drawable.cute,        R.drawable.excuse,
                R.drawable.laugh,       R.drawable.messed,      R.drawable.scare
        };

        List<Integer> imgList = new ArrayList<>();
        for (int img : imgs) imgList.add(img);
        Collections.shuffle(imgList);

        back = R.drawable.bg;

        for (int i = 0; i < facingBack.length; i++) {
            facingBack[i] = 0;
        }

        for (int i = 0; i < cards.length; i++) {
            cards[i].setBackgroundResource(back);
            final int index = i;

            cards[i].setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (facingBack[index] == 0) {
                            flipCard(cards[index], imgList.get(index), cards);
                            facingBack[index] = imgList.get(index);
                            if (click[0] == 0) lastClick[0] = index;
                            else turn[0] = true;
                            click[0]++;
                        }

                        else if (facingBack[index] != 0) {
                            flipCard(cards[index], back, cards);
                            facingBack[index] = 0;
                            click[0]--;
                        }

                        if (click[0] == 2) { // if player has chosen 2 cards
                            for (int j = 0; j < cards.length; j++) {
                                cards[j].setEnabled(false);
                            }

                            if (facingBack[index] == facingBack[lastClick[0]]) { // if the 2 cards match
                                cards[index].setClickable(false);
                                cards[lastClick[0]].setClickable(false);
                                turn[0] = false;
                                click[0] = 0;
                                mp[0]++;

                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                enableCards(cards);
                                            }
                                        }, 400
                                );

                                if (mp[0] == imgs.length / 2) {
                                    Toast.makeText(MainActivity.this, "You win!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            else { // if the 2 cards doesn't match
                                final int tempIndex = index;
                                final int tempLastClick = lastClick[0];
                                click[0] = 0;
                                turn[0] = false;
                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                flipCard(cards[tempIndex], back, cards);
                                                facingBack[tempIndex] = 0;
                                                flipCard(cards[tempLastClick], back, cards);
                                                facingBack[tempLastClick] = 0;
                                                enableCards(cards);
                                            }
                                        }, 800
                                );
                            }
                        }
                    }
                }
            );
        }
    }

    private void flipCard(final ImageButton card, final int newBackgroundResource, ImageButton[] cards) {
        card.setEnabled(false);
        ObjectAnimator flip1 = ObjectAnimator.ofFloat(card, "rotationY", 0f, 90f);
        flip1.setDuration(200);
        flip1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                card.setBackgroundResource(newBackgroundResource);
            }
        });

        ObjectAnimator flip2 = ObjectAnimator.ofFloat(card, "rotationY", -90f, 0f);
        flip2.setDuration(200);

        AnimatorSet flipAnimation = new AnimatorSet();
        flipAnimation.playSequentially(flip1, flip2);
        flipAnimation.start();
    }

    private void enableCards(ImageButton[] cards) {
        for (int i = 0; i < cards.length; i++) {
            cards[i].setEnabled(true);
        }
    }
}