(ns moon.level.uf-caves
  (:require [moon.movement-property :as movement-property]
            [moon.rand :as rand]
            [moon.tiled-map :as moon-tiled-map]
            [moon.tiled-map.create :as tiled-tiled-map]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [moon.map-properties :as map-properties]
            [moon.caves :as caves]
            [moon.g2d :as g2d]))

(defn- initial-grid
  [{:keys [initial-grid-create-fn
           size
           cave-style
           random]
    :as level}]
  (let [{:keys [start grid]} (initial-grid-create-fn random size size cave-style)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    (assoc level
           :level/start start
           :level/grid grid)))

(defn- fix-nads
  [{:keys [level/grid]
    :as level}]
  (let [grid ((:grid2d-fix-nads-fn level) grid)]
    (assoc level :level/grid grid)))

(defn- scale-grid [grid start scale]
  (let [grid (g2d/scale-uniform grid scale)]
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

(defn- position-tile-fn [grid]
  (let [uf-grounds (for [x [1 5]
                         y (range 5 11)
                         :when (not= [x y] [5 5])]
                     [x y])
        uf-walls (for [x [1]
                       y [13,16,19,22,25,28]]
                   [x y])
        transition? (fn [[x y]]
                      (= :ground (get grid [x (dec y)])))
        rand-0-3 (fn [] (rand/get-rand-weighted-item {0 60 1 1 2 1 3 1}))
        rand-0-5 (fn [] (rand/get-rand-weighted-item {0 30 1 1 2 1 3 1 4 1 5 1}))
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
      (case (get grid position)
        :wall (wall-tile)
        :transition (if (transition? position)
                      (transition-tile)
                      (wall-tile))
        :ground (ground-tile)))))

(defn- last-steps
  [{:keys [level/grid
           level/start
           level/spawn-rate
           level/creature-properties
           level/create-tile
           level/tile-size
           level/scaling]
    :as lvlctx}]
  (assert (= #{:wall :ground} (set (g2d/cells grid))))
  (let [{:keys [start-position grid]} (scale-grid grid start scaling)
        grid (g2d/assoc-transition-cells grid)
        position->tile (position-tile-fn grid)
        tiled-map (tiled-tiled-map/f
                   {:properties {"width" (g2d/width grid)
                                 "height" (g2d/height grid)
                                 "tilewidth" tile-size
                                 "tileheight" tile-size}
                    :layers [{:name "ground"
                              :visible? true
                              :properties {"movement-properties" true}
                              :tiles (for [position (g2d/posis grid)]
                                       [position (create-tile (position->tile position))])}]})
        can-spawn? #(= "all" (movement-property/f tiled-map %))
        _ (assert (can-spawn? start-position))
        level (inc (rand-int 6))
        creatures (filter #(= level (:creature/level %)) creature-properties)
        spawn-positions (g2d/flood-fill grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (<= (rand) spawn-rate)]
                    [position (rand-nth creatures)])]
    (moon-tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))

(defn create
  [world-fn-ctx]
  (let [{:keys [initial-grid-create-fn
                grid2d-fix-nads-fn
                level/creature-properties
                textures
                tile-size
                texture-path
                spawn-rate
                scaling
                cave-size
                cave-style]}
        (merge {:initial-grid-create-fn caves/create
                :grid2d-fix-nads-fn g2d/fix-nads
                :tile-size 48
                :texture-path "images/uf_terrain.png"
                :spawn-rate 0.02
                :scaling 3
                :cave-size 200
                :cave-style :wide}
               world-fn-ctx)]
    (reduce (fn [m f]
              (f m))
            {:initial-grid-create-fn initial-grid-create-fn
             :grid2d-fix-nads-fn grid2d-fix-nads-fn
             :size cave-size
             :cave-style cave-style
             :random (rand/new-random)
             :level/tile-size tile-size
             :level/create-tile (let [texture (get textures texture-path)]
                                  (memoize
                                   (fn [& {:keys [sprite-idx movement]}]
                                     {:pre [#{"all" "air" "none"} movement]}
                                     (let [texture-region (texture-region/new texture
                                                                              (* (sprite-idx 0) tile-size)
                                                                              (* (sprite-idx 1) tile-size)
                                                                              tile-size
                                                                              tile-size)
                                           tile (static-tiled-map-tile/new texture-region)]
                                       (map-properties/put! (static-tiled-map-tile/getProperties tile)
                                                            "movement" movement)
                                       tile))))
             :level/spawn-rate spawn-rate
             :level/scaling scaling
             :level/creature-properties creature-properties}
            [initial-grid
             fix-nads
             last-steps])))
