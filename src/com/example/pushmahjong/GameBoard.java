package com.example.pushmahjong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.example.pushmahjong.MahJong.AnimationFinished;

import tool.ScreenHelper;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class GameBoard extends RelativeLayout implements AnimationFinished {

	private final int BOARD_SIZE = 5;
	private final int LINE_WIDTH = 3;
	private final int GAP = 10;	// dp
	private final int SPOT_COUNT = 5;
	private final int MOVE_DISTANCE = 10;
	
	private Context context;
	
	private int gap;
	private int mjWidth;
	private int mjHeight;
	private int gridWidth;

	private int gridHeight;
	
	private int screenWidth;
	private int width;
	private int height;
	
	private Paint paint;
	private Bitmap bitmap;
	private Canvas canvas;
	
	private int[] xPosition = new int[BOARD_SIZE];
	private int[] yPosition = new int[BOARD_SIZE];
	private MahJong[][] hasMahJong = new MahJong[BOARD_SIZE][BOARD_SIZE];

	Random random = new Random();
	
	private ArrayList<MahJong> mahjongList;
	private float downX;
	private float downY;
	private float moveX;
	private float moveY;
	private boolean moved =false;
	private boolean touchable = true;
	private boolean spotted = false;
	
	public GameBoard(Context context, ArrayList<MahJong> mahjongList, int imageWidth, int imageHeight) {
		super(context);
		this.context = context;
		mjWidth = imageWidth;
		mjHeight = imageHeight;
		this.mahjongList = mahjongList;
		init(context);
		
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(bitmap, 0, 0, null);
		super.onDraw(canvas);
	}
	
	private void init(Context context) {
		calculateSize();
		paint = new Paint();
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(LINE_WIDTH);
		
		bitmap = Bitmap.createBitmap(screenWidth, screenWidth, Config.ARGB_8888);
		canvas = new Canvas(bitmap);
	}
	
	private int calculateSize() {
		screenWidth = ScreenHelper.getScreenWidth((Activity) context);
		gap = (int) (getResources().getDisplayMetrics().density * GAP);
		gridWidth = mjWidth + LINE_WIDTH;
		gridHeight = mjHeight + LINE_WIDTH;
		width = BOARD_SIZE * gridWidth;
		height = BOARD_SIZE * gridHeight;
		
		for (int i = 0; i < BOARD_SIZE; i++) {
			xPosition[i] = i * gridWidth + LINE_WIDTH + gap;
			yPosition[i] = i * gridHeight + LINE_WIDTH + gap;
		}
		
		return 0;
	}

	public void startGame(ArrayList<MahJong> arrayList) {
		spotMahJong();
		drawBoard();
	}
	
	private void spotMahJong() {
		for (int i = 0; i < SPOT_COUNT; i++) {
			int position = randomPosition();
			mahjongList.get(random.nextInt(mahjongList.size())).showMe(this,
					position/BOARD_SIZE, position%BOARD_SIZE);
		}
	}
	
	private int randomPosition() {
		int position; 
		do {
			position = random.nextInt(BOARD_SIZE*BOARD_SIZE);
		} while (hasMahJong[position/BOARD_SIZE][position%BOARD_SIZE] != null);
		return position;
	}

	private void drawBoard() {
		for (int i = 0; i <= BOARD_SIZE; i++) {
			canvas.drawLine(gap, gap + i * gridHeight, gap + width, gap + i * gridHeight, paint);
			canvas.drawLine(gap + i * gridWidth, gap, gap + i * gridWidth, gap + height, paint);
		}
		invalidate();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			moveX = event.getX() - downX;
			moveY = event.getY() - downY;
			checkMoved();
			break;
		case MotionEvent.ACTION_UP:
			touchable = true;
			moved = false;
			break;
		default:
			break;
		}
		return true;
	}

	private void checkMoved() {
		// TODO Auto-generated method stub
		if (touchable) {
			if (moveX > MOVE_DISTANCE) {
				moveRight();
			} else if (moveX < -MOVE_DISTANCE) {
				moveLeft();
			} else if (moveY > MOVE_DISTANCE) {
				moveDown();
			} else if (moveY < -MOVE_DISTANCE) {
				moveUp();
			} else {
				
			}
		}
		if (moved) {
			touchable = false;
			spotted = false;
		}
	}

	private void moveUp() {
		// TODO Auto-generated method stub
		// animation first
		for (int j = 0; j < BOARD_SIZE; j++) {
			int count = 0;
			for (int i = 0; i < BOARD_SIZE; i++) {
				if (hasMahJong[i][j] != null) {
					if (i > count) {
						hasMahJong[i][j].moveTo(this, count, j);
						moved = true;
					}
					count++;
				}
			}
		}
		
//		MahJong.mahjongType type = MahJong.mahjongType.NULL;
//		int sameTypeCount = 1;
//		for (int j = BOARD_SIZE - 1; j >= 0; j--) {
//			if (hasMahJong[BOARD_SIZE - 1][j] != null) {
//				type = hasMahJong[BOARD_SIZE - 1][j].getType();
//				if (type == hasMahJong[BOARD_SIZE - 1][j].getType()) {
//					// 前面有同样花色的麻将，计数器加1
//					sameTypeCount++;
//				} else {
//					// 不一样，先修改type，如果超过1个，消除那几个麻将
//					if (sameTypeCount > 1) {
//						for (int k = j; k < k + sameTypeCount; k++) {
//							// kick some mahjong
//							hasMahJong[BOARD_SIZE - 1][k].HideMe(this);
//						}
//					}
//					sameTypeCount = 1;
//				}
//			}
//		}
	}

	private void moveDown() {
		// TODO Auto-generated method stub
		// animation first
		for (int j = 0; j < BOARD_SIZE; j++) {
			int count = 0;
			for (int i = BOARD_SIZE - 1; i >= 0; i--) {
				if (hasMahJong[i][j] != null) {
					if (i < BOARD_SIZE - 1 - count) {
						hasMahJong[i][j].moveTo(this, BOARD_SIZE - 1 - count, j);
						moved = true;
					}
					count++;
				}
			}
		}
	}

	private void moveLeft() {
		// TODO Auto-generated method stub
		
	}

	private void moveRight() {
		// TODO Auto-generated method stub
		
	}
	
	public int getCordinateX(int x) {
		return xPosition[x];
	}
	
	public int getCordinateY(int y) {
		return yPosition[y];
	}
	
	public MahJong[][] getHasMahJong() {
		return hasMahJong;
	}
	
	public int getGridWidth() {
		return gridWidth;
	}

	@Override
	public void onAnimationFinished() {
		// TODO Auto-generated method stub
		if (!spotted) {
			spotMahJong();
			spotted = true;
		}
	}
}
