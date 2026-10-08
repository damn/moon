(ns moon.level.modules
  (:require [g2d.fix-nads :refer [fix-nads]]
            [g2d.flood-fill :refer [flood-fill]]
            [moon.rand :as rand]
            [moon.tiled-map :as moon-tiled-map]
            [moon.caves :as caves]
            [moon.g2d :as g2d]
            [moon.position :as position])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps MapLayer MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap TiledMapTile TiledMapTileLayer TiledMapTileLayer$Cell TmxMapLoader)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn- module-index->tiled-map-positions
  [[module-x module-y]
   [modules-width modules-height]
   module-offset-tiles]
  (let [start-x (* module-x (+ modules-width module-offset-tiles))
        start-y (* module-y (+ modules-height module-offset-tiles))]
    (for [x (range start-x (+ start-x modules-width))
          y (range start-y (+ start-y modules-height))]
      [x y])))

(def ^:private transition-idxvalues [2 8 1 4])

(defn- transition-idx-value [position position->transition?]
  (->> position
       position/get-4-neighbours
       (map-indexed (fn [idx position]
                      (if (position->transition? position)
                        (transition-idxvalues idx)
                        0)))
       (apply +)))

(defn- place-module*
  [module-offset-tiles
   modules-scale
   scaled-grid
   unscaled-position
   & {:keys [transition?
             transition-neighbor?]}]
  (let [floor-modules-row-width 4
        floor-modules-row-height 4
        floor->module-index (fn []
                              [(rand-int floor-modules-row-width)
                               (rand-int floor-modules-row-height)])
        transition-modules-row-width 4
        transition-modules-row-height 4
        transition-modules-offset-x 4
        transition-idxvalue->module-index (fn [idxvalue]
                                            [(+ (rem idxvalue transition-modules-row-width)
                                                transition-modules-offset-x)
                                             (int (/ idxvalue transition-modules-row-height))])
        [modules-width modules-height] modules-scale
        floor-idxvalue 0
        idxvalue (if transition?
                   (transition-idx-value unscaled-position transition-neighbor?)
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

(defn- place-step
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
        props (.getProperties ^TiledMap modules-tiled-map)
        _ (assert (and (= (.get props "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (.get props "height")
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

(defn- grid->tiled-map
  [^TiledMap schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (if (instance? StaticTiledMapTile tile)
                       (StaticTiledMapTile. ^StaticTiledMapTile tile)
                       (StaticTiledMapTile. ^TextureRegion tile))))]
    {:properties (merge (let [props (.getProperties schema-tiled-map)]
                          (zipmap (.getKeys props)
                                  (.getValues props)))
                        {"width" (g2d/width grid)
                         "height" (g2d/height grid)})
     :layers (for [layer (.getLayers schema-tiled-map)]
               {:name (.getName ^TiledMapTileLayer layer)
                :visible? (.isVisible ^TiledMapTileLayer layer)
                :properties (let [props (.getProperties ^TiledMapTileLayer layer)]
                              (zipmap (.getKeys ^MapProperties props)
                                      (.getValues ^MapProperties props)))
                :tiles (for [position (g2d/posis grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (.getCell ^TiledMapTileLayer layer (int (local-position 0)) (int (local-position 1)))]
                             [position (copy-tile (.getTile ^TiledMapTileLayer$Cell cell))])))})}))

(defn- create-tiled-map [schema-tiled-map scaled-grid]
  (let [{:keys [properties layers]} (grid->tiled-map schema-tiled-map scaled-grid)
        tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (.put (.getProperties tiled-map) k v))
    (doseq [{:keys [name visible? properties tiles]} layers]
      (assert (string? name))
      (assert (boolean? visible?))
      (let [props (.getProperties tiled-map)
            ^TiledMapTileLayer layer (doto (TiledMapTileLayer. (int (.get props "width"))
                                                               (int (.get props "height"))
                                                               (int (.get props "tilewidth"))
                                                               (int (.get props "tileheight")))
                                       (.setName ^String name)
                                       (.setVisible visible?))]
        (doseq [[k v] properties]
          (assert (string? k))
          (.put ^MapProperties (.getProperties layer) k v))
        (doseq [[[x y] tile] tiles
                :when tile]
          (.setCell layer (int x) (int y)
                    (doto (TiledMapTileLayer$Cell.)
                      (.setTile ^TiledMapTile tile))))
        (.add (.getLayers tiled-map) ^MapLayer layer)))
    tiled-map))

(defn- last-steps
  [spawn-rate creature-properties scaled-grid tiled-map start-position]
  (let [^TiledMap tiled-map tiled-map
        can-spawn? #(= "all" (moon-tiled-map/movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        spawn-positions (flood-fill scaled-grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (and (not= position start-position)
                                   (<= (rand) spawn-rate)
                                   (seq creature-properties))]
                    [position (rand-nth creature-properties)])]
    (moon-tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))

(defn create
  [{:keys [level/creature-properties
           world/map-size
           world/spawn-rate]
    :or {map-size 5
         spawn-rate 0.05}}]
  (let [scale [32 20]
        {:keys [start grid]} (caves/create (rand/new-random) map-size map-size :wide)
        grid (fix-nads grid)
        grid (let [grid (reduce #(assoc %1 %2 :transition)
                                grid
                                (filter (fn [position]
                                          (and (= :wall (get grid position))
                                               (some #(= :ground (get grid %))
                                                     (position/get-8-neighbours position))))
                                        (g2d/posis grid)))]
               (assert (or
                        (= #{:wall :ground :transition} (set (g2d/cells grid)))
                        (= #{:ground :transition} (set (g2d/cells grid))))
                       (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
               grid)
        scaled-grid (g2d/scale-by grid scale)
        schema-tiled-map (.load (TmxMapLoader.) "maps/modules.tmx")
        scaled-grid (place-step schema-tiled-map
                                scale
                                scaled-grid
                                grid
                                (filter #(= :ground (get grid %)) (g2d/posis grid))
                                (filter #(= :transition (get grid %)) (g2d/posis grid)))
        tiled-map (create-tiled-map schema-tiled-map scaled-grid)
        start-position (mapv * start scale)]
    (last-steps spawn-rate
                creature-properties
                scaled-grid
                tiled-map
                start-position)))
