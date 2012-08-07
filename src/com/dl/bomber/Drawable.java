package com.dl.bomber;

import android.graphics.Canvas;

public class Drawable {
	private int imgId = 0;
	private int row = 0;
	private int col = 0;
	private int x = 0;
	private int y =0;
	
	public Drawable(int row,int col,int x ,int y,int id){
		this.row = row;
		this.col = col;
		this.x = x;
		this.y = y;
		this.imgId = id;
	}
	
	public int getImgId(){
		return this.imgId;
	}
	
	public void onDraw(Canvas canvas){
		canvas.drawBitmap(GameView.getBmps().get(imgId),x,y,null);
	}
	
}
