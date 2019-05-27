package com.example.surfaceviewgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ScoresScreen extends BaseScreen {
    private BtnRect backBtn = new BtnRect(R.drawable.check_button_image);
    private BtnRect reloadBtn = new BtnRect(R.drawable.reload_button_image);

    private BtnRect upBtn = new BtnRect(R.drawable.up_button_image);
    private Bitmap upInactiveImage;
    private Rect upInactiveImageRect;
    private BtnRect downBtn = new BtnRect(R.drawable.down_button_image);
    private Bitmap downInactiveImage;
    private Rect downInactiveImageRect;

    private Bitmap scoresTopImage;
    private Rect scoresTopImageRect;

    private final int SHOWN_ENTRIES = 5;
    private DatabaseEntry[] entries = new DatabaseEntry[SHOWN_ENTRIES];
    private boolean[] readyForDrawing = new boolean[SHOWN_ENTRIES];
    private int[] place = new int[SHOWN_ENTRIES];
    private int entryRangeBegin = 0;
    private int entryRangeEnd = SHOWN_ENTRIES - 1;
    private int curEntryCount;

    public ScoresScreen(ScreenManager sm) {
        super(sm);

        int w = sm.getWidth();
        int h = sm.getHeight();
        int offset = (int)(0.05f * h);
        float buttonSide = 0.1f * w;

        backBtn.lft = (int)(w - buttonSide - offset);
        backBtn.rgt = (int)(w - offset);
        backBtn.top = (int)(h - buttonSide - offset);
        backBtn.btm = (int)(h - offset);

        reloadBtn.rgt = (int)(backBtn.lft - offset);
        reloadBtn.lft = (int)(reloadBtn.rgt - buttonSide);
        reloadBtn.top = backBtn.top;
        reloadBtn.btm = backBtn.btm;

        float buttonAspectRatio = ((float)upBtn.bmp.getHeight()) / upBtn.bmp.getWidth();
        float buttonHorzOffset = (reloadBtn.lft - 2f * 0.2f * w) / 3f;
        upBtn.lft = (int)buttonHorzOffset;
        upBtn.rgt = (int)(upBtn.lft + 0.2f * w);
        float buttonHeight = buttonAspectRatio * (upBtn.rgt - upBtn.lft);
        upBtn.top = (int)((backBtn.top + backBtn.btm - buttonHeight) / 2f);
        upBtn.btm = (int)(upBtn.top + buttonHeight);

        downBtn.lft = (int)(upBtn.rgt + buttonHorzOffset);
        downBtn.rgt = (int)(downBtn.lft + 0.2f * w);
        downBtn.top = upBtn.top;
        downBtn.btm = upBtn.btm;

        upInactiveImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.up_button_inactive_image);
        upInactiveImageRect = new Rect(upBtn.lft, upBtn.top, upBtn.rgt, upBtn.btm);
        downInactiveImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.down_button_inactive_image);
        downInactiveImageRect = new Rect(downBtn.lft, downBtn.top, downBtn.rgt, downBtn.btm);

        scoresTopImage = BitmapFactory.decodeResource(sm.ma.getResources(), R.drawable.scores_top_image);
        float scoresTopAspectRatio = ((float)scoresTopImage.getHeight()) / scoresTopImage.getWidth();
        scoresTopImageRect = new Rect(0, 0, w, (int)(w * scoresTopAspectRatio));

        for(int i = 0; i < SHOWN_ENTRIES; i++) {
            entries[i] = new DatabaseEntry();
            readyForDrawing[i] = false;
            place[i] = i + 1;
        }

        getEntryCountAndLoad();
    }

    @Override
    public void drawScreen(Canvas canvas) {
        Paint backPaint = new Paint();
        backPaint.setColor(Color.rgb(0, 0, 80));
        canvas.drawRect(0, 0, sm.getWidth(), sm.getHeight(), backPaint);

        canvas.drawBitmap(scoresTopImage, null, scoresTopImageRect, null);

        float entriesTop = scoresTopImageRect.bottom;
        float entriesBtm = backBtn.top;
        for(int i = 0; i < SHOWN_ENTRIES; i++)
            drawDatabaseEntry(canvas, entries[i], readyForDrawing[i], place[i], (int)(entriesTop + i * (entriesBtm - entriesTop) / SHOWN_ENTRIES + 0.075f * sm.getHeight()));

        backBtn.draw(canvas);
        reloadBtn.draw(canvas);

        if(entryRangeBegin > 0)
            upBtn.draw(canvas);
        else
            canvas.drawBitmap(upInactiveImage, null, upInactiveImageRect, null);
        if(curEntryCount > entryRangeEnd + 1)
            downBtn.draw(canvas);
        else
            canvas.drawBitmap(downInactiveImage, null, downInactiveImageRect, null);
    }

    @Override
    public void updateScreen() {
        if(sm.viewMenu) {
            sm.ma.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sm.ma.switchScreen();
                }
            });
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int)event.getX();
            int y = (int)event.getY();

            if(backBtn.pointInside(x, y))
                sm.viewMenu = true;
            else if(reloadBtn.pointInside(x, y))
                getEntryCountAndLoad();
            else if(entryRangeBegin > 0 && upBtn.pointInside(x, y)) {
                entryRangeBegin -= SHOWN_ENTRIES;
                entryRangeEnd -= SHOWN_ENTRIES;
                loadEntryRange();
            }
            else if(curEntryCount > entryRangeEnd + 1 && downBtn.pointInside(x, y)) {
                entryRangeBegin += SHOWN_ENTRIES;
                entryRangeEnd += SHOWN_ENTRIES;
                loadEntryRange();
            }
        }

        return true;
    }

    private void getEntryCountAndLoad() {
        sm.ma.rootRef.child("scores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                curEntryCount = (int)dataSnapshot.getChildrenCount();
                loadEntryRange();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void loadEntryRange() {
        int last = entryRangeEnd + 1;
        int first = SHOWN_ENTRIES;
        if(curEntryCount < last) {
            first = curEntryCount - last + SHOWN_ENTRIES;
            last = curEntryCount;
        }

        if(last == 0 || first == 0)
            return;

        final int lastFinal = last;
        final int firstFinal = first;

        // Firebase doesn't allow for sorting in descending order,
        // so to get highest scores we query for entries at the end and reverse them
        // it's also not possible to limit to last and first at the same time, so you need to skip the unnecessary elements
        Query query = sm.ma.rootRef.child("scores").orderByChild("score").limitToLast(lastFinal);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i = 0; i < SHOWN_ENTRIES; i++)
                    readyForDrawing[i] = false;

                int i = firstFinal - 1;
                int j = 0;
                for(DataSnapshot singleScore : dataSnapshot.getChildren()) {
                    if(j == firstFinal)
                        break;

                    DatabaseEntry queryEntry = singleScore.getValue(DatabaseEntry.class);

                    entries[i].setUuid(queryEntry.getUuid());
                    entries[i].setTag(queryEntry.getTag());
                    entries[i].setScore(queryEntry.getScore());
                    entries[i].setDate(queryEntry.getDate());
                    entries[i].setMinis(queryEntry.getMinis());
                    entries[i].setTotal(queryEntry.getTotal());

                    place[i] = entryRangeBegin + 1 + i;
                    readyForDrawing[i] = true;

                    i--;
                    j++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void drawDatabaseEntry(Canvas canvas, DatabaseEntry entry, boolean readyForDrawing, int place, int y) {
        if(!readyForDrawing)
            return;

        Paint textPaint = new Paint();
        textPaint.setColor(Color.rgb(145, 145, 145));
        textPaint.setTextSize(sm.getHeight() * 0.05f);
        textPaint.setTextAlign(Paint.Align.RIGHT);

        // uuid toString, rozdzielic na trzy czesci dostanego stringa, w kazdej pododawac znaki modulo 256
        // dostajemy rgb, rysujemy nim tlo taga, jesli uuid == sm.ma.playerUUID to najpierw rysujemy tlo czarne,
        // a na tym kolor troche mniejszy i dopiero tag, zeby byla obwodka czarna

        // zeby nie bylo problemow z nieczytelnym tagiem to po obliczeniu koloru tla znalezc negatyw i nim pisac taga
        int[] rgb = crunchDownUUID(entry.getUuid());

        Paint uuidPaint = new Paint();
        uuidPaint.setColor(Color.rgb(rgb[0], rgb[1], rgb[2]));

        Paint negPaint = new Paint();
        negPaint.setColor(Color.rgb(255 - rgb[0], 255 - rgb[1], 255 - rgb[2]));
        negPaint.setTextSize(sm.getHeight() * 0.05f);
        negPaint.setTextAlign(Paint.Align.LEFT);

        Rect tagBounds = new Rect();
        negPaint.getTextBounds(entry.getTag(), 0, 3, tagBounds);

        float w = sm.getWidth();
        float h = sm.getHeight();
        float tagX = 0.223f * w;
        float tagWHalf = tagBounds.width() / 2f;
        float tagH = tagBounds.height();
        float uuidEdge = 0.01f * h;
        float playerEdge = 0.005f * h;
        float uuidLeft = tagX - tagWHalf - uuidEdge;
        float uuidTop = y - tagH - uuidEdge;
        float uuidRight = tagX + tagWHalf + uuidEdge;
        float uuidBottom = y + uuidEdge;

        String entryUuid = entry.getUuid();
        String playerUuid = sm.ma.playerUUID.toString();

        if(entryUuid.equals(playerUuid)) {
            canvas.drawRect(uuidLeft - playerEdge, uuidTop - playerEdge, uuidRight + playerEdge, uuidBottom + playerEdge, textPaint);
            canvas.drawRect(uuidLeft, uuidTop, uuidRight, uuidBottom, uuidPaint);
        }
        else
            canvas.drawRect(uuidLeft, uuidTop, uuidRight, uuidBottom, uuidPaint);

        canvas.drawText(Integer.toString(place), 0.13f * w, y, textPaint);
        canvas.drawText(entry.getTag(), tagX - tagBounds.exactCenterX(), y, negPaint);
        canvas.drawText(Long.toString(entry.getScore()), 0.475f * w, y, textPaint);
        canvas.drawText(entry.getDate(), 0.648f * w, y, textPaint);
        canvas.drawText(Long.toString(entry.getMinis()), 0.81f * w, y, textPaint);
        canvas.drawText(Long.toString(entry.getTotal()), 0.97f * w, y, textPaint);
    }

    private int[] crunchDownUUID(String uuid) {
        int[] rgb = new int[3];

        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < uuid.length(); i++) {
            if(uuid.charAt(i) != '-')
                sb.append(uuid.charAt(i));
        }

        int third = sb.length() / 3;
        String redString = sb.substring(0, third);
        String grnString = sb.substring(third, 2 * third);
        String bluString = sb.substring(2 * third);

        rgb[0] = stringToColor(redString);
        rgb[1] = stringToColor(grnString);
        rgb[2] = stringToColor(bluString);

        return rgb;
    }

    private int stringToColor(String s) {
        int c = 0;

        for(int i = 0; i < s.length(); i++) {
            c += s.charAt(i);
            c %= 256;
        }

        return c;
    }

    private class BtnRect {
        public int lft, rgt, top, btm;

        public Bitmap bmp;

        public BtnRect(int bmpCode) {
            bmp = BitmapFactory.decodeResource(sm.ma.getResources(), bmpCode);
        }

        public boolean pointInside(int x, int y) {
            if(x >= lft && x <= rgt && y >= top && y <= btm)
                return true;
            return false;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(bmp, null, new Rect(lft, top, rgt, btm), null);
        }
    }
}
