(ns clojure.levels.uf-caves.last-steps
  (:require [clojure.g2d.flood-fill :as flood-fill]
            [clojure.position-tile-fn :as position-tile-fn]
            [clojure.levels.uf-caves.scale-grid :as scale-grid]
            [clojure.movement-property :as movement-property]
            [clojure.g2d.height :refer [->height]]
            [clojure.g2d.width :refer [->width]]
            [moon.g2d :as g2d]
            [clojure.g2d.printgrid :as printgrid]
            [clojure.tiled-tiled-map :as tiled-map]
            [clojure.tiled-map.add-creatures-layer :as add-creatures-layer]))

; TODO don't spawn my faction vampire w. player items ...
; FIXME - overlapping with player - don't spawn creatures on start position
(defn step
  [{:keys [level/grid
           level/start
           level/spawn-rate
           level/creature-properties
           level/create-tile
           level/tile-size
           level/scaling]
    :as lvlctx
    }]
  (assert (= #{:wall :ground} (set (g2d/cells grid))))
  (let [{:keys [start-position grid]} (scale-grid/f grid start scaling)
        grid (g2d/assoc-transition-cells grid)
        position->tile (position-tile-fn/f grid)
        tiled-map (tiled-map/f
                   {:properties {"width"  (->width  grid)
                                 "height" (->height grid)
                                 "tilewidth"  tile-size
                                 "tileheight" tile-size}
                    :layers [{:name "ground"
                              :visible? true
                              :properties {"movement-properties" true}
                              :tiles (for [position (g2d/posis grid)]
                                       [position (create-tile (position->tile position))])}]})

        can-spawn? #(= "all" (movement-property/f tiled-map %))
        _ (assert (can-spawn? start-position)) ; assuming hoping bottom left is movable
        level (inc (rand-int 6))
        creatures (filter #(= level (:creature/level %)) creature-properties)
        spawn-positions (flood-fill/f grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (<= (rand) spawn-rate)]
                    [position (rand-nth creatures)])]
    (add-creatures-layer/f tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))
