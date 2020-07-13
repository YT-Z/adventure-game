package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImportedImage(canvas);
        drawBackground(canvas);
        Game game = SingletonData.getInstance().game;
        game.getCurrentPage().drawPage(canvas);
        if (game.getSelectedShape() != null) game.getSelectedShape().drawShape(canvas);
    }

    protected void drawImportedImage(Canvas canvas) {
        if (ImportImage.getSelectedImage() != null) {
            ImportImage.getSelectedImage().setBounds(0, 0, viewWidth, viewHeight);
            ImportImage.getSelectedImage().draw(canvas);
        }
    }

    protected void drawBackground(Canvas canvas) {
        BitmapDrawable background = SingletonData.getInstance().images.get(
                SingletonData.getInstance().game.getCurrentPage().getBackgroundImage());
        if (background != null) canvas.drawBitmap(background.getBitmap(),
                null, new RectF(0.0f, 0.0f, viewWidth, viewHeight), null);
    }

    protected Shape selectedShape;
    protected int viewWidth, viewHeight;

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    private float deltaX, deltaY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Game game = SingletonData.getInstance().game;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectedShape = game.getCurrentPage().findSelected(event.getX(), event.getY());
                game.setSelectedShape(selectedShape);
                if (selectedShape != null){
                    deltaX = event.getX() - selectedShape.getX();
                    deltaY = event.getY() - selectedShape.getY();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (selectedShape != null) {
                    selectedShape.displace(event.getX() - deltaX, event.getY() - deltaY);
                }
                invalidate();
                break;
        }
        return true;
    }

}
