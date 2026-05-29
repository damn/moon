(ns clojure.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.utils TextureRegionDrawable)))

(defn create
  [{:keys [drawable/texture-region
           drawable/size
           drawable/tint]}]
  (let [drawable (doto (TextureRegionDrawable. ^TextureRegion texture-region)
                   (.setMinSize size size))]
    (when tint
      (.tint drawable (color/create tint)))
    drawable))

(defn create*
  ^TextureRegionDrawable
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (TextureRegionDrawable. ^TextureRegion texture-region)
    (.setMinSize (* scale (.getRegionWidth texture-region))
                 (* scale (.getRegionHeight texture-region)))))
