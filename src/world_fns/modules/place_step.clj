(ns world-fns.modules.place-step
  (:require [clojure.gdx :as gdx]
            [world-fns.modules.place-step.place-star :refer [place-module*]]))

(defn f
  [modules-tiled-map
   modules-scale
   scaled-grid
   unscaled-grid
   unscaled-floor-positions
   unscaled-transition-positions]
  (let [module-offset-tiles 1
        number-modules-x 8
        number-modules-y 4
        [modules-width modules-height] modules-scale
        props (gdx/tiled-map-get-properties modules-tiled-map)
        _ (assert (and (= (gdx/map-properties-get props "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (gdx/map-properties-get props "height")
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
