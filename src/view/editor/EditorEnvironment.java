package view.editor;

import api.IEntity;
import api.ILevel;
import api.ISystemManager;
import api.IView;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.component.movement.Position;
import model.component.visual.Sprite;
import model.core.SystemManager;
import update.GameLoopManager;
import view.View;
import view.enums.DefaultEntities;
import view.enums.GUISize;
import view.utilities.*;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EditorEnvironment extends Editor {

	private static final int SINGLE = 1;
	private BorderPane environmentPane = new BorderPane();
	private ILevel myEntitySystem;
	private ResourceBundle myResources;
	private ObservableList<IEntity> masterEntityList;
	private ObservableList<ILevel> allEnvironmentsList;
	private VBox leftPane = new VBox();
	private VBox rightPane = new VBox();
	private VBox masterEntityButtonsBox = new VBox();
	private VBox environmentEntityButtonsBox = new VBox();
	private TextField nameField = new TextField();
	private ScrollPane scrollPane = new ScrollPane(environmentPane);
	private ISystemManager game;
	private IView view;
	private GameLoopManager manager;

	public EditorEnvironment(String language, ILevel toEdit, ObservableList<IEntity> masterList,
			ObservableList<ILevel> addToList) {
		myResources = ResourceBundle.getBundle(language);
		masterList.addListener((ListChangeListener<IEntity>) c -> {
			this.updateDisplay(masterList);
		});
		masterEntityList = masterList;
		myEntitySystem = toEdit; 
		allEnvironmentsList = addToList;
		
		setUpGame(language);
		addLayoutComponents();
	}
	
	private void setUpGame(String language){
		game = new SystemManager(myEntitySystem);
		view = new View(game, (GUISize.TWO_THIRDS_OF_SCREEN.getSize()), GUISize.HEIGHT_MINUS_TAB.getSize(),
				scrollPane);
		manager = new GameLoopManager(language, game);
	}

	private void addLayoutComponents() {
		setLeftPane();
		setRightPane();
	}

	private void setLeftPane() {
		leftPane.getChildren().add(setNameDisplay());
		leftPane.getChildren().add(setEntityOptionsDisplay());
	}

	private TextField setNameDisplay() {
		if (view.getEntitySystem().getName().equals("")) {
			nameField.setText(myResources.getString("environmentName"));
		} else {
			nameField.setText(view.getEntitySystem().getName());
		}
		return nameField;
	}

	private ScrollPane setEntityOptionsDisplay() {
		if (masterEntityList.isEmpty()) {
			loadDefaults();
		}
		populateVbox(masterEntityButtonsBox, masterEntityList);
		return (new ScrollPane(masterEntityButtonsBox));
	}

	private void populateVbox(VBox vbox, Collection<IEntity> collection) {
		vbox.getChildren().clear();
		for (IEntity entity : collection) {
			Button addEntityButton = ButtonFactory.makeButton(( entity).getName(),
					e -> addToSystem(EntityCopier.copyEntity( entity)));
			(addEntityButton).setMaxWidth(Double.MAX_VALUE);
			vbox.getChildren().add(addEntityButton);
		}
	}

	private void addToSystem(IEntity entity) {
		view.getEntitySystem().addEntity(entity);
		addToScene(entity);
	}

	private void loadDefaults() {
		if (Alerts.showAlert(myResources.getString("addDefaults"), myResources.getString("addDefaultsQuestion"),
				myResources.getString("defaultsMessage"), AlertType.CONFIRMATION)) {
			masterEntityList.add(DefaultEntities.BACKGROUND.getDefault());
			masterEntityList.add(DefaultEntities.CHAR_1.getDefault());
			masterEntityList.add(DefaultEntities.CHAR_2.getDefault());
		}
	}

	private void setRightPane() {
		rightPane.getChildren().add(ButtonFactory.makeButton(myResources.getString("saveEnvironment"), e -> saveEnvironment()));
		rightPane.getChildren().add(new ScrollPane(environmentEntityButtonsBox));
		rightPane.getChildren().add(ButtonFactory.makeButton(myResources.getString("loopManager"), e -> createLoopManager()));
	}
	
	private void createLoopManager() {
		manager.show();
		
	}

	private void addToScene(IEntity entity) {
		try {
			if (!entity.hasComponent(Position.class) || !entity.hasComponent(Sprite.class)) {
				addComponents(entity);
			}
			environmentEntityButtonsBox.getChildren().add(createEntityButton(entity));
		} catch (Exception e) {
			Alerts.showAlert(myResources.getString("error"), null, myResources.getString("unableToAddEntity"),
					AlertType.ERROR);
		}
	}

	private Button createEntityButton(IEntity entity) {
		Button entityInButton = new Button(entity.getName());
		entityInButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MouseButton button = event.getButton();
				if (button == MouseButton.PRIMARY) {
					entityLeftClicked(entity);
				} else if (button == MouseButton.SECONDARY) {
					entityRightClicked(entity, entityInButton, event);
				}
			}
		});
		return entityInButton;
	}

	private void entityLeftClicked(IEntity entity) {
		view.toggleHighlight(entity);
	}

	
	private void entityRightClicked(IEntity entity, Button entityButton, MouseEvent event) {
		view.highlight(entity);
		Map<String, EventHandler<ActionEvent>> menuMap = new HashMap<String, EventHandler<ActionEvent>>();
		menuMap.put(myResources.getString("remove"), e -> removeFromDisplay(entity, entityButton));
		menuMap.put(myResources.getString("sendBack"), e -> sendToBack(entity));
		menuMap.put(myResources.getString("sendFront"), e -> sendToFront(entity));
		entityButton.setContextMenu(ContextMenuFactory.createContextMenu(menuMap));
	}

	private void sendToFront(IEntity e) {
		if(view.getEntitySystem().removeEntity(e.getID())!=null) {
			view.getEntitySystem().addEntity(e);
		}
	}

	private void sendToBack(IEntity e) {
		if(view.getEntitySystem().removeEntity(e.getID())!=null) {
			view.getEntitySystem().getAllEntities().add(0, e);
		}
	}

	private void sendForward(IEntity e) {
		int index = view.getEntitySystem().getAllEntities().indexOf(e)+1;
		if(view.getEntitySystem().removeEntity(e.getID())!=null) {
			if(index<view.getEntitySystem().getAllEntities().size()) {
				view.getEntitySystem().getAllEntities().add(index, e);
			}
		}
	}

	private void sendBackward(IEntity e) {
		int index = view.getEntitySystem().getAllEntities().indexOf(e)-1;
		if(view.getEntitySystem().removeEntity(e.getID())!=null) {
			if(index>=0) {
				view.getEntitySystem().getAllEntities().add(index, e);
			}
		}
	}

	private void updateDisplay(ObservableList<IEntity> masterList) {
		masterEntityList = masterList;
		populateVbox(masterEntityButtonsBox, masterEntityList);
	}

	@Override
	public void updateEditor() {
		populateVbox(masterEntityButtonsBox, masterEntityList);
	}

	@Override
	public void populateLayout() {
		environmentPane.setRight(rightPane);
		environmentPane.setLeft(leftPane);
		environmentPane.setCenter(view.getPane());
		view.getSubScene().setOnMouseClicked(e -> updateEnviornmentBox(environmentEntityButtonsBox,view.getLevel()));
	}

	private void updateEnviornmentBox(VBox vBox, ILevel entities) {
		vBox.getChildren().clear();
		for (IEntity entity : entities.getAllEntities()){
			vBox.getChildren().add(createEntityButton(entity));
		}
	}

	private void saveEnvironment() {
		String name = getName();
		view.getEntitySystem().setName(name);
		allEnvironmentsList.remove(view.getLevel());
		allEnvironmentsList.add(view.getLevel());
		environmentPane.getChildren().clear();
		environmentPane.setCenter(saveMessage(myResources.getString("saveMessage")));
	}

	private String getName() {
		String returnName = null;
		if (nameField.getText().equals(myResources.getString("environmentName"))) {
			returnName = UserInputBoxFactory.userInputBox(myResources.getString("noName"),
					myResources.getString("noNameMessage"));
		} else {
			returnName = nameField.getText();
		}
		return returnName;
	}

	private void addComponents(IEntity entity) {
		if (Alerts.showAlert(myResources.getString("confirm"), myResources.getString("componentsRequired"),
				myResources.getString("addComponentQuestion"), AlertType.CONFIRMATION)) {
			addPositionComponent(entity);
			addImagePathComponent(entity);
		}
	}

	private void addPositionComponent(IEntity entity) {
		entity.setSpec(Position.class, SINGLE);
		Position pos = new Position();
		entity.addComponent(pos);
	}

	private void addImagePathComponent(IEntity entity) {
		File file = FileUtilities.promptAndGetFile(FileUtilities.getImageFilters(),
				myResources.getString("pickImagePathImage"));
		entity.setSpec(Sprite.class, SINGLE);
		entity.addComponent(new Sprite(file.getPath()));
	}

	private void removeFromDisplay(IEntity entity, Button entityButton) {
		view.getEntitySystem().removeEntity(entity.getID());
		environmentEntityButtonsBox.getChildren().remove(entityButton);
	}

	public ILevel getEntitySystem() {
		return view.getLevel();
	}

	@Override
	public ScrollPane getPane() {
		return scrollPane;
	}

	public boolean displayContains(IEntity checkEntity) {
		return masterEntityList.contains(checkEntity);
	}

	public boolean environmentContains(IEntity checkEntity) {
		return view.getEntitySystem().containsEntity(checkEntity);
	}

}
