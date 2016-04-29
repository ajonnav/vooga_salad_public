package model.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import model.component.visual.AnimatedSprite;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import api.IComponent;
import api.IEntity;
import javafx.animation.Animation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

/**
 * Implementation of an IEntity.
 * <p>
 * This implementation uses Java's UUID to generate unique IDs.
 *
 * @author Rhondu Smithwick
 */
public class Entity implements IEntity, ChangeListener {

	private Animation currentAnimation;
	private SimpleStringProperty animationString = new SimpleStringProperty(this, "animationStatus", "");
	
	@XStreamAsAttribute()
	private final String ID;

	@XStreamAsAttribute()
	private String name;

	@XStreamAlias("components")
	private final ObservableMap<Class<? extends IComponent>, List<IComponent>> componentMap = FXCollections
			.observableHashMap();

	@XStreamAlias("Specs")
	private final Map<Class<? extends IComponent>, Integer> specs = Maps.newLinkedHashMap();

	private Set<String> groupIDs = new HashSet<String>();

	public Entity() {
		this("John Doe");
	}

	public Entity(String name) {
		ID = UUID.randomUUID().toString();
		animationString.addListener(this);
		setName(name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public Set<String> getGroupIDs() {
		return this.groupIDs;
	}

	@Override
	public void setGroupIDs(Set<String> groupIDs) {
		this.groupIDs = groupIDs;
	}

	@Override
	public void addGroupIDs(String... groupIDs) {
		this.groupIDs.addAll(Arrays.asList(groupIDs));
	}

	@Override
	public Collection<IComponent> getAllComponents() {
		return componentMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
	}

	@Override
	public Set<Class<? extends IComponent>> getComponentClasses() {
		return componentMap.keySet();
	}

	@Override
	public <T extends IComponent> List<T> getComponentList(Class<T> componentClass) {
		if (!hasComponent(componentClass)) {
			return new ArrayList<T>();
		}
		List<IComponent> currentComponents = componentMap.get(componentClass);
		return Lists.transform(currentComponents, componentClass::cast);
	}

	@Override
	public <T extends IComponent> boolean hasComponent(Class<T> componentClass) {
		return componentMap.containsKey(componentClass);
	}

	@Override
	public boolean forceAddComponent(IComponent componentToAdd, boolean forceAdd) {
		Class<? extends IComponent> componentClass = componentToAdd.getClassForComponentMap();
		boolean noTemplate = specs.isEmpty();
		boolean preCondition = forceAdd || noTemplate || specs.containsKey(componentClass);
		if (!preCondition) {
			return false;
		}
		if (!componentMap.containsKey(componentClass)) {
			componentMap.put(componentClass, Lists.newArrayList());
		}
		List<IComponent> componentStore = componentMap.get(componentClass);
		boolean eligibleByForceOrSize = forceAdd || noTemplate || (componentStore.size() < getSpec(componentClass));
		return eligibleByForceOrSize && componentStore.add(componentToAdd);
	}

	@Override
	public Boolean removeComponent(Class<? extends IComponent> componentClassToRemove) {
		if (componentMap.containsKey(componentClassToRemove)) {
			List<IComponent> components = componentMap.remove(componentClassToRemove);
			components.stream().forEach(IComponent::removeBindings);
			return true;
		}
		return false;
	}

	@Override
	public Map<Class<? extends IComponent>, Integer> getSpecs() {
		return specs;
	}

	@Override
	public String toString() {
		return String.format("ID: %s, Components: %s", ID, componentMap.toString());
	}
	
	public void setAnimationString(String animation) {
		animationString.set(animation);
	}
	
	public String getAnimationString() {
		return animationString.get();
	}
	
	public void setAnimation(Animation animation) {
		this.currentAnimation = animation;
	}
	
	public Animation getAnimation() {
		return currentAnimation;
	}

	@Override
	public void changed(ObservableValue arg0, Object arg1, Object arg2) {
		if(currentAnimation!=null) {
			currentAnimation.stop();
		}
        currentAnimation = this.getComponent(AnimatedSprite.class).getAnimation((String)arg2);
	}
	
	

}