package view;

import api.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Vooga;
import model.component.hud.HUD;
import model.component.movement.Orientation;
import model.component.movement.Position;
import model.component.physics.Collision;
import model.component.visual.Sprite;
import model.core.SystemManager;
import update.GameLoopManager;
import view.enums.GUISize;
import view.utilities.ButtonFactory;
import view.utilities.PopUp;
import view.utilities.SpriteUtilities;
<<<<<<< HEAD
import voogasalad.util.reflection.Reflection;
=======
import view.utilities.ToMainMenu;
>>>>>>> refs/remotes/origin/master

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

/**
 * 
 * @author Tom
 *
 */

public class View implements IView {

	private final double MILLISECOND_DELAY = 10;
	private final double SECOND_DELAY = MILLISECOND_DELAY / 1000;
	private final double gapSize = 1;
	private final ConsoleTextArea console = new ConsoleTextArea();
	private Group root = new Group();
	private ISystemManager model;
	private BorderPane pane;
	private SubScene subScene;
	private ViewUtilities viewUtils;
	private DragAndResizeDynamic DandR;
	private GameLoopManager manager;
	private HBox buttonBox = new HBox();
	private ResourceBundle myResources;
	private boolean debug;
	private Scene scene;
	private List<PopUp> myPopUpList = new ArrayList<PopUp>();

	public View(double viewWidth, double viewHeight, double sceneWidth, double sceneHeight, ILevel level, String language, boolean debug) {
		subScene = this.createSubScene(root, viewWidth, viewHeight);
		subScene.setOnMouseClicked(e -> deletePopUps(e));
		this.debug=debug;
		myResources = ResourceBundle.getBundle(language);
		initConsole();
		initButtons();
		pane = createMainBorderPane(root, this.subScene);
		scene = new Scene(pane, sceneWidth, sceneHeight);
		model = new SystemManager(scene, level);
		manager = new GameLoopManager(language, model);
		viewUtils = new ViewUtilities();
		if(this.debug){
			DandR = new DragAndResizeDynamic();
			DandR.makeRootDragAndResize(root);
		}
		ViewFeatureMethods.startTimeline(MILLISECOND_DELAY, e -> step(SECOND_DELAY));
	}

	private void deletePopUps(MouseEvent e) {
		if (e.getButton() == MouseButton.PRIMARY){
		for (PopUp popUp : myPopUpList){
			popUp.closeScene();
		}
		myPopUpList.clear();
	}
	}

	private void createLoopManager() {
		manager.show();
	}
	@Override
	public void setScene(Scene scene) {
		model.getLevel().setOnInput(scene);
	}

	public Pane getPane() {
		return this.pane;
	}

	public SubScene getSubScene() {
		return this.subScene;
	}

	public void startTimeline() {
		KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY), e -> this.step(SECOND_DELAY));
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(frame);
		timeline.play();
	}

	private SubScene createSubScene(Group root, double width, double height) {
		this.root = root;
		SubScene subScene = new SubScene(root, width, height);
		subScene.setFill(Color.WHITE);
		return subScene;
	}

	public void toggleHighlight(IEntity entity) {
		viewUtils.toggleHighlight(entity);
	}

	@Override
	public IEntitySystem getEntitySystem() {
		return model.getLevel().getEntitySystem();
	}

	@Override
	public ILevel getLevel() {
		return model.getLevel();
	}

	public void highlight(IEntity entity) {
		viewUtils.highlight(entity);
	}

	public Scene getScene() {
		return scene;
	}

	private ImageView getUpdatedImageView(IEntity e) {
		Position pos = e.getComponent(Position.class);
		ImageView imageView = SpriteUtilities.getImageView(e);
		imageView.setId(e.getID());
		imageView.setTranslateX(pos.getX());
		imageView.setTranslateY(pos.getY());
		if (e.hasComponent(Orientation.class)) {
			Orientation o = e.getComponent(Orientation.class);
			imageView.setRotate(o.getOrientation());
		}
		return imageView;
	}

	private Collection<Shape> getCollisionShapes(IEntity e) {
		List<Collision> collisions = e.getComponentList(Collision.class);
		Collection<Shape> shapes = new ArrayList<>();
		if(collisions.isEmpty()) {
			return shapes;
		}
		Collection<Bounds> bounds = new ArrayList<>();
		for (Collision c : collisions) {
			bounds.add(c.getMask());
		}
		for (Bounds b : bounds) {
			if (b == null) {
				continue;
			}
			Shape r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
			r.setFill(Color.TRANSPARENT);
			r.setStroke(Color.RED);
			r.setStrokeWidth(2);
			shapes.add(r);
		}
		return shapes;
	}

	@SuppressWarnings("unchecked")
	private void step(double dt) {
		// simulate
		model.step(dt);
		// render
		root.getChildren().clear();
		List<IEntity> entities = model.getEntitySystem().getAllEntities();
		for (IEntity e : entities) {
			if (e.hasComponents(Sprite.class, Position.class)) {
				if(debug){
					root.getChildren().addAll(getCollisionShapes(e));
				}
				if(debug){
					DandR.makeEntityDragAndResize(e);
				}
				ImageView imageView = getUpdatedImageView(e);
				imageView.setOnContextMenuRequested(event -> showPopUp(e,event));
				root.getChildren().add(imageView);
				if (!root.getChildren().contains(imageView)) {
					root.getChildren().add(imageView);
				}
			}

			if(e.hasComponents(HUD.class, Position.class)) {
				String hud = e.getComponent(HUD.class).getHUD();
				String shape = "Rectangle";
				double width = 100;
				double height = 100;
				String color = "10,10,10,1";
				for(String str: hud.split(";")) {
					String[] strip = str.split(":");
					String key = strip[0];
					String val = strip[1];
					if(key.equals(myResources.getString("shape"))) {
						shape = val;
					}
					if(key.equals(myResources.getString("width"))) {
						width = Double.parseDouble(val);
					}
					if(key.equals(myResources.getString("height"))) {
						height = Double.parseDouble(val);
					}
					if(key.equals(myResources.getString("color"))) {
						color = val;
					}
				}
				Shape s = (Shape) Reflection.createInstance(shape, width, height);

			}
		}
	}

	private BorderPane createMainBorderPane(Group root, SubScene subScene) {
		BorderPane pane = new BorderPane();
		ScrollPane center = new ScrollPane();
		root.setManaged(false);
		pane.setPadding(new Insets(gapSize, gapSize, gapSize, gapSize));
		pane.setCenter(center);
		center.setContent(subScene);
		center.setVbarPolicy(ScrollBarPolicy.NEVER);
		center.setHbarPolicy(ScrollBarPolicy.NEVER);
		pane.setBottom(setUpInputPane());
		return pane;
	}

	private BorderPane setUpInputPane() {
		BorderPane pane = new BorderPane();
		if(debug){
			pane.setTop(console);
		}
		pane.setBottom(buttonBox);
		return pane;
	}

	private void initButtons() {
		if(debug){
			buttonBox.getChildren().add(ButtonFactory.makeButton(myResources.getString("evaluate"), e -> this.evaluate()));
			buttonBox.getChildren().add(ButtonFactory.makeButton(myResources.getString("loopManager"), e -> this.createLoopManager()));
		}
		buttonBox.getChildren().add(ButtonFactory.makeButton(myResources.getString("mainMenu"), e -> ToMainMenu.toMainMenu(pane)));
		buttonBox.getChildren().add(ButtonFactory.makeButton(myResources.getString("startGameLoop"), e -> this.model.play()));
		buttonBox.getChildren().add(ButtonFactory.makeButton(myResources.getString("pauseGameLoop"), e -> this.model.pauseLoop()));
	}
