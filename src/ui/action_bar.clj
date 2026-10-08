(ns ui.action-bar
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup ImageButton Skin TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn add-skill!
  [action-bar
   {:keys [skill-id
           texture-region
           tooltip-text]}
   skin]
  (let [scale 2
        horizontal-group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")
        button-group (.getUserObject ^Actor horizontal-group)
        button (doto (ImageButton.
                      (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                        (.setMinSize (* scale (.getRegionWidth ^TextureRegion texture-region))
                                     (* scale (.getRegionHeight ^TextureRegion texture-region)))))
                 (.addListener (TextTooltip. ^String tooltip-text ^Skin skin))
                 (.setUserObject skill-id))]
    (.addActor ^Group horizontal-group ^Actor button)
    (.add ^ButtonGroup button-group ^Button button)
    nil))

(defn remove-skill!
  [action-bar skill-id]
  (let [horizontal-group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")
        button-group (.getUserObject ^Actor horizontal-group)
        button (get horizontal-group skill-id)]
    (.remove ^Actor button)
    (.remove ^ButtonGroup button-group ^Button button)
    nil))

(defn selected-skill [action-bar]
  (let [horizontal-group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")
        button-group (.getUserObject ^Actor horizontal-group)]
    (when-let [skill-button (.getChecked ^ButtonGroup button-group)]
      (.getUserObject ^Actor skill-button))))
