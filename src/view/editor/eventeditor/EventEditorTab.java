package view.editor.eventeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.java.accessibility.util.GUIInitializedListener;

import api.ILevel;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import view.editor.Editor;
import view.enums.GUISize;

public abstract class EventEditorTab extends Editor
{
	private ObservableList<ILevel> levelList;
	private ArrayList<ILevel> chosenLevels;
	private LevelPicker levelPicker;
	private Text createdEventText;
	private Timer timer;
	private ResourceBundle myResources;
	
	public EventEditorTab(String language, ObservableList<ILevel> levelList)
	{
		myResources = ResourceBundle.getBundle(language);
		
		this.levelList = levelList;
		levelPicker = new LevelPicker(language, levelList, this);
		chosenLevels = new ArrayList<ILevel>(levelList);

		createdEventText = new Text(myResources.getString("eventMade"));
		createdEventText.setOpacity(0);
		
		timer = new Timer();
	}

	public void flashCreatedEventText()
	{
		createdEventText.setOpacity(1);
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				disappearText();
			}
		}, GUISize.EVENT_EDITOR_DISAPPEAR_SPEED.getSize(), GUISize.EVENT_EDITOR_DISAPPEAR_SPEED.getSize());
	}
	
	private void disappearText()
	{
		createdEventText.setOpacity(createdEventText.getOpacity() - 0.02);
		if ( createdEventText.getOpacity() <= 0 )
		{
			timer.cancel();
		}
	}
	
	public ScrollPane getLevelPickerPane()
	{
		return levelPicker.getPane();
	}
	
	public void choseLevels(List<ILevel> levels)
	{
		actionOnChosenLevels(levels);
		this.chosenLevels = (ArrayList<ILevel>) levels;
	}
	
	public Text getCreatedLevelText()
	{
		return createdEventText;
	}
	
	public ArrayList<ILevel> getChosenLevels()
	{
		return chosenLevels;
	}
	
	public abstract void actionOnChosenLevels(List<ILevel> levels);
}