package com.maple.cloudweather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.maple.cloudweather.domain.Weather;

/**
 * 空气质量的弧形“表” 10行 120dp
 * 
 * @author Mixiaoxiao
 * 
 */
public class AqiView extends View {
	private final float density;
	// private float lineSize;//每一行高度
	private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
	private RectF rectF = new RectF();
//	private City aqiCity;
	private Weather mWeather;

	public AqiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		density = context.getResources().getDisplayMetrics().density;
		textPaint.setTextAlign(Align.CENTER);
		if(isInEditMode()){
			return;
		}
//		textPaint.setTypeface(MainActivity.getTypeface(context));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);

		final float w = getWidth();
		final float h = getHeight();
		if (w <= 0 || h <= 0) {
			return;
		}
		final float lineSize = h / 10f;// 大约是12dp
		if (mWeather == null) {
			textPaint.setStyle(Style.FILL);
			textPaint.setTextSize(lineSize * 1.25f);
			textPaint.setColor(0xaaffffff);
			canvas.drawText("暂无数据", w / 2f, h / 2f, textPaint);
			return;
		}
		float currAqiPercent = -1f;
		try {
			currAqiPercent = Float.valueOf(mWeather.aqi.city.aqi) / 500f;// 污染%
			currAqiPercent = Math.min(currAqiPercent, 1f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// canvas.drawColor(0x33ffffff);

		float aqiArcRadius = lineSize * 4f;
		textPaint.setStyle(Style.STROKE);
		textPaint.setStrokeWidth(lineSize * 1);
		textPaint.setColor(0x55ffffff);
		rectF.set(-aqiArcRadius, -aqiArcRadius, aqiArcRadius, aqiArcRadius);
		final int saveCount = canvas.save();
		canvas.translate(w / 2f, h / 2f);
		// draw aqi restPercent arc
		final float startAngle = -210f;
		final float sweepAngle = 240f;
		canvas.drawArc(rectF, startAngle + sweepAngle * currAqiPercent, sweepAngle * (1f - currAqiPercent), false,
				textPaint);
		if (currAqiPercent >= 0f) {
			// draw aqi aqiPercent arc
			textPaint.setColor(0x99ffffff);
			canvas.drawArc(rectF, startAngle, sweepAngle * currAqiPercent, false, textPaint);
			// draw aqi arc center circle
			textPaint.setColor(0xffffffff);
			textPaint.setStrokeWidth(lineSize / 8f);
			canvas.drawCircle(0, 0, lineSize / 3f, textPaint);
			// draw aqi number and text
			textPaint.setStyle(Style.FILL);
			textPaint.setTextSize(lineSize * 1.5f);
			textPaint.setColor(0xffffffff);
			try {
				canvas.drawText(mWeather.aqi.city.aqi + "", 0, lineSize * 3, textPaint);
			} catch (Exception e) {
			}
			textPaint.setTextSize(lineSize * 1f);
			textPaint.setColor(0x88ffffff);
			try {
				canvas.drawText(mWeather.aqi.city.qlty + "", 0, lineSize * 4.25f, textPaint);
			} catch (Exception e) {
			}

			// draw the aqi line
			canvas.rotate(startAngle + sweepAngle * currAqiPercent - 180f);
			textPaint.setStyle(Style.STROKE);
			textPaint.setColor(0xffffffff);
			float startX = lineSize / 3f;
			canvas.drawLine(-startX, 0, -lineSize * 4.5f, 0, textPaint);
		}
		canvas.restoreToCount(saveCount);
	}

	public void setData(Weather weather) {
		if (weather != null) {
			mWeather = weather;
			invalidate();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// this.lineSize = h / 10f;
	}

}