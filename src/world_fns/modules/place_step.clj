(ns world-fns.modules.place-step
  (:require [clojure.map-properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]
            [world-fns.modules.place-step.place-star :refer [place-module*]]))

(def ^:private module-offset-tiles 1)
(def ^:private number-modules-x 8)
(def ^:private number-modules-y 4)

(defn f
  [modules-tiled-map
   modules-scale
   scaled-grid
   unscaled-grid
   unscaled-floor-positions
   unscaled-transition-positions]
  (let [[modules-width modules-height] modules-scale
        _ (assert (and (= (props-get (get-properties modules-tiled-map) "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (props-get (get-properties modules-tiled-map) "height")
                          (* number-modules-y (+ modules-height module-offset-tiles)))))
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* module-offset-tiles
                                             modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? false))
                            scaled-grid
                            unscaled-floor-positions)
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* module-offset-tiles
                                             modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? true
                                             :transition-neighbor? #(#{:transition :wall}
                                                                     (get unscaled-grid %))))
                            scaled-grid
                            unscaled-transition-positions)]
    scaled-grid))
