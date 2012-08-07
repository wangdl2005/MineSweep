package com.dl.bomber;



import java.util.ArrayList;
import java.util.Random;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;;

public class GameView extends SurfaceView implements OnTouchListener, SurfaceHolder.Callback{

	private DrawThread drawThread ;
	private GameViewThread gameViewThread;
	private static final int BMP_COUNTS = 12;
	final int COL_COUNT = 20;
	final int ROW_COUNT  = 10;
	final int BOMB_COUNT = 25;
	final int BMP_WIDTH = 30;
	private static final String TAG = "BOMB";
	static int selRow = 0;
	static int selCol  = 0;
	private boolean setFlag = false;
	
	private static int bombRest = 0;
	
	public static int getBombRest() {
		return bombRest;
	}
	public static void setBombRest(int bombRest) {
		GameView.bombRest = bombRest;
	}

	private Rect rectBtnFlag = new Rect();
	//blank ,1,2,3
	//   4	,5,6,7
	//   8	,//,Bomb,Flag
	private static ArrayList<Bitmap> bmps = new  ArrayList<Bitmap>(BMP_COUNTS);
	private Drawable[][] map = new Drawable[ROW_COUNT][COL_COUNT];
	private int[][] clickMap = new int[ROW_COUNT][COL_COUNT];//0:not click;1:click;2:flag
	//two layer one for isClick ,one for drawing
	
