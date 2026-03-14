(ns moon.image-button
  (:require [moon.table :as table])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton
                                               Skin
                                               TextTooltip)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  Drawable
                                                  TextureRegionDrawable)
           (moon Stage)))

(defn create
  ^ImageButton
  [{:keys [^TextureRegion drawable/texture-region
           on-clicked
           drawable/scale
           ^Skin skin]
    :as opts}]
  (let [scale (or scale 1)
        [w h] [(.getRegionWidth  texture-region)
               (.getRegionHeight texture-region)]
        drawable (doto (TextureRegionDrawable. texture-region)
                   (.setMinSize (* scale w) (* scale h)))
        image-button (ImageButton. ^Drawable drawable)]
    (when on-clicked
      (.addListener image-button (proxy [ChangeListener] []
                                   (changed [event actor]
                                     (on-clicked actor (.ctx ^Stage (Event/.getStage event)))))))
    (when-let [tooltip (:tooltip opts)]
      (.addListener image-button (TextTooltip. (str tooltip) skin)))
    (table/set-opts! image-button opts)))
