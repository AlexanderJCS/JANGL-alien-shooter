package helper;

import jangl.io.mouse.Mouse;
import jangl.io.mouse.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class EventsManager {
    private static List<MouseEvent> mouseEvents = new ArrayList<>();

    public static void getEvents() {
        mouseEvents = Mouse.getEvents();
    }

    public static List<MouseEvent> getMouseEvents() {
        return mouseEvents;
    }
}