<<<<<<< HEAD

	private void mainMenu() { 
		Stage myStage = (Stage) pane.getScene().getWindow();
		myStage.setWidth(GUISize.MAIN_SIZE.getSize());
		myStage.setHeight(GUISize.MAIN_SIZE.getSize());
		Vooga vooga = new Vooga(myStage);
		vooga.init();
	}

=======
>>>>>>> refs/remotes/origin/master
	private void initConsole() {
		console.setText(myResources.getString("enterCommands"));
		console.appendText("\n\n");

		console.setOnKeyPressed(e -> {
			KeyCode keyCode = e.getCode();
			if (keyCode == KeyCode.ENTER) {
				this.evaluate();
				e.consume();
			}
		});
	}
	
	public void showPopUp(IEntity entity, ContextMenuEvent event) {
		
		Map<String, EventHandler<ActionEvent>> menuMap = new LinkedHashMap<String, EventHandler<ActionEvent>>();
		menuMap.put(myResources.getString("remove"), e -> ViewFeatureMethods.removeFromDisplay(entity, getEntitySystem()));
		menuMap.put(myResources.getString("sendBack"), e -> ViewFeatureMethods.sendToBack(entity, getEntitySystem()));
		menuMap.put(myResources.getString("sendFront"), e -> ViewFeatureMethods.sendToFront(entity, getEntitySystem()));
		menuMap.put(myResources.getString("sendBackOne"), e ->ViewFeatureMethods.sendBackward(entity, getEntitySystem()));
		menuMap.put(myResources.getString("sendForwardOne"), e -> ViewFeatureMethods.sendForward(entity, getEntitySystem()));
		
		PopUp myPopUp = new PopUp(GUISize.POP_UP_WIDTH.getSize(),GUISize.POP_UP_HIEGHT.getSize());
		myPopUp.show(setPopUp(menuMap),event.getScreenX(),event.getScreenY());
		myPopUpList.add(myPopUp);
	}
	
	public ScrollPane setPopUp(Map<String,EventHandler<ActionEvent>> map){
		VBox box = new VBox();
		for (Entry<String, EventHandler<ActionEvent>> entry : map.entrySet()){
			Button button = ButtonFactory.makeButton(entry.getKey(), entry.getValue());
			button.setMaxWidth(Double.MAX_VALUE);
			box.getChildren().add(button);
		}
		return new ScrollPane(box);
	}

	private void evaluate() {
		String text = console.getText();
		String command = text.substring(text.lastIndexOf("\n")).trim();
		console.println("\n----------------");
		try {
			Object result = model.getShell().evaluate(command);
			if (result != null) {
				console.println(result.toString());
			}
		} catch (Exception e) {
			console.println(e.getMessage());
		}
		console.println();
	}

	@Override
	public void dehighlight(IEntity entity) {
		viewUtils.dehighlight(entity);
	}
	
}
