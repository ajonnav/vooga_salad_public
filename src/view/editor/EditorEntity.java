package view.editor;

import java.util.Collection;

import gui.GuiObject;
import gui.GuiObjectFactory;
import model.entity.Entity;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import api.IComponent;
import api.IEntity;
import api.ISerializable;
/**
 * 
 * @author Melissa Zhang
 *
 */

public class EditorEntity extends Editor{
	
	private Pane editorPane;
	private IEntity myEntity;
	private VBox vbox;
	private String myLanguage;

	public EditorEntity(ISerializable entity, String language, Button button) {
		editorPane = new GridPane();
		myEntity = (Entity) entity;
		myLanguage = language;
	}

	@Override
	public void loadDefaults() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Pane getPane() {
		populateLayout();
		return editorPane;
	}

	@Override
	public void populateLayout() {
		vbox = new VBox();
		GuiObjectFactory guiFactory = new GuiObjectFactory(myLanguage);
		Collection<IComponent> componentList = myEntity.getAllComponents();
		
		for (IComponent component: componentList){
			for (SimpleObjectProperty<?> property: component.getProperties()){
			GuiObject object = guiFactory.createNewGuiObject(property.getName(), property, property.getValue());
			if (object!=null){
				vbox.getChildren().add((Node) object.getGuiNode());
			}
			}
		}
			
		editorPane.getChildren().add(vbox);
	}
	

	@Override
	public void updateEditor() {
		populateLayout();
	}

	@Override
	public void addSerializable(ISerializable serialize) {
		// TODO Auto-generated method stub
		
	}


}
