(ns world-fns.modules.last-steps
  (:require [com.badlogic.gdx.maps.map-layers :as layers]
            [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tile]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [moon.grid2d :as g2d]
            [clojure.grid2d.printgrid :as printgrid]
            [clojure.grid2d.flood-fill :as flood-fill]
            [gdx.tiled-map.movement-property :as movement-property]
            [gdx.tiled-map.add-creatures-layer :as add-creatures-layer]
            [world-fns.modules.area-level-grid :as area-level-grid]))

(defn- property-value [layer xy property-key]
  (if-let [cell (layer/cell layer xy)]
    (if-let [value (props/get (tile/properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))

(defn step
  [{:keys [
           world/max-area-level
           world/spawn-rate
           level/creature-properties
           grid
           start
           scale
           scaled-grid
           tiled-map
           start-position
           ]}]
  (let [


        can-spawn? #(= "all" (movement-property/f tiled-map %))

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
                      (set (g2d/cells area-level-grid)))
                   (= (set (concat [:wall max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))))

        scaled-area-level-grid (g2d/scale-grid area-level-grid scale)

        get-free-position-in-area-level (fn [area-level]
                                          (rand-nth
                                           (filter
                                            (fn [p]
                                              (and (= area-level (get scaled-area-level-grid p))
                                                   (#{:no-cell :undefined}
                                                    (property-value (layers/get (tiled-map/layers tiled-map) "creatures")
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
