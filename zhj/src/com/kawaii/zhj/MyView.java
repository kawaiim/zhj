package com.kawaii.zhj;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	SurfaceHolder surfaceHolder;
	Canvas canvas;
	Boolean repaint = false;
	Boolean start;
	Thread drawThread;
	// ��Ļ��Ⱥ͸߶�
	int screen_height;
	int screen_width;
	// ͼƬ��Դ
	Bitmap cardBitmap[] = new Bitmap[54];
	Bitmap bgBitmap; // ����
	Bitmap cardBgBitmap;// ͼƬ����
	Bitmap dizhuBitmap;// ����ͼ��
	// ��������
	int cardWidth, cardHeight;
	// ����
	Paint paint = null;
	// �ƶ���
	Card card[] = new Card[54];
	// ��ť
	String buttonText[] = new String[1];
	// ��ʾ
	String message[] = new String[3];
	boolean hideButton = true;
	// List
	List<Card> playerCardList[] = new TArrayList[3];

	// ������
	List<Card> dizhuList = new TArrayList();
	// ����
	int turn = -1;
	// �ѳ��Ʊ�
	List<Card> outList[] = new TArrayList[3];

	Handler handler;
	// ��ʼ����
	int iTmp[] = { 0, 13, 1, 14, 2, 15, 3, 16, 4, 17, 5, 18, 6, 19, 7, 20, 12 };// ������
	int iTmp1[] = { 51, 52, 53 };
	int iLmp[] = { 41, 42, 43 };
	int iRmp[] = { 45, 46, 47 };
	int iPlay1[] = { 16, 2, 2 };
	static int iPlayCount = 0;

	// ���캯��
	public MyView(Context context) {
		super(context);
	}

	public MyView(Context context, Handler handler) {
		super(context);
		this.handler = handler;
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("surfaceCreated", "���������");
		start = true;
		screen_height = getHeight();
		screen_width = getWidth();
		// ��ʼ��
		InitBitMap();
		// ��ʼ��Ϸ����
		new Thread(new Runnable() {
			@Override
			public void run() {
				// ��ʼ����
				if (iPlayCount == 3) {
					// start = false;
					iPlayCount = 0;
				}
				handCards();
				Log.i("turn", "" + turn);
				// �ȴ�����ѡ��
				while (start) {
					switch (turn) {
					case 0:
						player0();
						break;
					case 1:
						player1(iPlay1[iPlayCount++]);
						break;
					case 2:
						player2();
						break;
					default:
						break;
					}
					win();

				}
			}
		}).start();
		// ��ʼ��ͼ����
		drawThread = new Thread(this);
		drawThread.start();
	}

	// �����ߵ���
	public void drawOutList() {
		int x = 0, y = 0;
		for (int i = 0, len = outList[1].size(); i < len; i++) {
			x = screen_width / 2 + (i - len / 2) * cardWidth / 3;
			y = screen_height - 5 * cardHeight / 2;
			canvas.drawBitmap(outList[1].get(i).bitmap, x, y, null);
		}
	}

	// player0
	public void player0() {
		message[0] = "";
		setTimer(3, 0);
		message[0] = "��Ҫ";

		update();
		nextTurn();
	}

	// player2
	public void player2() {
		for (int i = 0; i < outList[1].size(); i++) {
			playerCardList[1].remove(outList[1].get(i));
		}
		message[2] = "";
		outList[2].clear();
		setTimer(3, 2);
		message[2] = "��Ҫ";
		update();
		nextTurn();
	}

	// player1
	public void player1(int m) {
		Sleep(1000);
		// ��ʼд���Ƶ���
		hideButton = true;
		outList[1].clear();
		for (int i = 0; i < m; i++) {
			Card card = playerCardList[1].get(i);
			outList[1].add(card);
		}

		nextTurn();
		update();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		start = false;
	}

	// ��Ҫ��ͼ�߳�
	@Override
	public void run() {
		while (start) {
			if (repaint) {
				BeginOnDraw();
				repaint = false;
				Sleep(1000);
			}
		}
	}

	// ��ʼ��ͼƬ,����
	public void InitBitMap() {
		turn = -1;
		// 1. װ��
		int count = 0;
		for (int i = 1; i <= 4; i++) {
			for (int j = 3; j <= 15; j++) {
				// ���������ҳ�ID
				String name = "a" + i + "_" + j;
				ApplicationInfo appInfo = getContext().getApplicationInfo();
				int id = getResources().getIdentifier(name, "drawable",
						appInfo.packageName);
				cardBitmap[count] = BitmapFactory.decodeResource(
						getResources(), id);
				card[count] = new Card(cardBitmap[count].getWidth(),
						cardBitmap[count].getHeight(), cardBitmap[count]);
				// ����Card������
				card[count].setName(name);
				count++;
			}
		}
		// ���С��������
		cardBitmap[52] = BitmapFactory.decodeResource(getResources(),
				R.drawable.a5_16);
		card[52] = new Card(cardBitmap[52].getWidth(),
				cardBitmap[52].getHeight(), cardBitmap[52]);
		card[52].setName("a5_16");
		cardBitmap[53] = BitmapFactory.decodeResource(getResources(),
				R.drawable.a5_17);
		card[53] = new Card(cardBitmap[53].getWidth(),
				cardBitmap[53].getHeight(), cardBitmap[53]);
		card[53].setName("a5_17");
		cardWidth = card[53].width;
		cardHeight = card[53].height;
		// ����ͼ��
		dizhuBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.dizhu);
		// ����
		bgBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);
		cardBgBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.cardbg);
		// ��ť

		buttonText[0] = new String();
		buttonText[0] = "������";
		// ��Ϣ,�ѳ���

		for (int i = 0; i < 3; i++) {
			message[i] = new String("");
			outList[i] = new TArrayList();
		}

		// ����ť
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(cardWidth * 2 / 5);
		paint.setAntiAlias(true);// ��ݺۼ�����ô����
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1.0f);
		paint.setTextAlign(Align.CENTER);

	}

	// ������
	public void drawBackground() {

		Rect src = new Rect(0, 0, bgBitmap.getWidth() * 3 / 4,
				2 * bgBitmap.getHeight() / 3);

		Rect dst = new Rect(0, 0, screen_width, screen_height);
		canvas.drawBitmap(bgBitmap, src, dst, null);
	}

	// ����
	public void handCards() {

		for (int i = 0; i < 3; i++) {
			playerCardList[i] = new TArrayList();
		}
		drawdp();
		drawL();
		drawD();
		drawR();
		hideButton = false;
		update();

		for (int j = 0; j < 3; j++) {
			Log.i("playerCardList", String.valueOf(playerCardList[j].size()));
		}

	}

	public void drawdp() {
		for (int i = 0; i < iTmp1.length; i++) {
			dizhuList.add(card[iTmp1[i]]);
			card[iTmp1[i]].setLocation(screen_width / 2 - (3 * (50 + i) - 155)
					* cardWidth / 2, 0);
		}

	}

	public void drawL() {
		for (int i = 0; i < iLmp.length; i++) {
			playerCardList[0].add(card[iLmp[i]]);
			card[iLmp[i]].setLocation(0, cardHeight / 2 + (i + 1) * cardHeight
					/ 21);
		}

	}

	public void drawR() {
		for (int i = 0; i < iRmp.length; i++) {
			playerCardList[2].add(card[iRmp[i]]);
			card[iRmp[i]].setLocation(screen_width - 3 * cardWidth / 2,
					cardHeight / 2 + (i + 1) * cardHeight / 21);

		}

	}

	public void drawD() {
		for (int i = 0; i < iTmp.length; i++) {

			card[iTmp[i]].setLocation(screen_width / 2 - (9 - i) * cardWidth
					* 2 / 3, screen_height - cardHeight);
			card[iTmp[i]].rear = false;
			playerCardList[1].add(card[iTmp[i]]);
		}
	}

	public void Sleep(long i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			Log.i("myview.java", "����Ե��ڴ�");
			e.printStackTrace();
		}
	}

	// �����
	public void drawPlayer(int player) {
		if (playerCardList[player] != null && playerCardList[player].size() > 0) {
			for (Card card : playerCardList[player])
				drawCard(card);
		}
	}

	// ����
	public void drawCard(Card card) {
		Bitmap tempbitBitmap;
		if (card.rear)
			tempbitBitmap = cardBgBitmap;
		else {
			tempbitBitmap = card.bitmap;
		}
		canvas.drawBitmap(tempbitBitmap, card.getSRC(), card.getDST(), null);
	}

	// ��ť(������������)
	public void drawButton() {
		if (!hideButton) {
			canvas.drawText(buttonText[0], screen_width / 2 - 2 * cardWidth,
					screen_height - cardHeight * 2, paint);
			canvas.drawRect(new RectF(screen_width / 2 - 3 * cardWidth,
					screen_height - cardHeight * 5 / 2, screen_width / 2
							- cardWidth, screen_height - cardHeight * 11 / 6),
					paint);
		}

	}

	// Message
	public void drawMessage() {

		if (!message[1].equals("")) {
			canvas.drawText(message[1], screen_width / 2, screen_height
					- cardHeight * 2, paint);
		}
		if (!message[0].equals("")) {
			canvas.drawText(message[0], cardWidth * 3, screen_height / 4, paint);
		}
		if (!message[2].equals("")) {
			canvas.drawText(message[2], screen_width - cardWidth * 3,
					screen_height / 4, paint);
		}
	}

	// ��һ�����
	public void nextTurn() {
		turn = (turn + 1) % 3;
	}

	// ������ͷ��
	public void drawDizhuIcon() {

		float x = 0f, y = 0f;
		x = cardWidth * 1.5f;
		y = screen_height - 2f * cardHeight;
		canvas.drawBitmap(dizhuBitmap, x, y, null);

	}

	// ��ͼ����
	public void BeginOnDraw() {
		// ��
		synchronized (surfaceHolder) {
			try {
				canvas = surfaceHolder.lockCanvas();
				// ������
				drawBackground();
				// ����
				for (int i = 0; i < 3; i++)
					drawPlayer(i);
				// ����ť( ������)
				drawButton();
				// message���� ��3��String��
				drawMessage();
				// ������ͼ��
				drawDizhuIcon();
				// ��������
				for (int i = 0; i < dizhuList.size(); i++)
					drawCard(dizhuList.get(i));
				// ���ƽ���
				drawOutList();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	// ���º���
	public void update() {
		repaint = true;
	}

	// �����¼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ֻ���ܰ����¼�
		if (event.getAction() != MotionEvent.ACTION_UP)
			return true;
		EventAction eventAction = new EventAction(this, event);

		// ��ť�¼�
		eventAction.getButton();
		return true;
	}

	// ��ʱ��
	public void setTimer(int t, int flag) {
		while (t-- > 0) {
			Sleep(1000);
			message[flag] = t + "";
			update();
		}
		message[flag] = "";
	}

	// �жϳɹ�
	public void win() {

		if (playerCardList[1].size() == 0) {
			for (int i = 0; i < 54; i++) {
				card[i].rear = false;
			}
			start = false;
			Message msg = Message.obtain();
			msg.what = 0;
			Bundle builder = new Bundle();
			builder.putString("data", "��ϲ��Ӯ��");
			builder.putString("data1", "�Ա��������̣���������");
			msg.setData(builder);
			handler.sendMessage(msg);

		}
	}

}
