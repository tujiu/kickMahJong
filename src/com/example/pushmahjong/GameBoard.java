package com.example.pushmahjong;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.example.pushmahjong.MahJong.AnimationFinished;
import com.example.pushmahjong.MahJong.MahjongType;

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

	private static final int BASE_SCORE = 100;
	private final int BOARD_SIZE = 5;
	private final int LINE_WIDTH = 3;
	private final int GAP = 10;	// dp
	private final int SPOT_COUNT = 3;
	private final int MOVE_DISTANCE = 10;
	
	private Context context;
	
	private enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT
	}
	private enum BonusType {
		DUIZI,
		SANTIAO,
		SITIAO,
		LIANGDUI,
		HULU_INCREASE,
		HULU_DECREASE,
		DUIZI_HULU_INCREASE_2,
		HULU_INCREASE_2,
		HULU_INCREASE_2_DUIZI,
		DUIZI_HULU_INCREASE_3,
		HULU_INCREASE_3,
		HULU_INCREASE_3_DUIZI,
		DUIZI_HULU_INCREASE_4,
		HULU_INCREASE_4,
		HULU_INCREASE_4_DUIZI,
		HULU_INCREASE_5,
		DUIZI_HULU_DECREASE_2,
		HULU_DECREASE_2,
		HULU_DECREASE_2_DUIZI,
		DUIZI_HULU_DECREASE_3,
		HULU_DECREASE_3,
		HULU_DECREASE_3_DUIZI,
		DUIZI_HULU_DECREASE_4,
		HULU_DECREASE_4,
		HULU_DECREASE_4_DUIZI,
		HULU_DECREASE_5,
		HULU_DUIZI_INCREASE,
		HULU_DUIZI_DECREASE,
		SANTIAO_DUIZI,
		NULL,
	}
	private Direction direction;
	private int gap;
	private int mjWidth;
	private int mjHeight;
	private int gridWidth;

	private int gridHeight;
	
	private int screenWidth;
	private int width;
	private int height;
	
	private int score;
	
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
	private boolean underanimation =false;
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
		
		score = 0;
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
		if (touchable && !underanimation) {
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
			underanimation = true;
		}
	}

	private void moveUp() {
		// animation first
		for (int j = 0; j < BOARD_SIZE; j++) {
			int count = 0;
			for (int i = 0; i < BOARD_SIZE; i++) {
				if (hasMahJong[i][j] != null) {
					if (i > count) {
						hasMahJong[i][j].moveTo(this, count, j);
						direction = Direction.UP;
						moved = true;
					}
					count++;
				}
			}
		}
	}

	private void moveDown() {
		// animation first
		for (int j = 0; j < BOARD_SIZE; j++) {
			int count = 0;
			for (int i = BOARD_SIZE - 1; i >= 0; i--) {
				if (hasMahJong[i][j] != null) {
					if (i < BOARD_SIZE - 1 - count) {
						hasMahJong[i][j].moveTo(this, BOARD_SIZE - 1 - count, j);
						direction = Direction.DOWN;
						moved = true;
					}
					count++;
				}
			}
		}
	}

	private void moveLeft() {
		// animation first
		for (int i = 0; i < BOARD_SIZE; i++) {
			int count = 0;
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (hasMahJong[i][j] != null) {
					if (j > count) {
						hasMahJong[i][j].moveTo(this, i, count);
						direction = Direction.LEFT;
						moved = true;
					}
					count++;
				}
			}
		}
	}

	private void moveRight() {
		// animation first
		for (int i = 0; i < BOARD_SIZE; i++) {
			int count = 0;
			for (int j = BOARD_SIZE - 1; j >= 0; j--) {
				if (hasMahJong[i][j] != null) {
					if (j < BOARD_SIZE - 1 - count) {
						hasMahJong[i][j].moveTo(this, i, BOARD_SIZE - 1 - count);
						direction = Direction.RIGHT;
						moved = true;
					}
					count++;
				}
			}
		}
	}
	
	private boolean kickUp() {
		// animation first
		int savedScore = score;
		for (int j = 0; j < BOARD_SIZE; j++) {
			int count = 0;
			BonusType bonusType = BonusType.NULL;
			MahjongType type = MahjongType.NULL;
			int savedValue = -1;
			for (int i = 0; i < BOARD_SIZE; i++) {
				if (hasMahJong[i][j] == null) {
					getScoreAndHideMahjonh(count, bonusType, i, j);
					break;
				} else {
					if (i == 0) {
						type = hasMahJong[i][j].getType();
					} else {
						if (hasMahJong[i][j].getType() == type) {
							count++;
							bonusType = checkBonus(hasMahJong[i][j].getValue(), 
									hasMahJong[i][j-1].getValue(), 
									bonusType,
									savedValue);
						} else {
							getScoreAndHideMahjonh(count, bonusType, i, j);
							count = 1;
							type = hasMahJong[i][j].getType();
						}
					}
				}
			}
		}
		if (savedScore < score) {
			return true;
		}
		return false;
	}
	
	private boolean kickDown() {
		// animation first
		for (int j = 0; j < BOARD_SIZE; j++) {
			int count = 0;
			for (int i = BOARD_SIZE - 1; i >= 0; i--) {
				if (hasMahJong[i][j] != null) {
					if (i < BOARD_SIZE - 1 - count) {
						hasMahJong[i][j].moveTo(this, BOARD_SIZE - 1 - count, j);
						direction = Direction.DOWN;
						moved = true;
					}
					count++;
				}
			}
		}
		return false;
	}
	
	private boolean kickLeft() {
		// animation first
		for (int i = 0; i < BOARD_SIZE; i++) {
			int count = 0;
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (hasMahJong[i][j] != null) {
					if (j > count) {
						hasMahJong[i][j].moveTo(this, i, count);
						direction = Direction.LEFT;
						moved = true;
					}
					count++;
				}
			}
		}
		return false;
	}
	
	private boolean kickRight() {
		// animation first
		for (int i = 0; i < BOARD_SIZE; i++) {
			int count = 0;
			for (int j = BOARD_SIZE - 1; j >= 0; j--) {
				if (hasMahJong[i][j] != null) {
					if (j < BOARD_SIZE - 1 - count) {
						hasMahJong[i][j].moveTo(this, i, BOARD_SIZE - 1 - count);
						direction = Direction.RIGHT;
						moved = true;
					}
					count++;
				}
			}
		}
		return false;
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
		// to kick the same mahjong
		if (kickMahJong()) {
			
		} else {
			if (!spotted) {
				spotMahJong();
				spotted = true;
			}
		}
		underanimation = false;
	}

	private boolean kickMahJong() {
		switch (direction) {
		case UP:
			return kickUp();
		case DOWN:
			return kickDown();
		case LEFT:
			return kickLeft();
		case RIGHT:
			return kickRight();
		default:
			return false;
		}
	}

	private BonusType checkBonus(int value, int preValue, BonusType bonusType, int savedValue) {
		if (value == preValue) {
			// same value
			switch (bonusType) {
			case DUIZI:
				bonusType = BonusType.SANTIAO;
				break;
			case SANTIAO:
				bonusType = BonusType.SITIAO;
				break;
			default:
				bonusType = BonusType.DUIZI;
				break;
			}
		} else if (value == preValue + 1) {
			bonusType = BonusType.HULU_INCREASE;
		} else if (value == preValue - 1) {
			bonusType = BonusType.HULU_DECREASE;
		}
		return bonusType;
	}

	private void getScoreAndHideMahjonh(int count, BonusType bonusType, int i, int j) {
		if (count > 1) {
			for (int k = 0; k < count; k++) {
				switch (direction) {
				case UP:
					hasMahJong[i][j-k].HideMe(this);
					break;
				case DOWN:
					hasMahJong[i][j+k].HideMe(this);
					break;
				case LEFT:
					hasMahJong[i-k][j].HideMe(this);
					break;
				case RIGHT:
					hasMahJong[i+k][j].HideMe(this);
					break;
				default:
					break;
				}
			}
			score += count * BASE_SCORE * 2;
		}
	}

}
