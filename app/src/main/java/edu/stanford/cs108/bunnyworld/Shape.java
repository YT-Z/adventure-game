package edu.stanford.cs108.bunnyworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Shape {

    private Game game;
    private String name;
    private static int count;
    private Page page;
    private RectF boundary;
    private String image;
    private String text;
    private List<String> scripts;
    private List<List<String>> splitScripts;
    private boolean isVisible;
    private boolean isMovable;
    private boolean isReactive;
    private RichText richText;

    public static final Map<String, Float> SIZE_MAP = new LinkedHashMap<String, Float>() {{
        put("Tiny", 60.0f);
        put("Small", 100.0f);
        put("Normal", 110.0f);
        put("Large", 120.0f);
        put("Huge", 200.0f);
    }};

    public static final Map<String, Integer> COLOR_MAP = new LinkedHashMap<String, Integer>() {{
        put("Black", Color.BLACK);
        put("Blue", Color.BLUE);
        put("Cyan", Color.CYAN);
        put("DKGray", Color.DKGRAY);
        put("Gray", Color.GRAY);
        put("Green", Color.GREEN);
        put("LTGray", Color.LTGRAY);
        put("Magenta", Color.MAGENTA);
        put("Red", Color.RED);
        put("Transparent", Color.TRANSPARENT);
        put("White", Color.WHITE);
        put("Yellow", Color.YELLOW);
    }};

    public Shape(Game game) {
        this.game = game;
        name = "Shape" + (++ count);
        generateDistinctName();
        page = null;
        boundary = new RectF(0.0f, 0.0f, 200.0f, 200.0f);
        image = "(None)";
        text = "";
        scripts = new ArrayList<>();
        splitScripts = new ArrayList<>();
        isVisible = true;
        isMovable = true;
        isReactive = false;
        richText = new RichText(false, false, "Normal", "Black");
    }

    public Shape(String name, Game game) {
        this.name = name;
        this.game = game;
        scripts = new ArrayList<>();
        splitScripts = new ArrayList<>();
        isReactive = false;
    }

    public class RichText {

        private boolean isBold;
        private boolean isItalic;
        private String size;
        private String color;
        private Paint paint;

        public RichText(boolean isBold, boolean isItalic, String size, String color) {
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.size = size;
            this.color = color;
            paint = new Paint();
            if(isBold && isItalic) {
                paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            } else if (isBold) {
                paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else if (isItalic) {
                paint.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
            }
            paint.setTextSize(SIZE_MAP.get(size));
            paint.setColor(COLOR_MAP.get(color));
        }

        public boolean getBold() {
            return isBold;
        }

        public void setBold(boolean bold) {
            isBold = bold;
        }

        public boolean getItalic() {
            return isItalic;
        }

        public void setItalic(boolean italic) {
            isItalic = italic;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Paint getPaint() {
            return paint;
        }

        public void setPaint(Paint paint) {
            this.paint = paint;
        }

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
        Shape.count = count;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RectF getBoundary() {
        return boundary;
    }

    public void setBoundary(RectF boundary) {
        this.boundary = boundary;
    }

    public float getX() {
        return boundary.left;
    }

    public void setX(float x) {
        float width = boundary.width();
        boundary.left = x;
        boundary.right = x + width;
    }

    public float getY() {
        return boundary.top;
    }

    public void setY(float y) {
        float height = boundary.height();
        boundary.top = y;
        boundary.bottom = y + height;
    }

    public void displace(float x, float y) {
        if (game.isOn() && !isMovable) return;
        setX(x);
        setY(y);
    }

    public float getWidth() {
        return boundary.width();
    }

    public void setWidth(float width) {
        boundary.right = boundary.left + width;
    }

    public float getHeight() {
        return boundary.height();
    }

    public void setHeight(float height) {
        boundary.bottom = boundary.top + height;
    }

    public boolean getVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean getMovable() {
        return isMovable;
    }

    public void setMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    public boolean getReactive() {
        return isReactive;
    }

    public void setReactive(boolean isReactive) {
        this.isReactive = isReactive;
    }

    public List<String> getScripts() {
        return scripts;
    }

    public List<List<String>> getSplitScripts() {
        return splitScripts;
    }

    public RichText getRichText() {
        return richText;
    }

    public void setRichText(RichText richText) {
        this.richText = richText;
    }

    public boolean contains(float x, float y) {
        return boundary.contains(x, y);
    }

    public Shape clone() {
        Shape shape = new Shape("Shape" + (++ count), game);
        shape.generateDistinctName();
        shape.page = page;
        shape.boundary = new RectF(boundary.left, boundary.top, boundary.right, boundary.bottom);
        shape.image = image;
        shape.text = text;
        shape.scripts.addAll(scripts);
        for (List<String> splitScript : splitScripts) {
            shape.splitScripts.add(new ArrayList<String>(splitScript));
        }
        shape.isVisible = isVisible;
        shape.isMovable = isMovable;
        shape.richText = new RichText(
                richText.isBold, richText.isItalic, richText.size, richText.color);
        return shape;
    }

    public void drawShape(Canvas canvas) {
        if (game.isOn() && !isVisible) return;

        if (!text.equals("")) {
            canvas.drawText(text, boundary.left, boundary.bottom, richText.paint);
        } else if (!image.equals("(None)")) {
            canvas.drawBitmap(SingletonData.getInstance().images.get(image).getBitmap(),
                    null, boundary, null);
        } else {
            canvas.drawRect(boundary, Game.fillPaint);
        }

        if (this == game.getSelectedShape()) {
            canvas.drawRect(boundary, Game.onSelectStrokePaint);
        }

        if (isReactive) {
            canvas.drawRect(boundary, Game.onReactStrokePaint);
        }
    }

    public void reshape(float x, float y, float w, float h) {
        boundary.set(x, y, x + w, y + h);
    }

    public boolean isDistinct() {
        for (Shape shape : game.getShapes()) {
            if (this == shape) continue;
            if (name.toLowerCase().equals(shape.getName().toLowerCase())) return false;
        }
        return true;
    }

    public void generateDistinctName() {
        while (!isDistinct()) {
            name = "Shape" + (++ count);
        }
    }

    protected abstract class Behavior {

        protected String trigger;
        protected boolean isExecutable;
        protected List<Pair<String, Object>> actions;

        protected Behavior(String trigger) {
            this.trigger = trigger;
            isExecutable = true;
        }

        public List<Pair<String, Object>> getActions() {
            return actions;
        }

        public void setActions(List<Pair<String, Object>> actions) {
            this.actions = actions;
        }

        public void execute() {
            if (!isVisible) return;
            if (!isExecutable) return;
            for (Pair<String, Object> action : actions) {
                switch (action.first) {
                    case Game.GOTO:
                        Page page = (Page) action.second;
                        game.switchToPage(page);
                        break;
                    case Game.PLAY:
                        MediaPlayer mp = (MediaPlayer) action.second;
                        if (mp != null) mp.start();
                        break;
                    case Game.HIDE:
                        Shape toHide = (Shape) action.second;
                        if (toHide != null) toHide.setVisible(false);
                        break;
                    case Game.SHOW:
                        Shape toShow = (Shape) action.second;
                        if (toShow != null) toShow.setVisible(true);
                        break;
                }
            }
        }

        public void enable() {
            isExecutable = true;
        }

        public void disable() {
            isExecutable = false;
        }

    }

    public class OnClickBehavior extends Behavior {

        public OnClickBehavior() {
            super(Game.ONCLICK);
            actions = new ArrayList<>();
        }

        @Override
        public void execute() {
            if (Shape.this != game.getSelectedShape()) return;
            if (page == null) return;
            if (!page.getShapes().contains(Shape.this)) return;
            if (Page.Possession.getShapes().contains(Shape.this)) return;
            super.execute();
        }

    }

    public class OnEnterBehavior extends Behavior {

        public OnEnterBehavior() {
            super(Game.ONENTER);
            actions = new ArrayList<>();
        }

        @Override
        public void execute() {
            super.execute();
            disable();
        }

    }

    public class OnDropBehavior extends Behavior {

        private Map<Shape, List<Pair<String, Object>>> actionsMap;

        public OnDropBehavior() {
            super(Game.ONDROP);
            actionsMap = new HashMap<>();
        }

        public void reactsTo(Shape shape) {
            if (actionsMap.containsKey(shape)) {
                isReactive = true;
                actions = actionsMap.get(shape);
            }
        }

        public void unreacts() {
            isReactive = false;
            actions = null;
        }

        @Override
        public void execute() {
            if (!isReactive) return;
            super.execute();
        }

    }

    private OnClickBehavior onClickBehavior;
    private OnEnterBehavior onEnterBehavior;
    private OnDropBehavior onDropBehavior;

    public OnClickBehavior getOnClickBehavior() {
        return onClickBehavior;
    }

    public OnEnterBehavior getOnEnterBehavior() {
        return onEnterBehavior;
    }

    public OnDropBehavior getOnDropBehavior() {
        return onDropBehavior;
    }

    public void parseScripts() {
        Behavior cursor = null;
        onClickBehavior = new OnClickBehavior();
        onEnterBehavior = new OnEnterBehavior();
        onDropBehavior = new OnDropBehavior();
        for (List<String> splitScript : splitScripts) {
            int i = 0;
            String trigger = splitScript.get(i);
            switch (trigger) {
                case Game.ONCLICK:
                    if (onClickBehavior.actions.isEmpty()) cursor = onClickBehavior;
                    i ++;
                    break;
                case Game.ONENTER:
                    cursor = onEnterBehavior;
                    i ++;
                    break;
                case Game.ONDROP:
                    cursor = onDropBehavior;
                    Shape object = game.getShape(splitScript.get(++ i));
                    if (!onDropBehavior.actionsMap.containsKey(object))
                        onDropBehavior.actionsMap.put(object, new ArrayList<Pair<String, Object>>());
                    onDropBehavior.actions = onDropBehavior.actionsMap.get(object);
                    break;
            }
            i ++;
            if (cursor == null) continue;
            List<Pair<String, Object>> actions = cursor.actions;
            while (i < splitScript.size()) {
                String action = splitScript.get(i);
                switch (action) {
                    case Game.GOTO:
                        actions.add(new Pair<String, Object>(action,
                                game.getPage(splitScript.get(++ i))));
                        break;
                    case Game.PLAY:
                        actions.add(new Pair<String, Object>(action,
                                SingletonData.getInstance().sounds.get(splitScript.get(++ i))));
                        break;
                    case Game.HIDE:
                    case Game.SHOW:
                        actions.add(new Pair<String, Object>(action,
                                game.getShape(splitScript.get(++ i))));
                        break;
                }
                i ++;
            }
            cursor = null;
        }
    }

}
