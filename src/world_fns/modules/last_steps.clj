(ns world-fns.modules.last-steps
  (:require [tiled-map-tile-layer.property-value :refer [property-value]]
            [grid2d.cells :refer [->cells]]
            [grid2d.scale-grid :as scale-grid]
            [grid2d.printgrid :as printgrid]
            [grid2d.flood-fill :as flood-fill]
            [tiled-map.movement-property :as movement-property]
            [tiled-map.add-creatures-layer :as add-creatures-layer]
            [world-fns.modules.area-level-grid :as area-level-grid])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn step
  [{:keys [world/max-area-level
           world/spawn-rate
           level/creature-properties
           grid
           start
           scale
           scaled-grid
           tiled-map
           start-position]}]
  (let [can-spawn? #(= "all" (movement-property/f tiled-map %))
        _ (assert (can-spawn? start-position)) ; assuming hoping bottom left is movable
        spawn-positions (flood-fill/f scaled-grid start-position can-spawn?)
        ;_ (println "scaled grid with filled nil: '?' \n")
        ;_ (printgrid/f (reduce #(assoc %1 %2 nil) scaled-grid spawn-positions))
        ;_ (println "\n")
        {:keys [_steps area-level-grid]} (area-level-grid/create
                                          :grid grid
                                          :start start
                                          :max-level max-area-level
                                          :walk-on #{:ground :transition})
        ;_ (printgrid/f area-level-grid)
        _ (assert (or
                   (= (set (concat [max-area-level] (range max-area-level)))
                      (set (->cells area-level-grid)))
                   (= (set (concat [:wall max-area-level] (range max-area-level)))
                      (set (->cells area-level-grid)))))
        scaled-area-level-grid (scale-grid/f area-level-grid scale)
        get-free-position-in-area-level (fn [area-level]
                                          (rand-nth
                                           (filter
                                            (fn [p]
                                              (and (= area-level (get scaled-area-level-grid p))
                                                   (#{:no-cell :undefined}
                                                    (property-value (MapLayers/.get (.getLayers tiled-map) "creatures")
                                                                    p
                                                                    "id"))))
                                            spawn-positions)))
        creatures (for [position spawn-positions
                        :let [area-level (get scaled-area-level-grid position)
                              creatures (filter #(= area-level (:creature/level %))
                                                creature-properties)]
                        :when (and (number? area-level)
                                   (<= (rand) spawn-rate)
                                   (seq creatures))]
                    [position (rand-nth creatures)])]
    (add-creatures-layer/f tiled-map creatures)
    {:tiled-map tiled-map
     :start-position (get-free-position-in-area-level 0)
     :area-level-grid scaled-area-level-grid}))
