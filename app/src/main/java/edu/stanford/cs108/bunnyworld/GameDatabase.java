package edu.stanford.cs108.bunnyworld;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public abstract class GameDatabase {

    public static SQLiteDatabase initDatabase(Context context) {
        SQLiteDatabase db = context.openOrCreateDatabase("GamesDB", MODE_PRIVATE, null);
        Cursor tablesCursor = db.rawQuery(
                "SELECT * FROM sqlite_master WHERE type = 'table' AND name = 'games';",
                null);
        if (tablesCursor.getCount() == 0) {
            String setupStr = "CREATE TABLE games ("
                    + "name TEXT, "
                    + "page_class_count INTEGER, "
                    + "shape_class_count INTEGER, "
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ");";
            db.execSQL(setupStr);
        }
        tablesCursor.close();
        createExample(db);
        return db;
    }

    private static String getGameCode(String gameName, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT _id FROM games WHERE name = '" + gameName + "';", null);
        String gameCode = null;
        if (cursor.moveToNext()) {
            gameCode = "Game" + cursor.getInt(0);
        }
        cursor.close();
        return gameCode;
    }

    public static List<String> getGameList(SQLiteDatabase db) {
        List<String> games = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name From games;", null);
        while(cursor.moveToNext()) games.add(cursor.getString(0));
        cursor.close();
        return games;
    }

    public static void saveGame(Game game, SQLiteDatabase db, boolean committed) {
        String gameName = game.getName();
        if (!committed) gameName += "_tmp";
        deleteGame(gameName, db);

        String insertGameStr = "INSERT INTO games VALUES ("
                + "'" + gameName + "',"
                + Page.getCount() + ","
                + Shape.getCount() + ","
                + "NULL);";
        db.execSQL(insertGameStr);

        String gameCode = getGameCode(gameName, db);

        String setupPagesStr = "CREATE TABLE " + gameCode + "_pages" + " ("
                + "name TEXT, "
                + "background_image TEXT, "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";
        db.execSQL(setupPagesStr);

        savePages(gameCode, game.getPages(), db);

        String setupShapesStr = "CREATE TABLE " + gameCode + "_shapes" + " ("
                + "name TEXT, " // 0
                + "page_name TEXT, " // 1
                + "boundary_left REAL, boundary_top REAL, boundary_right REAL, boundary_bottom REAL, " // 2 3 4 5
                + "image TEXT, text TEXT, " // 6 7
                + "scripts TEXT, " // 8
                + "visible INTEGER, movable INTEGER, " // 9 10
                + "richtext_bold INTEGER, richtext_italic INTEGER, " // 11 12
                + "richtext_size TEXT, richtext_color TEXT, " // 13 14
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT" // 15
                + ");";
        db.execSQL(setupShapesStr);

        saveShapes(gameCode, game.getShapes(), db);
    }

    private static void savePages(String gameCode, List<Page> pages, SQLiteDatabase db) {
        for (Page page : pages) {
            String insertPageStr = "INSERT INTO " + gameCode + "_pages" + " VALUES "
                    + "('" + page.getName() + "','" + page.getBackgroundImage() + "',NULL);";
            db.execSQL(insertPageStr);
        }
    }

    private static void saveShapes(String gameCode, List<Shape> shapes, SQLiteDatabase db) {
        for (Shape shape : shapes) {
            String visible = shape.getVisible() ? "1" : "0";
            String movable = shape.getMovable() ? "1" : "0";
            String scripts = "NONE";
            if (!shape.getScripts().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String script : shape.getScripts()) {
                    stringBuilder.append(script).append("/");
                }
                if (stringBuilder.charAt(stringBuilder.length() - 1) == '/') stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                scripts = stringBuilder.toString();
            }
            String bold = shape.getRichText().getBold() ? "1" : "0";
            String italic = shape.getRichText().getItalic() ? "1" : "0";
            String insertShapeStr = "INSERT INTO " + gameCode + "_shapes" + " VALUES ("
                    + "'" + shape.getName() + "',"
                    + "'" + shape.getPage().getName() + "',"
                    + shape.getBoundary().left + ","
                    + shape.getBoundary().top + ","
                    + shape.getBoundary().right + ","
                    + shape.getBoundary().bottom + ","
                    + "'" + shape.getImage() + "',"
                    + "'" + shape.getText() + "',"
                    + "'" + scripts + "',"
                    + visible + "," + movable + ","
                    + bold + "," + italic + ","
                    + "'" + shape.getRichText().getSize() + "',"
                    + "'" + shape.getRichText().getColor() + "',"
                    + "NULL);";
            db.execSQL(insertShapeStr);
        }
    }

    public static void deleteGame(String gameName, SQLiteDatabase db) {
        String gameCode = getGameCode(gameName, db);
        db.execSQL("DELETE FROM games WHERE name = '" + gameName + "';");
        db.execSQL("DROP TABLE IF EXISTS " + gameCode + "_pages" + ";");
        db.execSQL("DROP TABLE IF EXISTS " + gameCode + "_shapes" + ";");
    }

    public static Game loadGame(String gameName, SQLiteDatabase db) {
        Game game = null;
        Cursor cursor = db.rawQuery("SELECT * FROM games WHERE name = '" + gameName + "'", null);
        if (cursor.moveToNext()) {
            game = new Game();
            game.setName(cursor.getString(0));
            loadPages(game, db);
            loadShapes(game, db);
            Page.setCount(cursor.getInt(1));
            Shape.setCount(cursor.getInt(2));
            categorizeShapes(game, db);
            game.setCurrentPage(game.getPages().get(0));
        }
        cursor.close();
        return game;
    }

    private static void loadPages(Game game, SQLiteDatabase db) {
        String gameCode = getGameCode(game.getName(), db);
        Cursor cursor = db.rawQuery("SELECT * FROM " + gameCode + "_pages", null);
        while (cursor.moveToNext()) {
            Page page = new Page(cursor.getString(0), game);
            page.setBackgroundImage(cursor.getString(1));
            game.getPages().add(page);
        }
        cursor.close();
    }

    private static void loadShapes(Game game, SQLiteDatabase db) {
        String gameCode = getGameCode(game.getName(), db);
        Cursor cursor = db.rawQuery("SELECT * FROM " + gameCode + "_shapes", null);
        while (cursor.moveToNext()) {
            Shape shape = new Shape(cursor.getString(0), game);
            shape.setBoundary(new RectF(
                    cursor.getFloat(2),
                    cursor.getFloat(3),
                    cursor.getFloat(4),
                    cursor.getFloat(5)));
            shape.setImage(cursor.getString(6));
            shape.setText(cursor.getString(7));
            String rawScripts = cursor.getString(8);
            if (!rawScripts.equals("NONE")) {
                String[] scripts = rawScripts.split("/");
                for (String script : scripts) {
                    shape.getScripts().add(script);
                    String[] rawSplitScript = script.substring(0, script.length() - 1).split(" ");
                    List<String> splitScript = new ArrayList<>();
                    int i = 0;
                    while (i < rawSplitScript.length) {
                        switch (rawSplitScript[i]) {
                            case "on":
                                switch (rawSplitScript[++ i]) {
                                    case "click":
                                        splitScript.add("on click");
                                        splitScript.add("");
                                        break;
                                    case "enter":
                                        splitScript.add("on enter");
                                        splitScript.add("");
                                        break;
                                    case "drop":
                                        splitScript.add("on drop");
                                        splitScript.add(rawSplitScript[++ i]);
                                        break;
                                }
                                break;
                            case "goto":
                            case "play":
                            case "hide":
                            case "show":
                                splitScript.add(rawSplitScript[i]);
                                splitScript.add(rawSplitScript[++ i]);
                                break;
                        }
                        i ++;
                    }
                    shape.getSplitScripts().add(splitScript);
                }
            }
            shape.setVisible(cursor.getInt(9) == 1);
            shape.setMovable(cursor.getInt(10) == 1);
            shape.setRichText(shape.new RichText(
                    cursor.getInt(11) == 1,
                    cursor.getInt(12) == 1,
                    cursor.getString(13),
                    cursor.getString(14)));
            game.getShapes().add(shape);
        }
        cursor.close();
    }

    private static void categorizeShapes(Game game, SQLiteDatabase db) {
        String gameCode = getGameCode(game.getName(), db);
        for (Page page : game.getPages()) {
            Cursor cursor = db.rawQuery("SELECT _id FROM " + gameCode + "_shapes"
                    + " WHERE page_name = '" + page.getName() + "'", null);
            while (cursor.moveToNext()) {
                Shape shape = game.getShapes().get(cursor.getInt(0) - 1);
                page.getShapes().add(shape);
                shape.setPage(page);
            }
            cursor.close();
        }
    }

    public static void cleanCache(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name, _id FROM games WHERE name LIKE '%_tmp'", null);
        while (cursor.moveToNext()) {
            String gameName = cursor.getString(0);
            String gameCode = getGameCode(gameName, db);
            db.execSQL("DELETE FROM games WHERE name = '" + gameName + "';");
            db.execSQL("DROP TABLE IF EXISTS " + gameCode + "_pages" + ";");
            db.execSQL("DROP TABLE IF EXISTS " + gameCode + "_shapes" + ";");
        }
        cursor.close();
    }

    private static void createExample(SQLiteDatabase db) {
        db.execSQL("DELETE FROM games WHERE name = 'BunnyWorldExample';");
        db.execSQL("INSERT INTO games VALUES ('BunnyWorldExample', 5, 0, NULL);");
        String gameCode = getGameCode("BunnyWorldExample", db);
        db.execSQL("DROP TABLE IF EXISTS " + gameCode + "_pages" + ";");
        db.execSQL("CREATE TABLE " + gameCode + "_pages" + " ("
                + "name TEXT, "
                + "background_image TEXT, "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");");
        db.execSQL("DROP TABLE IF EXISTS " + gameCode + "_shapes" + ";");
        db.execSQL("CREATE TABLE " + gameCode + "_shapes" + " ("
                + "name TEXT, " // 0
                + "page_name TEXT, " // 1
                + "boundary_left REAL, boundary_top REAL, boundary_right REAL, boundary_bottom REAL, " // 2 3 4 5
                + "image TEXT, text TEXT, " // 6 7
                + "scripts TEXT, " // 8
                + "visible INTEGER, movable INTEGER, " // 9 10
                + "richtext_bold INTEGER, richtext_italic INTEGER, " // 11 12
                + "richtext_size TEXT, richtext_color TEXT, " // 13 14
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT" // 15
                + ");");
        db.execSQL("INSERT INTO " + gameCode + "_pages" + " VALUES "
                + "('Page1','(None)',NULL),"
                + "('Page2','(None)',NULL),"
                + "('Page3','(None)',NULL),"
                + "('Page4','(None)',NULL),"
                + "('Page5','(None)',NULL)"
                + ";");
        /*
        Page1
         */
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('Text1','Page1',100,0,300,200,'(None)','Bunny World!','NONE',1,0,1,0,'Huge','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('Text2','Page1',200,100,400,300,'(None)','You are in a maze of twisty little passages, all alike','NONE',1,0,0,0,'Tiny','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('door1','Page1',100,400,300,600,'(None)','','on click goto Page2;',1,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('door2','Page1',800,400,1000,600,'(None)','','on click goto Page3;',0,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('door3','Page1',1500,400,1700,600,'(None)','','on click goto Page4;',1,0,0,0,'Normal','Black',NULL);,");
        /*
        Page2
         */
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('mystic','Page2',800,100,1000,400,'mystic','','on click hide carrot play munching;/on enter show door2;',1,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('mysticText','Page2',400,300,600,500,'(None)','Mystic Bunny-Rub my tummy for a big surprise','NONE',1,0,0,0,'Tiny','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('door4','Page2',100,400,300,600,'(None)','','on click goto Page1;',1,0,0,0,'Normal','Black',NULL);");
        /*
        Page3
         */
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('fire','Page3',100,50,1700,250,'fire','','on enter play fire;',1,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('fireText','Page3',250,150,450,350,'(NONE)','Eek! Fire-Room. Run away!','NONE',1,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('door5','Page3',800,400,1000,600,'(None)','','on click goto Page2;',1,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('carrot','Page3',1500,400,1700,600,'carrot','','NONE',1,1,0,0,'Normal','Black',NULL);");
        /*
        Page4
         */
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('death','Page4',800,100,1000,400,'death','','on enter play evillaugh;/on drop carrot hide carrot play carrotcarrotcarrot hide death show exit;/on click play evillaugh;',1,0,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('deathText','Page4',350,300,550,500,'(None)','You must appease the Bunny of Death!','NONE',1,0,0,0,'Tiny','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('exit','Page4',1500,400,1700,600,'(None)','','on click goto Page5;',0,0,0,0,'Normal','Black',NULL);");
        /*
        Page5
         */
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('carrot1','Page5',200,200,400,400,'carrot','','NONE',1,1,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('carrot2','Page5',800,200,1000,400,'carrot','','NONE',1,1,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('carrot3','Page5',1400,200,1600,400,'carrot','','NONE',1,1,0,0,'Normal','Black',NULL);");
        db.execSQL("INSERT INTO " + gameCode + "_shapes" + " VALUES "
                + "('horayText','Page5',500,400,700,600,'(None)','You Win! Yay!','on enter play hooray;',1,0,1,0,'Large','Black',NULL);");
    }

}
