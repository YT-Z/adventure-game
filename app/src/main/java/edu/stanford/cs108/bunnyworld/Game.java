package edu.stanford.cs108.bunnyworld;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private String name;
    private Page currentPage;
    private Shape selectedShape;
    private List<Page> pages;
    private List<Shape> shapes;
    private List<String> errorMessages;
    private boolean gameOn;

    public static Paint textPaint;
    public static Paint fillPaint;
    public static Paint onSelectStrokePaint;
    public static Paint onReactStrokePaint;

    public final static String ONCLICK = "on click";
    public final static String ONDROP = "on drop";
    public final static String ONENTER = "on enter";
    public final static String PLAY = "play";
    public final static String SHOW = "show";
    public final static String GOTO = "goto";
    public final static String HIDE = "hide";

    public final static int POSSESSION_CAPACITY = 8;

    public Game() {
        pages = new ArrayList<>();
        shapes = new ArrayList<>();
        currentPage = null;
        selectedShape = null;
        initPaints();
        gameOn = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Page currentPage) {
        this.currentPage = currentPage;
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }

    public List<Page> getPages() {
        return pages;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public boolean isOn() {
        return gameOn;
    }

    public void startGame() {
        gameOn = true;
    }

    public void endGame() {
        gameOn = false;
    }

    public void switchToPage(Page newPage) {
        if (currentPage != null && currentPage != newPage) {
            for (Shape shape : currentPage.getShapes()) {
                shape.getOnEnterBehavior().enable();
            }
        }
        currentPage = newPage;
        for (Shape shape : newPage.getShapes()) {
            shape.getOnEnterBehavior().execute();
        }
        setSelectedShape(null);
    }

    private void initPaints() {
        textPaint = new Paint();
        textPaint.setTextSize(100);

        fillPaint = new Paint();
        fillPaint.setColor(Color.GRAY);
        fillPaint.setStyle(Paint.Style.FILL);

        onSelectStrokePaint = new Paint();
        onSelectStrokePaint.setColor(Color.RED);
        onSelectStrokePaint.setStyle(Paint.Style.STROKE);
        onSelectStrokePaint.setStrokeWidth(5.0f);

        onReactStrokePaint = new Paint();
        onReactStrokePaint.setColor(Color.GREEN);
        onReactStrokePaint.setStyle(Paint.Style.STROKE);
        onReactStrokePaint.setStrokeWidth(5.0f);
    }

    public boolean checkError() {
        errorMessages = new ArrayList<>();
        for (Shape shape : shapes) {
            List<List<String>> splitScripts = shape.getSplitScripts();
            for (List<String> script : splitScripts) {
                int i = 0;
                String trigger = script.get(i);
                if (trigger.equals(ONDROP)) {
                    String shapeName = script.get(++ i);
                    if (getShape(shapeName) == null) {
                        errorMessages.add(shape.getName() + ": " + shapeName + " not found");
                    }
                } else {
                    i ++;
                }
                i ++;
                while (i < script.size()) {
                    String action = script.get(i);
                    switch (action) {
                        case GOTO:
                            String pageName = script.get(++ i);
                            if (getPage(pageName) == null) {
                                errorMessages.add(shape.getName() + ": " + pageName + " not found");
                            }
                            break;
                        case PLAY:
                            String soundName = script.get(++ i);
                            if (!SingletonData.getInstance().sounds.keySet().contains(soundName)) {
                                errorMessages.add(shape.getName() + ": " + soundName + " not found");
                            }
                            break;
                        case HIDE:
                        case SHOW:
                            String shapeName = script.get(++ i);
                            if (getShape(shapeName) == null) {
                                errorMessages.add(shape.getName() + ": " + shapeName + " not found");
                            }
                            break;
                        default:
                            i ++;
                    }
                    i ++;
                }
            }
        }
        return errorMessages.isEmpty();
    }

    public Shape getShape(String shapeName) {
        for (Shape shape : shapes) {
            if (shape.getName().equals(shapeName)) return shape;
        }
        return null;
    }

    public Page getPage(String pageName) {
        for (Page page : pages) {
            if (page.getName().equals(pageName)) return page;
        }
        return null;
    }

}
