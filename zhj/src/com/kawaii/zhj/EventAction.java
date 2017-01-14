package com.kawaii.zhj;

import android.view.MotionEvent;

public class EventAction {
	MotionEvent event;
	MyView view;

	public EventAction(MyView view, MotionEvent event) {
		this.event = event;
		this.view = view;
	}

	// 操作按钮事件
	public void getButton() {
		if (!view.hideButton) {
			float x = event.getX(), y = event.getY();
			// 左边按钮
			if ((x > view.screen_width / 2 - 3 * view.cardWidth)
					&& (y > view.screen_height - view.cardHeight * 5 / 2)
					&& (x < view.screen_width / 2 - view.cardWidth)
					&& (y < view.screen_height - view.cardHeight * 11 / 6)) {
				// 抢地主
				if (view.buttonText[0].equals("抢地主")) {
					// 加入地主牌
					int i = 17;
					for (Card card : view.dizhuList) {

						card.rear = false;
						card.setLocation(view.screen_width / 2 - (9 - i)
								* view.cardWidth * 2 / 3, view.screen_height
								- view.cardHeight);
						i++;
					}
					view.playerCardList[1].addAll(view.dizhuList);

					view.update();
					view.setTimer(3, 1);
					view.dizhuList.clear();
					view.update();
					view.turn = 1;
				}

				view.hideButton = !view.hideButton;
			}

		}
	}

}
