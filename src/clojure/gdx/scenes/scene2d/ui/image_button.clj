(ns clojure.gdx.scenes.scene2d.ui.image-button
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.scenes.scene2d.ui.image-button :as image-button]
            [moon.ui.actor :as actor])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn- create-drawable*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (TextureRegionDrawable. ^TextureRegion texture-region)
    (.setMinSize (* scale (texture-region/width texture-region))
                 (* scale (texture-region/height texture-region)))))

(defmethod actor/create :ui/image-button
  [{:keys [drawable] :as opts}]
  (doto (image-button/create (create-drawable* drawable))
    (actor/set-opts! opts)))
