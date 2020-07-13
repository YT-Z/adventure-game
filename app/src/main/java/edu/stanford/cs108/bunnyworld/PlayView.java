package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PlayView extends GameView {

    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        pageHeight = (float) viewHeight * 0.75f;

        blackOutlinePaint = new Paint();
        blackOutlinePaint.setColor(Color.BLACK);
        blackOutlinePaint.setStyle(Paint.Style.STROKE);
        blackOutlinePaint.setStrokeWidth(10.0f);
    }

    private static Paint blackOutlinePaint;

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0.0f, pageHeight, viewWidth, pageHeight, blackOutlinePaint);
        for (Shape shape : Page.Possession.getShapes()) {
            shape.drawShape(canvas);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void drawImportedImage(Canvas canvas) {
        if (ImportImage.getSelectedImage() != null) {
            ImportImage.getSelectedImage().setBounds(0, 0, viewWidth, (int) pageHeight);
            ImportImage.getSelectedImage().draw(canvas);
        }
    }

    @Override
    protected void drawBackground(Canvas canvas) {
        BitmapDrawable background = SingletonData.getInstance().images.get(SingletonData.getInstance().game.getCurrentPage().getBackgroundImage());
        if (background != null) canvas.drawBitmap(background.getBitmap(),
                null, new RectF(0.0f, 0.0f, viewWidth, pageHeight), null);
    }

    private float pageHeight;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        pageHeight = (float) viewHeight * 0.75f;
        Page.Possession.setDimension(w, h);
    }

    private float deltaX, deltaY;
    private float savedX, savedY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Game game = SingletonData.getInstance().game;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                selectedShape = game.getCurrentPage().findSelected(event.getX(), event.getY());
                game.setSelectedShape(selectedShape);
                if (selectedShape != null) {
                    savedX = selectedShape.getX();
                    savedY = selectedShape.getY();
                    deltaX = event.getX() - savedX;
                    deltaY = event.getY() - savedY;
                    if (selectedShape.getPage() != null) selectedShape.getOnClickBehavior().execute();
                    for (Shape shape : SingletonData.getInstance().game.getCurrentPage().getShapes()) {
                        shape.getOnDropBehavior().reactsTo(selectedShape);
                    }
                    for (Shape shape : Page.Possession.getShapes()) {
                        shape.getOnDropBehavior().reactsTo(selectedShape);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectedShape != null) {
                    selectedShape.displace(event.getX() - deltaX, event.getY() - deltaY);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (selectedShape != null) {
                    boolean overlapped = false;
                    boolean toSnapBack = false;
                    for (int i = game.getCurrentPage().getShapes().size() - 1; i >= 0; i --) {
                        Shape receivingShape = game.getCurrentPage().getShapes().get(i);
                        if (receivingShape == selectedShape) continue;
                        if (!receivingShape.getVisible()) continue;
                        if (RectF.intersects(receivingShape.getBoundary(), selectedShape.getBoundary())) {
                            if (receivingShape.getReactive()) {
                                receivingShape.getOnDropBehavior().execute();
                                overlapped = true;
                                toSnapBack = false;
                                break;
                            } else {
                                overlapped = true;
                                toSnapBack = true;
                            }
                        }
                    }

                    if (overlapped) {
                        if (toSnapBack) {
                            selectedShape.displace(savedX, savedY);
                        } else {
                            Page.Possession.removeFromPossession(selectedShape, game.getCurrentPage());
                            float distance = pageHeight - (event.getY() - deltaY);
                            float height = selectedShape.getHeight();
                            if (distance < height) selectedShape.displace(event.getX() - deltaX, pageHeight - height);
                        }
                    } else {
                        float distance = pageHeight - (event.getY() - deltaY);
                        float height = selectedShape.getHeight();
                        if (distance >= 0.5f * height && distance < height) {
                            selectedShape.displace(event.getX() - deltaX, pageHeight - height);
                        } else if (distance > 0 && distance < 0.5f * height) {
                            selectedShape.displace(event.getX() - deltaX, pageHeight);
                        }
                        if (selectedShape.getY() >= pageHeight) {
                            if (selectedShape.getPage() != null) {
                                Page.Possession.addToPossession(selectedShape, game.getCurrentPage());
                            } else {
                                Page.Possession.removeFromPossession(selectedShape, game.getCurrentPage());
                                Page.Possession.addToPossession(selectedShape, game.getCurrentPage());
                            }
                        } else {
                            Page.Possession.removeFromPossession(selectedShape, game.getCurrentPage());
                        }
                    }

                    for (Shape shape : game.getCurrentPage().getShapes()) {
                        shape.getOnDropBehavior().unreacts();
                    }
                    for (Shape shape : Page.Possession.getShapes()) {
                        shape.getOnDropBehavior().unreacts();
                    }

                }
                invalidate();
                break;
        }
        return true;
    }

}
