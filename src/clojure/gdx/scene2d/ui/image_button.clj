(ns clojure.gdx.scene2d.ui.image-button
  (:require [com.badlogic.gdx.scenes.scene2d.utils.drawable :as drawable]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn- create-drawable*
  [{:keys [^TextureRegion drawable/texture-region
           drawable/scale]}]
  (doto (texture-region-drawable/create texture-region)
    (drawable/set-min-size! (* scale (.getRegionWidth texture-region))
                            (* scale (.getRegionHeight texture-region)))))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (ImageButton. ^Drawable (create-drawable* drawable))
    (actor/set-opts! opts)))
