(ns moon.ui.image-button
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.table :as table]
            [moon.ui.tooltip :as tooltip])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  Drawable
                                                  TextureRegionDrawable)))

(defn create
  [{:keys [^TextureRegion drawable/texture-region
           on-clicked
           drawable/scale
           skin]
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
                                     (on-clicked actor (.ctx (Event/.getStage event)))))))
    (when-let [tooltip (:tooltip opts)]
      (tooltip/add! image-button
                    tooltip
                    skin))
    (table/set-opts! image-button opts)))
