package com.example.pushmahjong;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MahJong extends ImageView {

	public static final int TONG_TIAO_WAN_MAX = 9;
	public static final int FENG_MAX = 4;
	public static final int ZI_MAX = 3;
	public static final int TYPE_TOTAL = TONG_TIAO_WAN_MAX * 3 + FENG_MAX + ZI_MAX;
	public static final int TOTAL = TYPE_TOTAL * 4;
	private static final long ANIMATION_DURATION = 3000;

	public enum mahjongType {
		TONG, // 筒
		TIAO, // 条
		WAN, // 万
		FENG, // 风
		ZI, // 中发白
		NULL
	};

	private int id;
	private int drawableId;
	private mahjongType type;
	private int value;
	private int size;

	private boolean isShowed = false;
	private boolean toBeKickMark = false;

	private Context context;
	private int positionX;
	private int positionY;
	private int cordinateX;
	private int cordinateY;
	private boolean moveHorizon;
	
	public interface AnimationFinished {
		public void onAnimationFinished();
	}
	
	private AnimationFinished animationFinished;
	
	public void setAnimationFinished(AnimationFinished animationFinished) {
		this.animationFinished = animationFinished;
	}

	public MahJong(Context context, int id) {
		super(context);
		this.context = context;
		this.id = id;
		init();
	}

	private void init() {
		final int[] drawableIds = {
				R.drawable.b1,
				R.drawable.b2,
				R.drawable.b3,
				R.drawable.b4,
				R.drawable.b5,
				R.drawable.b6,
				R.drawable.b7,
				R.drawable.b8,
				R.drawable.b9,
				
				R.drawable.w1,
				R.drawable.w2,
				R.drawable.w3,
				R.drawable.w4,
				R.drawable.w5,
				R.drawable.w6,
				R.drawable.w7,
				R.drawable.w8,
				R.drawable.w9,
				
				R.drawable.t1,
				R.drawable.t2,
				R.drawable.t3,
				R.drawable.t4,
				R.drawable.t5,
				R.drawable.t6,
				R.drawable.t7,
				R.drawable.t8,
				R.drawable.t9,
				
				R.drawable.f1,
				R.drawable.f2,
				R.drawable.f3,
				R.drawable.f4,
				
				R.drawable.z1,
				R.drawable.z2,
				R.drawable.z3,
		};
		int id = this.id % TYPE_TOTAL;
		drawableId = drawableIds[id];
		if (id < TONG_TIAO_WAN_MAX - 1) {
			type = mahjongType.TONG;
			value = id;
		} else if (id < TONG_TIAO_WAN_MAX * 2 - 1) {
			type = mahjongType.TIAO;
			value = id - TONG_TIAO_WAN_MAX;
		} else if (id < TONG_TIAO_WAN_MAX * 3 - 1) {
			type = mahjongType.WAN;
			value = id - TONG_TIAO_WAN_MAX * 2;
		} else if (id < TONG_TIAO_WAN_MAX * 3 + FENG_MAX - 1) {
			type = mahjongType.FENG;
			value = id - TONG_TIAO_WAN_MAX * 3;
		} else {
			type = mahjongType.ZI;
			value = id - TONG_TIAO_WAN_MAX * 3 - FENG_MAX;
		}
		
		setImageResource(drawableId);
	}

	public int getMaxValue(mahjongType t) {
		int retValue = 0;
		switch (t) {
		case TONG:
		case TIAO:
		case WAN:
			retValue = TONG_TIAO_WAN_MAX;
			break;
		case ZI:
			retValue = ZI_MAX;
			break;
		case FENG:
			retValue = FENG_MAX;
			break;
		default:
			break;
		}
		return retValue;
	}

	public int getImageSize() {
		Drawable drawable = context.getResources().getDrawable(R.drawable.b1);
		return drawable.getIntrinsicWidth();
	}

	public void showMe(GameBoard board, int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;

		if (!isShowed) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);// 与父容器的左侧对齐
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);// 与父容器的上侧对齐
			cordinateX = lp.topMargin = board.getCordinateX(positionX);
			cordinateY = lp.leftMargin = board.getCordinateY(positionY);
			
			board.addView(this, lp);
			isShowed = true;
			
			board.getHasMahJong()[positionX][positionY] = this;
		}
	}

	public void HideMe(GameBoard board) {
		if (isShowed) {
			isShowed = false;
			board.removeView(this);
			
			board.getHasMahJong()[positionX][positionY] = null;
		}
	}
	
	public mahjongType getType() {
		return type;
	}

	public void setType(mahjongType type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	public boolean isToBeKickMark() {
		return toBeKickMark;
	}

	public void setToBeKickMark(boolean toBeKickMark) {
		this.toBeKickMark = toBeKickMark;
	}

	public void moveTo(GameBoard board, int x, int y) {
		animationFinished = board;
		int end, start;
		if (x == positionX) {
			end = board.getCordinateX(y);
			start = cordinateY;
			moveHorizon = true;
		} else {
			end = board.getCordinateY(x);
			start = cordinateX;
			moveHorizon = false;
		}
		ValueAnimator va = ValueAnimator.ofInt(start, end);
		va.setDuration(ANIMATION_DURATION);
		va.setInterpolator(new AccelerateInterpolator());
		va.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				animationFinished.onAnimationFinished();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
		va.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				int value = Integer.valueOf(animation.getAnimatedValue().toString());
				Log.d("tujiu", ""+value);
				if (moveHorizon) {
					cordinateY = value;
				} else {
					cordinateX = value;
				}
				RelativeLayout.LayoutParams lp = (LayoutParams) getLayoutParams();
				lp.leftMargin = cordinateY;
				lp.topMargin = cordinateX;
				setLayoutParams(lp);
			}
		});
		va.start();
		
		board.getHasMahJong()[positionX][positionY] = null;
		positionX = x;
		positionY = y;
		board.getHasMahJong()[positionX][positionY] = this;
		
	}
}
