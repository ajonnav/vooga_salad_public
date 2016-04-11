package events;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rhondusmithwick on 4/10/16.
 *
 * @author Rhondu Smithwick
 */
public class InputSystem {
    private final Map<String, Action> actionMap = new HashMap<>();
    private final Map<KeyCode, String> keyMap = new HashMap<>();

    public void addEvent(String actionName, Action action) {
        actionMap.put(actionName, action);
    }
    
    public void addKeyBinding(KeyCode k, String actionName) {
    	keyMap.put(k, actionName);
    }

    public boolean containsEvent(String actionName) {
        return actionMap.containsKey(actionName);
    }
    
    public boolean containsKey(KeyCode k) {
        return keyMap.containsKey(k);
    }

    public void take(KeyCode k) {
    	// TODO maybe change this if tree
        if (containsKey(k)) {
        	if(containsEvent(keyMap.get(k))) {
        		actionMap.get(keyMap.get(k)).activate();
        	}
        }
    }
}
