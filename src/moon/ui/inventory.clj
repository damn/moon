(ns moon.ui.inventory
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Actor
                                            Group)
           (com.badlogic.gdx.scenes.scene2d.ui Image
                                               Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn- find-cell [^Group group cell]
  (first (filter #(= (Actor/.getUserObject %) cell)
                 (.getChildren group))))

(defn- window->cell [inventory-window cell]
  (-> inventory-window
      (Group/.findActor "inventory-cell-table")
      (find-cell cell)))

(defn set-item! [inventory-window cell {:keys [^TextureRegion texture-region tooltip-text]} skin]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (Group/.findActor cell-widget "image-widget")
        cell-size (:cell-size (Actor/.getUserObject image-widget))
        drawable (doto (TextureRegionDrawable. texture-region)
                   (.setMinSize cell-size cell-size))]
    (Image/.setDrawable image-widget drawable)
    (Actor/.addListener cell-widget (TextTooltip. ^String tooltip-text ^Skin skin))
    nil))

(defn remove-item! [inventory-window cell]
  (let [cell-widget (window->cell inventory-window cell)
        image-widget (Group/.findActor cell-widget "image-widget")]
    (Image/.setDrawable image-widget (:background-drawable (Actor/.getUserObject image-widget)))
    ; TODO
    ;(.removeListener actor (.getListeners actor))
    ; ... first find the listener
    #_(tooltip/remove! cell-widget)
    nil))
