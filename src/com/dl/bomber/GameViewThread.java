package com.dl.bomber;

import android.util.Log;

public class GameViewThread extends Thread {
	GameView gameView;
	int sleepSpan = 300;
	int waitSpan = 1500;
	boolean flag = true;
	
	public GameViewThread(GameView gv){
		this.gameView = gv;
	}
	
	@Override
	public void run() {
		while(flag){
			{
				//background
				if(gameView.isLost()){
					Log.d("BOMB", "LOST");
					break;
				}
				if(gameView.isWin()){
					Log.d("BOMB", "WIN");
					break;
				}
				int leftBomb = gameView.BOMB_COUNT-gameView.checkFlag();
				GameView.setBombRest(leftBomb);
				Log.d("BOMB","LEFT:" + leftBomb);
			}
			//睡眠
			try{
				Thread.sleep(sleepSpan);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			//线程空转
			try{
				Thread.sleep(waitSpan);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.run();
	}
}
