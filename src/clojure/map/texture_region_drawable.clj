(ns clojure.map.texture-region-drawable
  (:require [clojure.rgba.color :refer [rgba->Color]]
            [clojure.texture-region.get-region-height :refer [get-region-height]]
            [clojure.texture-region.get-region-width :refer [get-region-width]]
            [clojure.utils.texture-region-drawable :as drawable]))

(defn create
  [{:keys [drawable/texture-region
           drawable/size
           drawable/tint]}]
  (let [drawable (doto (drawable/create texture-region)
                   (drawable/set-min-size! size size))]
    (when-let [color tint]
      (drawable/tint! drawable (rgba->Color color)))
    drawable))

(defn create*
  [{:keys [drawable/texture-region
           drawable/scale]}]
  (doto (drawable/create texture-region)
    (drawable/set-min-size! (* scale (get-region-width texture-region))
                            (* scale (get-region-height texture-region)))))