	public static ArrayList<Bitmap> getBmps() {
		return bmps;
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		getHolder().addCallback(this);
		drawThread  = new DrawThread(getHolder(), this);
		gameViewThread = new GameViewThread(this);
		initGame();
		
		gameViewThread.start();
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.drawThread.setFlag(true);
		drawThread.setIsViewOn(true);
        if(! drawThread.isAlive()){//如果后台重绘线程没起来,就启动它
        	try
        	{
        		drawThread.start();
        	}
        	catch(Exception e)
        	{
        		e.printStackTrace();
        	}        	
        }
        
        this.setOnTouchListener(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		this.drawThread.setIsViewOn(false);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		//draw button
		Paint paint = new Paint();
		paint.setColor(Color.RED);	
		if(true == setFlag){
			canvas.drawRect(new Rect(rectBtnFlag.left - 5,rectBtnFlag.top -5
					,rectBtnFlag.right+5,rectBtnFlag.bottom+5),paint);
		}
		canvas.drawBitmap(bmps.get(11),null, rectBtnFlag, null);
		//
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				if(clickMap[i][j] == 0){
					canvas.drawBitmap(bmps.get(0), j*BMP_WIDTH, i*BMP_WIDTH,null);
				}
				else if(clickMap[i][j] == 1){
					map[i][j].onDraw(canvas);
				}
				else if(clickMap[i][j] == 2){
					//flag
					canvas.drawBitmap(bmps.get(11), j*BMP_WIDTH, i*BMP_WIDTH,null);
				}
			}
		//super.onDraw(canvas);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {		
		Log.d(TAG, setFlag?"true":"false");
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			//click button
			int x = (int) event.getX() ;
			int y = (int) event.getY() ;
			if(rectBtnFlag.contains(x, y))
			{
				setFlag =  !setFlag;
			}
			//
			selCol = x/ BMP_WIDTH;
			selRow = y/ BMP_WIDTH;
			if(selRow < ROW_COUNT && selCol < COL_COUNT){
				if(clickMap[selRow][selCol] == 1){
					checkBlank(selRow, selCol);
				}
				if(clickMap[selRow][selCol] == 0){
					if(false == setFlag){
						clickMap[selRow][selCol]  = 1;
						//***显示全部
						if(map[selRow][selCol].getImgId() == 9){
							checkBlank(selRow, selCol);
						}
					}
					else
					{
						clickMap[selRow][selCol]  = 2;
						setFlag = false;
					}
				}			
			}
		}
		return false;
	}
	
	
	private void checkBlank(int row,int col){
		if(row - 1 >= 0 && col - 1>=0 && clickMap[row - 1][col - 1] == 0){
			clickMap[row - 1][col - 1] = 1;  
			if(map[row-1][col-1].getImgId()==9){
				checkBlank(row-1,col-1);
			}
		}
		if(row - 1 >= 0 && clickMap[row - 1][col ] == 0){
			clickMap[row - 1][col ] = 1;  
			if(map[row-1][col].getImgId()==9){
				checkBlank(row-1,col);
			}
		}
		if(row - 1 >= 0 && col +1<COL_COUNT && clickMap[row - 1][col +1] == 0){
			clickMap[row - 1][col + 1] = 1;  
			if(map[row-1][col+1].getImgId()==9){
				checkBlank(row-1,col+1);
			}
		}
		if(col - 1>=0 && clickMap[row ][col - 1] == 0){
			clickMap[row ][col - 1] = 1;  
			if(map[row][col-1].getImgId()==9){
				checkBlank(row,col-1);
			}
		}
		if(col+1<COL_COUNT && clickMap[row ][col + 1] == 0){
			clickMap[row ][col +1] = 1;  
			if(map[row][col+1].getImgId()==9){
				checkBlank(row,col+1);
			}
		}
		if(row +1 <ROW_COUNT&& col - 1>=0 && clickMap[row +1][col - 1] == 0){
			clickMap[row +1][col - 1] = 1;  
			if(map[row+1][col-1].getImgId()==9){
				checkBlank(row+1,col-1);
			}
		}
		if(row +1 <ROW_COUNT && clickMap[row +1][col ] == 0){
			clickMap[row +1][col ] = 1;  
			if(map[row +1][col ].getImgId()==9){
				checkBlank(row +1,col );
			}
		}
		if(row +1 <ROW_COUNT && col +1<COL_COUNT && clickMap[row + 1][col + 1] == 0){
			clickMap[row + 1][col + 1] = 1;  
			if(map[row+1][col+1].getImgId()==9){
				checkBlank(row+1,col+1);
			}
		}
	}
	
	public int checkFlag(){
		int count = 0;
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				if(clickMap[i][j] == 2)
				{
					++count;
				}
			}
		return count;
	}
	
	public boolean isLost(){
		boolean flag = false;
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				if(map[i][j].getImgId() == 10 && clickMap[i][j]==1){
					flag = true;
					break;
				}
			}
		return flag;
	}
	
	public boolean isWin(){
		boolean flag = false;
		int count = BOMB_COUNT;
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				if(map[i][j].getImgId() == 10)
				{
					if(clickMap[i][j] != 2){
						flag = false;
						break;
					}
					else{
						--count;
					}
				}
			}
		flag =  (count==0);
		return flag;
	}
	
	private void initGame(){
		initBitmap(this.getResources());
		initMatrix();
		
		bombRest = BMP_COUNTS;
		rectBtnFlag.set(650, 100, 700, 150);
	}
	
	private void initMatrix(){
		//create a 20 * 10 matrix ,including 20 bombs;
		//create matrix;
		int[][] mat = new int[ROW_COUNT][COL_COUNT];
		//random array
		ArrayList<Integer> tmpArray =new ArrayList<Integer>();
		//initial 
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				mat[i][j] = 0;//blank
				clickMap[i][j] = 0;
				tmpArray.add(i*COL_COUNT + j, i*COL_COUNT + j);
			}
		//creating 20 bombs;
		Random random = new Random();
		int rand = 0;
		int rand_count = 0;
		while(rand_count < BOMB_COUNT){
			rand = random.nextInt(ROW_COUNT * COL_COUNT - rand_count);
			++rand_count;
			int randInt = tmpArray.get(rand);
			mat[randInt/COL_COUNT][randInt%COL_COUNT] = 10;//bomb
			tmpArray.remove(rand);
		}
		//recalculate mat
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				if(mat[i][j] == 10){
					continue;
				}
				int count = 0;
				//-----------
				if(i-1>=0 && j-1 >=0 && mat[i-1][j-1] ==10){
					++count;
				}
				if(i-1>=0 &&  mat[i-1][j] ==10){
					++count;
				}
				if(i-1>=0 && j+1 <COL_COUNT && mat[i-1][j+1] ==10){
					++count;
				}
				if(j-1 >=0 && mat[i][j-1] ==10){
					++count;
				}
				if( j+1 <COL_COUNT&& mat[i][j+1] ==10){
					++count;
				}
				if(i+1<ROW_COUNT&& j-1 >=0 && mat[i+1][j-1] ==10){
					++count;
				}
				if(i+1<ROW_COUNT&& mat[i+1][j] ==10){
					++count;
				}
				if(i+1<ROW_COUNT&& j+1 <COL_COUNT && mat[i+1][j+1] ==10){
					++count;
				}
				if(count ==0 ) count = 9;
				mat[i][j] = count;
			}
		//init map
		for(int i=0;i<ROW_COUNT;++i)
			for(int j=0;j<COL_COUNT;++j)
			{
				map[i][j] = new Drawable(i, j, j*BMP_WIDTH, i*BMP_WIDTH,mat[i][j]);
			}
	}
	
	private void initBitmap(Resources r){
		Bitmap bmpElement = BitmapFactory.decodeResource(r, R.drawable.bomber);
		for(int i=0;i<BMP_COUNTS;++i){
			bmps.add(i, Bitmap.createBitmap(bmpElement, i%4* BMP_WIDTH, i/4 * BMP_WIDTH, BMP_WIDTH, BMP_WIDTH));
		}
		bmpElement = null;
	}

	//刷新帧线程
		class DrawThread extends Thread{

			private int sleepSpan = 100;//睡眠的毫秒数 
			private SurfaceHolder surfaceHolder;
			private GameView gameView;
			private boolean isViewOn = false;
			private boolean flag = true;
	        public DrawThread(SurfaceHolder surfaceHolder, GameView gameView) {//构造器
	        	super.setName("==GameView.DrawThread");
	            this.surfaceHolder = surfaceHolder;
	            this.gameView = gameView;
	        }
	        
	        public void setFlag(boolean flag) {//设置循环标记位
	        	this.flag = flag;
	        }
	        
	        public void setIsViewOn(boolean isViewOn){
	        	this.isViewOn = isViewOn;
	        }
	        
			public void run() {
				Canvas c;
				while(flag){
		            while (isViewOn) {
		                c = null;
		                try {
		                	// 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
		                    c = this.surfaceHolder.lockCanvas(null);
		                    synchronized (this.surfaceHolder) {
		                    	gameView.onDraw(c);
		                    }
		                } finally {
		                    if (c != null) {
		                    	//更新屏幕显示内容
		                        this.surfaceHolder.unlockCanvasAndPost(c);
		                    }
		                }
		                try{
		                	Thread.sleep(sleepSpan);//睡眠指定毫秒数
		                }
		                catch(Exception e){
		                	e.printStackTrace();
		                }
		            }
		            try{
		            	Thread.sleep(1500);//睡眠指定毫秒数
		            }
		            catch(Exception e){
		            	e.printStackTrace();
		            }
				}
			}
		}
	
}
