(ns moon.modules.last-steps
  (:require [clojure.grid2d :as g2d]
            [moon.area-level-grid :as area-level-grid]
            [moon.tiled-map :as tiled-map]
            [moon.world-fns.creature-layer :as creature-layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)))

(defn- property-value [^TiledMapTileLayer layer [x y] property-key]
  (if-let [cell (.getCell layer x y)]
    (if-let [value (.get (.getProperties (.getTile cell)) property-key)]
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
           ^TiledMap tiled-map
           start-position
           ]}]
  (let [


        can-spawn? #(= "all" (tiled-map/movement-property tiled-map %))

        _ (assert (can-spawn? start-position)) ; assuming hoping bottom left is movable

        spawn-positions (g2d/flood-fill scaled-grid start-position can-spawn?)
        ;_ (println "scaled grid with filled nil: '?' \n")
        ;_ (printgrid (reduce #(assoc %1 %2 nil) scaled-grid spawn-positions))
        ;_ (println "\n")

        {:keys [_steps area-level-grid]} (area-level-grid/create
                                          :grid grid
                                          :start start
                                          :max-level max-area-level
                                          :walk-on #{:ground :transition})
        ;_ (printgrid area-level-grid)
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
                                                    (property-value (.get (.getLayers tiled-map) "creatures")
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
    (creature-layer/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position (get-free-position-in-area-level 0)
     :area-level-grid scaled-area-level-grid}))
