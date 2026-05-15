(ns com.badlogic.gdx.scenes.scene2d.ui.image-button
  (:require [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable
                                                  TextureRegionDrawable)))

(defn- create-drawable*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (TextureRegionDrawable. ^TextureRegion texture-region)
    (.setMinSize (* scale (.getRegionWidth texture-region))
                 (* scale (.getRegionHeight texture-region)))))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (ImageButton. ^Drawable (create-drawable* drawable))
    (actor/set-opts! opts)))
