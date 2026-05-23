(ns game.world-fns.uf-caves
  (:require [clojure.rand :as rand]
            [moon.tiled-map]
            [clojure.tiled-map :as tiled-map]
            [clojure.graphics.texture :as texture]
            [moon.grid2d :as g2d]))

(defn- assoc-transition-cells [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (g2d/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition}       (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    ;_ (printgrid grid)
    ;_ (println)
    grid))

(defn- scale-grid [grid start scale]
  (let [grid (g2d/scalegrid grid scale)]
    ;_ (printgrid grid)
    ;_ (println)
    {:start-position (mapv #(* % scale) start)
     :grid grid}))

(defn- position->tile-fn [grid]
  (let [uf-grounds (for [x [1 5]
                         y (range 5 11)
                         :when (not= [x y] [5 5])] ; wooden
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


; TODO don't spawn my faction vampire w. player items ...
; FIXME - overlapping with player - don't spawn creatures on start position
(defn- last-steps
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
  (let [
        {:keys [start-position grid]} (scale-grid grid start scaling)

        grid (assoc-transition-cells grid)

        position->tile (position->tile-fn grid)
        tiled-map (moon.tiled-map/create-map
                   {:properties {"width"  (g2d/width  grid)
                                 "height" (g2d/height grid)
                                 "tilewidth"  tile-size
                                 "tileheight" tile-size}
                    :layers [{:name "ground"
                              :visible? true
                              :properties {"movement-properties" true}
                              :tiles (for [position (g2d/posis grid)]
                                       [position (create-tile (position->tile position))])}]})

        can-spawn? #(= "all" (moon.tiled-map/movement-property tiled-map %))
        _ (assert (can-spawn? start-position)) ; assuming hoping bottom left is movable
        level (inc (rand-int 6))
        creatures (filter #(= level (:creature/level %)) creature-properties)
        spawn-positions (g2d/flood-fill grid start-position can-spawn?)
        creatures (for [position spawn-positions
                        :when (<= (rand) spawn-rate)]
                    [position (rand-nth creatures)])]
    (moon.tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position start-position}))

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

(defn create
  [{:keys [initial-grid-create-fn
           grid2d-fix-nads-fn
           level/creature-properties
           textures
           tile-size
           texture-path
           spawn-rate
           scaling
           cave-size
           cave-style]}]
  (reduce (fn [m f]
            (f m))
          {:initial-grid-create-fn initial-grid-create-fn
           :grid2d-fix-nads-fn grid2d-fix-nads-fn
           :size cave-size
           :cave-style cave-style
           :random (java.util.Random.)
           :level/tile-size tile-size
           :level/create-tile (let [texture (get textures texture-path)]
                                (memoize
                                 (fn [& {:keys [sprite-idx movement]}]
                                   {:pre [#{"all" "air" "none"} movement]}
                                   (moon.tiled-map/create-tile
                                    (texture/region texture
                                                    (* (sprite-idx 0) tile-size)
                                                    (* (sprite-idx 1) tile-size)
                                                    tile-size
                                                    tile-size)
                                    "movement" movement))))
           :level/spawn-rate spawn-rate
           :level/scaling scaling
           :level/creature-properties creature-properties}
          [initial-grid
           fix-nads
           last-steps]))
