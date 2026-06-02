(ns gdx.scenes.scene2d.utils.texture-region-drawable
  (:require [clojure.gdx.graphics.color :refer [Color]]
            [clojure.gdx.graphics.g2d.texture-region :as texture-region]
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
    (drawable/set-min-size! (* scale (texture-region/width texture-region))
                            (* scale (texture-region/height texture-region)))))
