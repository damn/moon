(ns world-fns.uf-caves.last-steps
  (:require [clojure.grid2d.flood-fill :as flood-fill]
            [world-fns.uf-caves.position-tile-fn :as position-tile-fn]
            [clojure.grid2d.adjacent-wall-positions :as adjacent-wall-positions]
            [gdx.tiled-map.movement-property :as movement-property]
            [moon.grid2d :as g2d]
            [clojure.grid2d.printgrid :as printgrid]
            [clojure.grid2d.scalegrid :as scalegrid]
            [com.badlogic.gdx.maps.tiled.tiled-map.create :as create-tiled-map]
            [gdx.tiled-map.add-creatures-layer :as add-creatures-layer]))

(defn- scale-grid [grid start scale]
  (let [grid (scalegrid/f grid scale)]
    ;_ (printgrid/f grid)
    ;_ (println)
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

(defn- assoc-transition-cells [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (adjacent-wall-positions/f grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition}       (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    ;_ (printgrid/f grid)
    ;_ (println)
    grid))

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
  (let [{:keys [start-position grid]} (scale-grid grid start scaling)
        grid (assoc-transition-cells grid)
        position->tile (position-tile-fn/f grid)
        tiled-map (create-tiled-map/f
                   {:properties {"width"  (g2d/width  grid)
                                 "height" (g2d/height grid)
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
