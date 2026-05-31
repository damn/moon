(ns world-fns.modules.place-step
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [world-fns.utils.transitions :as transitions]
            [world-fns.modules.place-step.module-index-tiled-map-positions :refer [module-index->tiled-map-positions]]))

(def ^:private floor-modules-row-width 4)
(def ^:private floor-modules-row-height 4)

(defn- floor->module-index []
  [(rand-int floor-modules-row-width)
   (rand-int floor-modules-row-height)])

(def ^:private transition-modules-row-width 4)
(def ^:private transition-modules-row-height 4)
(def ^:private transition-modules-offset-x 4)

(defn- transition-idxvalue->module-index [idxvalue]
  [(+ (rem idxvalue transition-modules-row-width)
      transition-modules-offset-x)
   (int (/ idxvalue transition-modules-row-height))])

(def ^:private module-offset-tiles 1)
(def ^:private floor-idxvalue 0)

(defn- place-module* [modules-scale
                      scaled-grid
                      unscaled-position
                      & {:keys [transition?
                                transition-neighbor?]}]
  (let [[modules-width modules-height] modules-scale
        idxvalue (if transition?
                   (transitions/idx-value unscaled-position transition-neighbor?)
                   floor-idxvalue)
        tiled-map-positions (module-index->tiled-map-positions
                             (if transition?
                               (transition-idxvalue->module-index idxvalue)
                               (floor->module-index))
                             modules-scale
                             module-offset-tiles)
        offsets (for [x (range modules-width)
                      y (range modules-height)]
                  [x y])
        offset->tiled-map-position (zipmap offsets tiled-map-positions)
        scaled-position (mapv * unscaled-position modules-scale)]
    (reduce (fn [grid offset]
              (assoc grid
                     (mapv + scaled-position offset)
                     (offset->tiled-map-position offset)))
            scaled-grid
            offsets)))

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
        _ (assert (and (= (props/get (tiled-map/props modules-tiled-map) "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (props/get (tiled-map/props modules-tiled-map) "height")
                          (* number-modules-y (+ modules-height module-offset-tiles)))))
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? false))
                            scaled-grid
                            unscaled-floor-positions)
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? true
                                             :transition-neighbor? #(#{:transition :wall}
                                                                     (get unscaled-grid %))))
                            scaled-grid
                            unscaled-transition-positions)]
    scaled-grid))
