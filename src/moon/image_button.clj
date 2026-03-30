(ns moon.image-button
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d Event)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  Drawable
                                                  TextureRegionDrawable)
           (moon Stage)))

(defn create
  ^ImageButton
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
                                     (on-clicked actor (.ctx ^Stage (Event/.getStage event)))))))
    (when-let [tooltip (:tooltip opts)]
      (.addListener image-button (text-tooltip/create (str tooltip) skin)))
    image-button))
