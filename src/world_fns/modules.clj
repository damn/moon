(ns world-fns.modules
  (:require [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]
            moon.tiled-map
            [moon.grid2d :as g2d]
            [world-fns.modules.place :as place-module]
            [world-fns.modules.area-level-grid :as area-level-grid])
  (:import (java.util Random)))

(defn- print-grid [{:keys [grid] :as world-fn-ctx}]
  (g2d/printgrid grid)
  (println " - ")
  world-fn-ctx)

(defn- initial-grid
  [{:keys [initial-grid-fn
           grid2d-fix-nads-fn
           world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (initial-grid-fn (Random.) map-size map-size :wide)
        grid (grid2d-fix-nads-fn grid)]
    (assoc world-fn-ctx
           :start start
           :grid grid)))

(defn- assoc-transitions
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (g2d/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition} (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    (assoc world-fn-ctx :grid grid)))

(defn- create-scaled-grid [w]
  (assoc w :scaled-grid (g2d/scale-grid (:grid w) (:scale w))))

(defn- load-schema-tiled-map [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load! "maps/modules.tmx")))

(def copy-tile
  (memoize
   (fn [tile]
     (assert tile)
     (com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile. tile))))

(defn props->clj [props]
  (zipmap (.getKeys props)
          (.getValues props)))

(defn- grid->tiled-map
  [schema-tiled-map grid]
  {:properties (merge (props->clj (.getProperties schema-tiled-map))
                      {"width" (g2d/width grid)
                       "height" (g2d/height grid)})
   :layers (for [layer (.getLayers schema-tiled-map)]
             {:name (.getName layer)
              :visible? (.isVisible layer)
              :properties (props->clj (.getProperties layer))
              :tiles (for [position (g2d/posis grid)
                           :let [local-position (get grid position)]
                           :when local-position]
                       (when (vector? local-position)
                         (when-let [cell (.getCell layer (local-position 0) (local-position 1))]
                           [position (copy-tile (.getTile cell))])))})})

(defn- convert-to-tiled-map
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (moon.tiled-map/create-map (grid->tiled-map schema-tiled-map scaled-grid))))

(defn- calculate-start-position [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))

(defn- property-value [layer [x y] property-key]
  (if-let [cell (.getCell layer x y)]
    (if-let [value (.get (.getProperties (.getTile cell)) property-key)]
      value
      :undefined)
    :no-cell))

(defn- last-steps
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


        can-spawn? #(= "all" (moon.tiled-map/movement-property tiled-map %))

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
    (moon.tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position (get-free-position-in-area-level 0)
     :area-level-grid scaled-area-level-grid}))

(defn create
  [{:keys [world/map-size
           world/max-area-level
           steps]
    :as world-fn-ctx}]
  (assert (<= max-area-level map-size))
  (-> world-fn-ctx
      (assoc :scale [32 20])
      initial-grid
      #_print-grid
      assoc-transitions
      #_print-grid
      create-scaled-grid
      load-schema-tiled-map
      place-module/step
      convert-to-tiled-map
      calculate-start-position
      last-steps))
