(ns clojure.gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.scenes.scene2d.utils.texture-region-drawable :as texture-region-drawable]))

(defn create
  [{:keys [drawable/texture-region
           drawable/min-size
           drawable/tint]}]
  (let [drawable (doto (texture-region-drawable/create texture-region)
                   (texture-region-drawable/set-min-size! min-size))]
    (when tint
      (texture-region-drawable/tint! drawable (color/create tint)))
    drawable))
