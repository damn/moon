(ns clojure.levels.modules.last-steps
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.property-value :refer [property-value]]
            [moon.g2d :as g2d]
            [clojure.scale-grid :as scale-grid]
            [clojure.g2d.flood-fill :as flood-fill]
            [clojure.movement-property :as movement-property]
            [clojure.tiled-map.add-creatures-layer :as add-creatures-layer]
            [com.badlogic.gdx.maps.map-layers :as map-layers]))

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
        ;_ (g2d/print-y-up (reduce #(assoc %1 %2 nil) scaled-grid spawn-positions))
        ;_ (println "\n")
        {:keys [_steps area-level-grid]} (g2d/area-level-grid
                                          :grid grid
                                          :start start
                                          :max-level max-area-level
                                          :walk-on #{:ground :transition})
        ;_ (g2d/print-y-up area-level-grid)
        _ (assert (or
                   (= (set (concat [max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))
                   (= (set (concat [:wall max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))))
        scaled-area-level-grid (scale-grid/f area-level-grid scale)
        get-free-position-in-area-level (fn [area-level]
                                          (rand-nth
                                           (filter
                                            (fn [p]
                                              (and (= area-level (get scaled-area-level-grid p))
                                                   (#{:no-cell :undefined}
                                                    (property-value (map-layers/get (tiled-map/getLayers tiled-map) "creatures")
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
