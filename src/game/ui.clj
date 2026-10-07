(ns game.ui
  (:require [moon.item :as item]
            [moon.textures :as textures])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor Group Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Button ButtonGroup Image Skin TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable TextureRegionDrawable)))

(defn- action-bar-get-data
  [action-bar]
  {:post [(:horizontal-group %)
          (:button-group %)]}
  (let [group (.findActor ^Group action-bar "moon.ui.action-bar.horizontal-group")]
    {:horizontal-group group
     :button-group (.getUserObject ^Actor group)}))

(defn- action-bar-remove-skill!
  [action-bar skill-id]
  (let [{:keys [horizontal-group button-group]} (action-bar-get-data action-bar)
        button (get horizontal-group skill-id)]
    (.remove ^Actor button)
    (.remove ^ButtonGroup button-group ^Button button)
    nil))

(defn action-bar-selected-skill [action-bar]
  (when-let [skill-button (.getChecked ^ButtonGroup (:button-group (action-bar-get-data action-bar)))]
    (.getUserObject ^Actor skill-button)))

(defn- inventory-window-get-cell [inventory-window cell]
  (->> (.getChildren ^Group (.findActor ^Group inventory-window "inventory-cell-table"))
       (filter #(= (.getUserObject ^Actor %) cell))
       first))

(defn- inventory-window-remove-item! [inventory-window cell]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")]
    (.setDrawable ^Image image-widget ^Drawable (:background-drawable (.getUserObject ^Actor image-widget)))
    ; !! TODO FIXME FIXME FIXME !!!
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))

(defn- inventory-window-set-item! [inventory-window cell {:keys [texture-region tooltip-text]} skin]
  (let [cell-widget (inventory-window-get-cell inventory-window cell)
        image-widget (.findActor ^Group cell-widget "image-widget")
        cell-size (:cell-size (.getUserObject ^Actor image-widget))]
    (.setDrawable ^Image image-widget ^Drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                                                  (.setMinSize cell-size cell-size)))
    (.addListener ^Actor cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn ui-set-item! [skin stage textures cell item]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-set-item! cell
                                  {:texture-region (textures/texture-region textures (:entity/image item))
                                   :tooltip-text (item/info-text item)}
                                  skin)))

(defn ui-remove-item! [stage cell]
  (-> (.getRoot ^Stage stage)
      (.findActor "moon.ui.windows.inventory")
      (inventory-window-remove-item! cell)))

(defn toggle-inventory-visible! [stage]
  (let [inventory (-> (.getRoot ^Stage stage)
                      (.findActor "moon.ui.windows.inventory"))]
    (.setVisible ^Actor inventory (not (.isVisible ^Actor inventory)))))
