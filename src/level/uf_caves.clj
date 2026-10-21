(ns level.uf-caves
  (:require [lib.g2d :as g2d]
            [lib.tiled-map :as moon-tiled-map]
            [lib.caves :as caves]
            [lib.position :as position])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.maps MapLayer MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMap TiledMapTile TiledMapTileLayer TiledMapTileLayer$Cell)
           (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)
           (java.util Random)))

(defn- scale-grid [grid start scale]
  (let [w (* (count grid) scale)
        h (* (count (grid 0)) scale)
        xyfn (fn [posi]
               (get-in grid (mapv #(int (/ % scale)) posi)))
        grid (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                                 (range h)))
                   (range w))]
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

(defn- get-rand-weighted-item
  "given a sequence of items and their weight, returns a weighted random item.
  for example {:a 5 :b 1} returns b only in about 1 of 6 cases"
  [weights]
  (let [result (rand-int (reduce + (map #(% 1) weights)))]
    (loop [r 0
           items weights]
      (let [[item weight] (first items)
            r (+ r weight)]
        (if (> r result)
          item
          (recur (int r) (rest items)))))))

(defn- position-tile-fn [grid]
  (let [uf-grounds (for [x [1 5]
                         y (range 5 11)
                         :when (not= [x y] [5 5])]
                     [x y])
        uf-walls (for [x [1]
                       y [13,16,19,22,25,28]]
                   [x y])
        transition? (fn [[x y]]
                      (= :ground (get-in grid [x (dec y)])))
        rand-0-3 (fn [] (get-rand-weighted-item {0 60 1 1 2 1 3 1}))
        rand-0-5 (fn [] (get-rand-weighted-item {0 30 1 1 2 1 3 1 4 1 5 1}))
        [ground-x ground-y] (rand-nth uf-grounds)
        {wall-x 0 wall-y 1} (rand-nth uf-walls)
        [transition-x transition-y] [wall-x (inc wall-y)]
        wall-tile (fn []
                    {:sprite-idx [(+ wall-x (rand-0-5)) wall-y]
                     :movement "none"})
        transition-tile (fn []
                          {:sprite-idx [(+ transition-x (rand-0-5))
                                        transition-y]
                           :movement "none"})
        ground-tile (fn []
                      {:sprite-idx [(+ ground-x (rand-0-3))
                                    ground-y]
                       :movement "all"})]
    (fn [position]
      (case (get-in grid position)
        :wall (wall-tile)
        :transition (if (transition? position)
                      (transition-tile)
                      (wall-tile))
        :ground (ground-tile)))))

(defn- create-tiled-map [grid tile-size create-tile position->tile]
  (let [properties {"width" (count grid)
                    "height" (count (grid 0))
                    "tilewidth" tile-size
                    "tileheight" tile-size}
        layers [{:name "ground"
                 :visible? true
                 :properties {"movement-properties" true}
                 :tiles (for [x (range (count grid))
                              y (range (count (grid 0)))
                              :let [position [x y]]]
                          [position (create-tile (position->tile position))])}]
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

(defn create
  [world-fn-ctx]
  (let [{:keys [level/creature-properties
                textures
                tile-size
                texture-path
                spawn-rate
                scaling
                cave-size
                cave-style]}
        (merge {:tile-size 48
                :texture-path "images/uf_terrain.png"
                :spawn-rate 0.02
                :scaling 3
                :cave-size 200
                :cave-style :wide}
               world-fn-ctx)
        {:keys [start grid]} (caves/create (Random.) cave-size cave-size cave-style)
        _ (assert (= #{:wall :ground} (set (apply concat grid))))
        grid (g2d/fix-nads grid)
        create-tile (let [texture (get textures texture-path)]
                      (memoize
                       (fn [& {:keys [sprite-idx movement]}]
                         {:pre [#{"all" "air" "none"} movement]}
                         (let [texture-region (TextureRegion. ^Texture texture
                                                              (int (* (sprite-idx 0) tile-size))
                                                              (int (* (sprite-idx 1) tile-size))
                                                              (int tile-size)
                                                              (int tile-size))
                               tile (StaticTiledMapTile. ^TextureRegion texture-region)]
                           (.put ^MapProperties (.getProperties ^StaticTiledMapTile tile)
                                 "movement" movement)
                           tile))))
        _ (assert (= #{:wall :ground} (set (apply concat grid))))
        {:keys [start-position grid]} (scale-grid grid start scaling)
        grid (let [grid (reduce #(assoc-in %1 %2 :transition) grid
                                (filter (fn [position]
                                          (and (= :wall (get-in grid position))
                                               (some #(= :ground (get-in grid %))
                                                     (position/get-8-neighbours position))))
                                        (for [x (range (count grid)) y (range (count (grid 0)))] [x y])))]
               (assert (or
                        (= #{:wall :ground :transition} (set (apply concat grid)))
                        (= #{:ground :transition} (set (apply concat grid))))
                       (str "(set (apply concat grid)): " (set (apply concat grid))))
               grid)
        position->tile (position-tile-fn grid)
        tiled-map (create-tiled-map grid tile-size create-tile position->tile)
        can-spawn? #(= "all" (moon-tiled-map/movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        level (inc (rand-int 6))
        creatures (filter #(= level (:creature/level %)) creature-properties)
        spawn-positions (g2d/flood-fill grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (and (not= position start-position)
                                   (<= (rand) spawn-rate))]
                    [position (rand-nth creatures)])]
    (moon-tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))
