(ns gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [clojure.gdx.graphics.color :refer [Color]]
            [clojure.gdx.graphics.g2d.texture-region.get-region-height :refer [get-region-height]]
            [clojure.gdx.graphics.g2d.texture-region.get-region-width :refer [get-region-width]]
            [clojure.gdx.scene2d.utils.texture-region-drawable :as drawable]))

(defn create
  [{:keys [drawable/texture-region
           drawable/size
           drawable/tint]}]
  (let [drawable (doto (drawable/create texture-region)
                   (drawable/set-min-size! size size))]
    (when tint
      (drawable/tint! drawable (Color tint)))
    drawable))

(defn create*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (drawable/create texture-region)
    (drawable/set-min-size! (* scale (get-region-width texture-region))
                            (* scale (get-region-height texture-region)))))
