(ns clojure.gdx.scene2d.ui.image-button
  (:require [clojure.gdx.scene2d.utils.drawable :as drawable]
            [clojure.gdx.scene2d.utils.texture-region-drawable :as texture-region-drawable]
            [clojure.graphics.texture-region :as texture-region]
            [clojure.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui ImageButton)
           (com.badlogic.gdx.scenes.scene2d.utils Drawable)))

(defn- create-drawable*
  [{:keys [drawable/texture-region drawable/scale]}]
  (doto (texture-region-drawable/create texture-region)
    (drawable/set-min-size! (* scale (texture-region/width texture-region))
                            (* scale (texture-region/height texture-region)))))

(defn create
  [{:keys [drawable] :as opts}]
  (doto (ImageButton. ^Drawable (create-drawable* drawable))
    (actor/set-opts! opts)))
