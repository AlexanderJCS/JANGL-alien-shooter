package helper;

import helper.ini.IniParser;
import jangl.graphics.font.Font;

import java.util.Random;

public class Consts {
    public static final Font FONT = new Font(
            "src/main/resources/font/press_start.fnt", "src/main/resources/font/press_start.png"
    );

    public static final IniParser SETTINGS = new IniParser("src/main/resources/settings.ini");
    public static final Random RANDOM = new Random();
}
