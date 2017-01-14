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
	// 屏幕宽度和高度
	int screen_height;
	int screen_width;
	// 图片资源
	Bitmap cardBitmap[] = new Bitmap[54];
	Bitmap bgBitmap; // 背面
	Bitmap cardBgBitmap;// 图片背面
	Bitmap dizhuBitmap;// 地主图标
	// 基本参数
	int cardWidth, cardHeight;
	// 画笔
	Paint paint = null;
	// 牌对象
	Card card[] = new Card[54];
	// 按钮
	String buttonText[] = new String[1];
	// 提示
	String message[] = new String[3];
	boolean hideButton = true;
	// List
	List<Card> playerCardList[] = new TArrayList[3];

	// 地主牌
	List<Card> dizhuList = new TArrayList();
	// 轮流
	int turn = -1;
	// 已出牌表
	List<Card> outList[] = new TArrayList[3];

	Handler handler;
	// 开始发牌
	int iTmp[] = { 0, 13, 1, 14, 2, 15, 3, 16, 4, 17, 5, 18, 6, 19, 7, 20, 12 };// 地主牌
	int iTmp1[] = { 51, 52, 53 };
	int iLmp[] = { 41, 42, 43 };
	int iRmp[] = { 45, 46, 47 };
	int iPlay1[] = { 16, 2, 2 };
	static int iPlayCount = 0;

	// 构造函数
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
		Log.i("surfaceCreated", "这里调用了");
		start = true;
		screen_height = getHeight();
		screen_width = getWidth();
		// 初始化
		InitBitMap();
		// 开始游戏进程
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 开始发牌
				if (iPlayCount == 3) {
					// start = false;
					iPlayCount = 0;
				}
				handCards();
				Log.i("turn", "" + turn);
				// 等待地主选完
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
		// 开始绘图进程
		drawThread = new Thread(this);
		drawThread.start();
	}

	// 画已走的牌
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
		message[0] = "不要";

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
		message[2] = "不要";
		update();
		nextTurn();
	}

	// player1
	public void player1(int m) {
		Sleep(1000);
		// 开始写出牌的了
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

	// 主要绘图线程
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

	// 初始化图片,参数
	public void InitBitMap() {
		turn = -1;
		// 1. 装牌
		int count = 0;
		for (int i = 1; i <= 4; i++) {
			for (int j = 3; j <= 15; j++) {
				// 根据名字找出ID
				String name = "a" + i + "_" + j;
				ApplicationInfo appInfo = getContext().getApplicationInfo();
				int id = getResources().getIdentifier(name, "drawable",
						appInfo.packageName);
				cardBitmap[count] = BitmapFactory.decodeResource(
						getResources(), id);
				card[count] = new Card(cardBitmap[count].getWidth(),
						cardBitmap[count].getHeight(), cardBitmap[count]);
				// 设置Card的名字
				card[count].setName(name);
				count++;
			}
		}
		// 最后小王，大王
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
		// 地主图标
		dizhuBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.dizhu);
		// 背景
		bgBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);
		cardBgBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.cardbg);
		// 按钮

		buttonText[0] = new String();
		buttonText[0] = "抢地主";
		// 消息,已出牌

		for (int i = 0; i < 3; i++) {
			message[i] = new String("");
			outList[i] = new TArrayList();
		}

		// 画按钮
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(cardWidth * 2 / 5);
		paint.setAntiAlias(true);// 锯齿痕迹不那么明显
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(1.0f);
		paint.setTextAlign(Align.CENTER);

	}

	// 画背景
	public void drawBackground() {

		Rect src = new Rect(0, 0, bgBitmap.getWidth() * 3 / 4,
				2 * bgBitmap.getHeight() / 3);

		Rect dst = new Rect(0, 0, screen_width, screen_height);
		canvas.drawBitmap(bgBitmap, src, dst, null);
	}

	// 发牌
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
			Log.i("myview.java", "这里吃掉内存");
			e.printStackTrace();
		}
	}

	// 玩家牌
	public void drawPlayer(int player) {
		if (playerCardList[player] != null && playerCardList[player].size() > 0) {
			for (Card card : playerCardList[player])
				drawCard(card);
		}
	}

	// 画牌
	public void drawCard(Card card) {
		Bitmap tempbitBitmap;
		if (card.rear)
			tempbitBitmap = cardBgBitmap;
		else {
			tempbitBitmap = card.bitmap;
		}
		canvas.drawBitmap(tempbitBitmap, card.getSRC(), card.getDST(), null);
	}

	// 按钮(抢地主，出牌)
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

	// 下一个玩家
	public void nextTurn() {
		turn = (turn + 1) % 3;
	}

	// 画地主头像
	public void drawDizhuIcon() {

		float x = 0f, y = 0f;
		x = cardWidth * 1.5f;
		y = screen_height - 2f * cardHeight;
		canvas.drawBitmap(dizhuBitmap, x, y, null);

	}

	// 画图函数
	public void BeginOnDraw() {
		// 锁
		synchronized (surfaceHolder) {
			try {
				canvas = surfaceHolder.lockCanvas();
				// 画背景
				drawBackground();
				// 画牌
				for (int i = 0; i < 3; i++)
					drawPlayer(i);
				// 画按钮( 抢地主)
				drawButton();
				// message部分 用3个String存
				drawMessage();
				// 画地主图标
				drawDizhuIcon();
				// 画地主牌
				for (int i = 0; i < dizhuList.size(); i++)
					drawCard(dizhuList.get(i));
				// 出牌界面
				drawOutList();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null)
					surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	// 更新函数
	public void update() {
		repaint = true;
	}

	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 只接受按下事件
		if (event.getAction() != MotionEvent.ACTION_UP)
			return true;
		EventAction eventAction = new EventAction(this, event);

		// 按钮事件
		eventAction.getButton();
		return true;
	}

	// 计时器
	public void setTimer(int t, int flag) {
		while (t-- > 0) {
			Sleep(1000);
			message[flag] = t + "";
			update();
		}
		message[flag] = "";
	}

	// 判断成功
	public void win() {

		if (playerCardList[1].size() == 0) {
			for (int i = 0; i < 54; i++) {
				card[i].rear = false;
			}
			start = false;
			Message msg = Message.obtain();
			msg.what = 0;
			Bundle builder = new Bundle();
			builder.putString("data", "恭喜你赢了");
			builder.putString("data1", "淘宝搜索店铺：卡哇伊漫");
			msg.setData(builder);
			handler.sendMessage(msg);

		}
	}

}
