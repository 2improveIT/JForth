package tools;

import java.awt.*;
import java.util.HashMap;

public class ForthProperties {
    private static HashMap<String, Object> map = new HashMap<>();

    static {
        putImgScale (new Point(400, 400));
        putBkColor (Color.BLACK);
        putImgFormat ("png");
    }

    public static String getImgFormat() {
        return (String)map.get("imgformat");
    }
    public static void putImgFormat(String s) {
        map.put("imgformat", s);
    }

    public static Point getImgScale() {
        return (Point)map.get("imgscale");
    }
    public static void putImgScale(Point p) {
        map.put("imgscale", p);
    }

    public static Color getBkColor() {
        return (Color)map.get("bkcolor");
    }
    public static void putBkColor(Color c) {
        map.put("bkcolor", c);
    }
}
