package edu.stanford.cs108.bunnyworld;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page {

    private Game game;
    private String name;
    private static int count = 0;
    private List<Shape> shapes;
    private String backgroundImage;

    public Page(Game game) {
        this.game = game;
        count ++;
        name = "Page" + count;
        generateDistinctName();
        shapes = new ArrayList<>();
        backgroundImage = "(None)";
    }

    public Page(String name, Game game) {
        this.name = name;
        this.game = game;
        shapes = new ArrayList<>();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Page.count = count;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public static class Possession {

        private static List<Shape> shapes = new ArrayList<>();

        private static Map<Shape, Float> ratios = new HashMap<>();

        public static List<Shape> getShapes() {
            return shapes;
        }

        public static float width, height;

        public static void setDimension(float width, float height) {
            Possession.width = width;
            Possession.height = height;
        }

        public static float calculateRatio(Shape shape) {
            float wRatio = width / Game.POSSESSION_CAPACITY / shape.getWidth();
            float hRatio = height / 4 / shape.getHeight();
            return Math.min(wRatio, hRatio);
        }

        public static void drawPossession(Canvas canvas) {
            for (Shape shape : shapes) {
                shape.drawShape(canvas);
            }
        }

        public static void addToPossession(Shape shape, Page fromPage) {
            if (shape.getPage() == null) return;
            if (!fromPage.getShapes().contains(shape)) return;
            shape.setPage(null);
            fromPage.shapes.remove(shape);
            Possession.shapes.add(shape);
            Possession.ratios.put(shape, calculateRatio(shape));
            shape.reshape(shape.getX(), shape.getY(),
                    shape.getWidth() * ratios.get(shape),
                    shape.getHeight() * ratios.get(shape));
            for (int i = 0; i < shapes.size(); i ++) {
                Shape s = shapes.get(i);
                s.reshape(i * width / Game.POSSESSION_CAPACITY, height * 0.75f,
                        s.getWidth(), s.getHeight());
            }
        }

        public static void removeFromPossession(Shape shape, Page toPage) {
            if (shape.getPage() != null) return;
            if (!Possession.shapes.contains(shape)) return;
            shape.setPage(toPage);
            toPage.shapes.add(shape);
            shape.reshape(shape.getX(), shape.getY(),
                    shape.getWidth() / ratios.get(shape),
                    shape.getHeight() / ratios.get(shape));
            Possession.shapes.remove(shape);
            Possession.ratios.remove(shape);
            for (int i = 0; i < shapes.size(); i ++) {
                Shape s = shapes.get(i);
                s.reshape(i * width / Game.POSSESSION_CAPACITY, height * 0.75f,
                        s.getWidth(), s.getHeight());
            }
        }

        public static void clear() {
            shapes.clear();
        }

    }

    public boolean isDistinct() {
        for (Page page : game.getPages()) {
            if (this == page) continue;
            if (name.toLowerCase().equals(page.getName().toLowerCase())) return false;
        }
        return true;
    }

    public void generateDistinctName() {
        while (!isDistinct()) {
            count ++;
            name = "Page" + count;
        }
    }

    public void drawPage(Canvas canvas) {
        for (Shape shape : shapes) {
            shape.drawShape(canvas);
        }
    }

    public Shape findSelected(float x, float y) {
        for (int i = shapes.size() - 1; i >= 0; i --) {
            Shape shape = shapes.get(i);
            if (game.isOn() && !shape.getVisible()) continue;
            if (shape.contains(x, y)) {
                shapes.remove(shape);
                shapes.add(shape);
                return shape;
            }
        }
        for (int i = Possession.shapes.size() - 1; i >= 0; i --) {
            Shape shape = Possession.shapes.get(i);
            if (game.isOn() && !shape.getVisible()) continue;
            if (shape.contains(x, y)) {
                Possession.shapes.remove(shape);
                Possession.shapes.add(shape);
                return shape;
            }
        }
        return null;
    }

}
