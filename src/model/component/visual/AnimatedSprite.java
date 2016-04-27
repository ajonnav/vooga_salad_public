package model.component.visual;

import javafx.animation.Animation;
import javafx.beans.property.SimpleObjectProperty;
import utility.SingleProperty;
import voogasalad.util.spriteanimation.animation.AnimationContainer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;

/**
 * This component contains the animated sprite
 *
 * @author Melissa Zhang
 */
public class AnimatedSprite extends Sprite {
    private static final String DEFAULT_BUNDLE = "resources/spriteProperties,abobo";
    private final SingleProperty<String> singleProperty = new SingleProperty<>("BundlePath", DEFAULT_BUNDLE);
    private transient AnimationContainer container;

    public AnimatedSprite() {
    }

    /**
     * Construct with no animation.
     *
     * @param imagePath starting value
     */
    public AnimatedSprite(String imagePath, String bundlePath) { // TODO: place default in resource file
        super(imagePath);
        setBundlePath(bundlePath);
    }

    /**
     * Construct with starting values.
     *
     * @param imagePath   String path to image
     * @param imageWidth  width of image
     * @param imageHeight height of image
     * @param imagePath   String path to spritesheet
     */
    public AnimatedSprite(String imagePath, double imageWidth, double imageHeight, String bundlePath) {
        super(imagePath, imageWidth, imageHeight);
        setBundlePath(bundlePath);
    }

    public SimpleObjectProperty<String> bundlePathProperty() {
        return singleProperty.property1();
    }

    public String getBundlePath() {
        return bundlePathProperty().get();
    }

    public void setBundlePath(String bundlePath) {
        bundlePathProperty().set(bundlePath);
        reInitializeContainer();
    }

    @Override
    public void setImagePath(String imagePath) {
        super.setImagePath(imagePath);
        if (container != null) {
            reInitializeContainer();
        }
    }

    public AnimationContainer getContainer() {
        return container;
    }

    private void reInitializeContainer() {
        this.container = new AnimationContainer(getImageView(), getBundlePath());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        reInitializeContainer();
    }

    @Override
    public List<SimpleObjectProperty<?>> getProperties() {
        List<SimpleObjectProperty<?>> superProps = super.getProperties();
        superProps.add(bundlePathProperty());
        return superProps;
    }

    public Set<String> getAnimationNames() {
        return getContainer().getAnimationNames();
    }

    public boolean hasAnimation(String animationName) {
        return getContainer().hasAnimation(animationName);
    }

    public Animation getAnimation(String animationName) {
        return getContainer().getAnimation(animationName);
    }
}