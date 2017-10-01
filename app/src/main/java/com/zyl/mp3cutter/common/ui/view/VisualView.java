package com.zyl.mp3cutter.common.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VisualView extends View {

	private Rect mRect = new Rect();
	private MyPaint mPaint = new MyPaint();
	private byte[] mBytes;// 接受波形
	private float mPointUp[];// 上半波形
	private float mPointDown[];// 下半波形
	private final float angleSpan = 1;// 角度变化
	private final float moveSpan = 2f;// 移动步径
	public static float degrees = 0;// 角度
	public float maxMove;// 最大移动
	public float move = moveSpan;// 当前移动
	private boolean flagDraw = true;
	private boolean flagStar = true;
	private float[] mOuterPoints;
	private float[] mInnerPoints;
	private final float LENGTH = 300;
	private float degreeStar = 0;
	private float scale = 0;
	private List<float[]> localList;
	private List<Integer> sizeList;
	private final int MAX_COUNT = 10;
	private final int MAX_SIZE = 50;
	private static int col[] = { Color.BLUE, Color.CYAN, Color.GREEN,
			Color.MAGENTA, Color.RED, Color.YELLOW, Color.WHITE };
	private float []mPoints;
	private float []mNorPoints;
	private final float SPAN = 1.0f;
	public int style=0;// 1--线  2--线山   3--山

	public VisualView(Context context,AttributeSet attrs) {
		super(context,attrs);
		mPaint.setColor(Color.YELLOW);
		mPaint.setStrokeWidth(2f);
		mPaint.setAntiAlias(true);
		mOuterPoints = new float[10];
		mInnerPoints = new float[10];
	}

	public void update(byte[] mByte) {
		this.mBytes = mByte;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		mRect.set(0, 0, getWidth(), getHeight());
		if (flagStar) {
			initOuterPoints();
			initList();
		}
		float w = getWidth() / 2;
		float h = getHeight() / 2;

		for (int i = 0; i < MAX_COUNT; i++) {
			int size = sizeList.get(i);
			if (size >= MAX_SIZE) {
				del(i);
			} else {
				float[] local = localList.get(i);
				RectF r = new RectF(local[0] - size, local[1] - size, local[0]
						+ size, local[1] + size);
				mPaint.setColor(col[(int) (Math.random() * col.length)]);
				mPaint.setStrokeWidth(1f);
				mPaint.setStyle(Style.STROKE);
				mPaint.setAlpha(200);
				canvas.drawOval(r, mPaint);
				sizeList.remove(i);
				sizeList.add(i, ++size);
			}
		}

		Paint cycle = new Paint();
		int color[] = new int[2];
		cycle.setARGB(200, 158, 52, 146);
		color[0] = cycle.getColor();
		cycle.setARGB(200, 174, 137, 224);
		RadialGradient rg = new RadialGradient(w, h, scale + 10, color[0],
				color[1], TileMode.MIRROR);
		cycle.setShader(rg);
		RectF rf = new RectF(w - scale / 2, h - scale / 2, w + scale / 2, h
				+ scale / 2);
		canvas.drawOval(rf, cycle);
		scale = (scale + 20) % 900;

		degreeStar = (degreeStar - angleSpan + 2) % (360);
		Matrix m = canvas.getMatrix();
		canvas.rotate(-degreeStar, mRect.width() / 2, mRect.height() / 2);
		drawStar(canvas, 180);
		drawStar(canvas, 200);
		drawStar(canvas, 230);
		canvas.setMatrix(m);
		
		mPaint.setARGB(240, 172, 175, 64);
		switch(style)
		{
		case 1:
			drawStyleOne(canvas);
			break;
		case 2:
			drawStyleTwo(canvas);
			break;
		case 3:
			drawSytleThree(canvas);
			break;
		}

	}
	
	private void drawStyleOne(Canvas canvas)
	{
		canvas.rotate(degrees, mRect.width() / 2, mRect.height() / 2);
		degrees = (degrees + angleSpan) % 360;
		initPoints();
		maxMove = 2 * (mRect.height() / 2 - mRect.height() / 3);
		move = (move + moveSpan) % maxMove;
		drawDownRect(canvas);
		drawUpRect(canvas);
	}

	public void drawDownRect(Canvas canvas) {
		for (int i = 1; i < mPointDown.length; i = i + 2) {
			mPointDown[i] = mRect.height() - mPointUp[i] - move;
		}
		canvas.drawLines(mPointDown, mPaint);
	}

	public void drawUpRect(Canvas canvas) {
		for (int i = 1; i < mPointUp.length; i = i + 2) {
			mPointUp[i] = mPointUp[i] + move;
		}
		canvas.drawLines(mPointUp, mPaint);
	}

	public void initPoints() {
		if (mBytes == null) {
			mPointUp = new float[] { 0, mRect.height() / 3, mRect.width(),
					mRect.height() / 3 };
			mPointDown = new float[] { 0, mRect.height() / 3 * 2,
					mRect.width(), mRect.height() / 3 * 2 };
			return;
		}
		if (mPointUp == null || mPointUp.length < mBytes.length * 4) {
			mPointUp = new float[mBytes.length * 4];
			mPointDown = new float[mBytes.length * 4];
		}
		for (int i = 0; i < mBytes.length - 1; i++) {
			mPointDown[i * 4] = mPointUp[i * 4] = mRect.width() * i
					/ (mBytes.length - 1);
			mPointUp[i * 4 + 1] = mRect.height() / 3
					+ ((byte) (mBytes[i] + 128)) * (mRect.height() / 3) / 512;
			mPointDown[i * 4 + 2] = mPointUp[i * 4 + 2] = mRect.width()
					* (i + 1) / (mBytes.length - 1);
			mPointUp[i * 4 + 3] = mRect.height() / 3
					+ ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 3)
					/ 512;
		}
	}

	private void initOuterPoints() {
		float r = (float) (LENGTH * Math.sin(0.1 * Math.PI) / Math
				.sin(126.0 / 180 * Math.PI));
		for (int i = 0; i < 5; i++) {
			mOuterPoints[2 * i] = (float) (LENGTH * Math.cos(Math
					.toRadians(i * 72))) + mRect.width() / 2;
			mOuterPoints[2 * i + 1] = (float) (LENGTH * Math.sin(Math
					.toRadians(i * 72))) + mRect.height() / 2;
			mInnerPoints[2 * i] = (float) (r * Math.cos(Math
					.toRadians(i * 72 + 36))) + mRect.width() / 2;
			mInnerPoints[2 * i + 1] = (float) (r * Math.sin(Math
					.toRadians(i * 72 + 36))) + mRect.height() / 2;
		}
		flagStar = false;
	}

	private void drawStar(Canvas canvas, int a) {

		mPaint.setARGB(a, 0, 128, 255);
		mPaint.setStrokeWidth(3f);
		canvas.scale(0.5f, 0.5f, mRect.width() / 2, mRect.height() / 2);
		canvas.drawLine(mOuterPoints[0], mOuterPoints[1], mInnerPoints[0],
				mInnerPoints[1], mPaint);
		canvas.drawLine(mInnerPoints[0], mInnerPoints[1], mOuterPoints[2],
				mOuterPoints[3], mPaint);

		canvas.drawLine(mOuterPoints[2], mOuterPoints[3], mInnerPoints[2],
				mInnerPoints[3], mPaint);
		canvas.drawLine(mInnerPoints[2], mInnerPoints[3], mOuterPoints[4],
				mOuterPoints[5], mPaint);

		canvas.drawLine(mOuterPoints[4], mOuterPoints[5], mInnerPoints[4],
				mInnerPoints[5], mPaint);
		canvas.drawLine(mInnerPoints[4], mInnerPoints[5], mOuterPoints[6],
				mOuterPoints[7], mPaint);

		canvas.drawLine(mOuterPoints[6], mOuterPoints[7], mInnerPoints[6],
				mInnerPoints[7], mPaint);
		canvas.drawLine(mInnerPoints[6], mInnerPoints[7], mOuterPoints[8],
				mOuterPoints[9], mPaint);

		canvas.drawLine(mOuterPoints[8], mOuterPoints[9], mInnerPoints[8],
				mInnerPoints[9], mPaint);
		canvas.drawLine(mInnerPoints[8], mInnerPoints[9], mOuterPoints[0],
				mOuterPoints[1], mPaint);
	}

	private void initList() {
		localList = new ArrayList<float[]>();
		sizeList = new ArrayList<Integer>();
		for (int i = 0; i < MAX_COUNT; i++) {
			float[] local = new float[] { (float) Math.random() * getWidth(),
					(float) Math.random() * getHeight() };
			int size = (int) (Math.random() * 20);
			localList.add(local);
			sizeList.add(size);
		}
	}

	private void del(int i) {
		localList.remove(i);
		sizeList.remove(i);
		float[] local = new float[] { (float) Math.random() * getWidth(),
				(float) Math.random() * getHeight() };
		int size = (int) (Math.random() * 50);
		localList.add(local);
		sizeList.add(size);
	}
	
	public void drawStyleTwo(Canvas canvas) {
		if (mBytes == null) {
			return;
		}
		initmPoints(2);
		mNorPoints = new float[mPoints.length];
		for (int i = 0; i < mBytes.length - 1; i = i + 5) {
			mPoints[i * 4] = (mRect.width() / (mBytes.length - 1) + SPAN) * i;
			float temp = ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128
					+ mRect.height() / 2;
			if (temp > mRect.height() / 2) {
				mPoints[i * 4 + 1] = mRect.height() - temp;
			} else {
				mPoints[i * 4 + 1] = temp;
			}
			mPoints[i * 4 + 2] = (mRect.width() / (mBytes.length - 1) + SPAN)
					* i;
			mPoints[i * 4 + 3] = mRect.height() / 2;
		}
		for (int i = 0; i < mPoints.length / 4; i = i + 1) {
			canvas.drawLine(mPoints[4 * i], mPoints[4 * i + 1],
					mPoints[4 * i + 2], mPoints[4 * i + 3], mPaint);
		}
	}

	public void drawSytleThree(Canvas canvas) {
		if (mBytes == null) {
			return;
		}
		initmPoints(2);
		for (int i = 0; i < mBytes.length - 1; i++) {
			mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
			float temp = (mRect.height() / 2 + ((byte) (mBytes[i] + 128))
					* (mRect.height() / 2) / 128);
			if (temp > mRect.height() / 2) {
				mPoints[i * 4 + 1] = mRect.height() - temp;
			} else {
				mPoints[i * 4 + 1] = temp;
			}
			mPoints[i * 4 + 2] = mRect.width() * (i) / (mBytes.length - 1);
			mPoints[i * 4 + 3] = mRect.height() / 2;
		}
		canvas.drawLines(mPoints, mPaint);
	}

	public void initmPoints(int span) {
		if (mPoints == null || mPoints.length < mBytes.length * 4 * span) {
			mPoints = new float[mBytes.length * 4 * span];
		}
	}
	class MyPaint extends Paint implements Serializable{}
	
}
