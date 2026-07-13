(ns moon.level.modules
  (:require [moon.rand :as rand]
            [gdx.tmx-map-loader :as tmx-map-loader]
            [gdx.tiled-map :as moon-tiled-map]
            [gdx.map-layers :as map-layers]
            [gdx.tiled-map-tile-layer :as tiled-map-tile-layer :refer [property-value]]
            [gdx.tiled-map-tile-layer-cell :as cell]
            [gdx.static-tiled-map-tile :as static-tiled-map-tile]
            [gdx.map-properties :as map-properties]
            [moon.caves :as caves]
            [moon.g2d :as g2d]
            [moon.position :as position]))

(defn print-grid [{:keys [grid] :as world-fn-ctx}]
  (g2d/print-y-up grid)
  (println " - ")
  world-fn-ctx)

(defn- initial-grid
  [{:keys [initial-grid-fn
           grid2d-fix-nads-fn
           world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (initial-grid-fn (rand/new-random) map-size map-size :wide)
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
  (assoc w :scaled-grid (g2d/scale-by (:grid w) (:scale w))))

(defn- load-schema-tiled-map [w]
  (assoc w :schema-tiled-map (tmx-map-loader/load-tiled-map "maps/modules.tmx")))

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
        _ (assert (and (= (moon-tiled-map/get-property modules-tiled-map "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (moon-tiled-map/get-property modules-tiled-map "height")
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

(defn- place-modules
  [{:keys [scale
           scaled-grid
           grid
           schema-tiled-map]
    :as w}]
  (assoc w :scaled-grid (place-step schema-tiled-map
                                    scale
                                    scaled-grid
                                    grid
                                    (filter #(= :ground (get grid %)) (g2d/posis grid))
                                    (filter #(= :transition (get grid %)) (g2d/posis grid)))))

(defn- grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (static-tiled-map-tile/create tile)))]
    {:properties (merge (map-properties/clojurize (moon-tiled-map/get-properties schema-tiled-map))
                        {"width" (g2d/width grid)
                         "height" (g2d/height grid)})
     :layers (for [layer (moon-tiled-map/get-layers schema-tiled-map)]
               {:name (tiled-map-tile-layer/get-name layer)
                :visible? (tiled-map-tile-layer/visible? layer)
                :properties (map-properties/clojurize (tiled-map-tile-layer/get-properties layer))
                :tiles (for [position (g2d/posis grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (tiled-map-tile-layer/get-cell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (cell/get-tile cell))])))})}))

(defn- convert-to-tiled-map
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (moon-tiled-map/create (grid->tiled-map schema-tiled-map scaled-grid))))

(defn- calculate-start [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))

(defn- last-steps
  [{:keys [world/max-area-level
           world/spawn-rate
           level/creature-properties
           grid
           start
           scale
           scaled-grid
           tiled-map
           start-position]}]
  (let [can-spawn? #(= "all" (moon-tiled-map/movement-property tiled-map %))
        _ (assert (can-spawn? start-position))
        spawn-positions (g2d/flood-fill scaled-grid start-position can-spawn?)
        {:keys [_steps area-level-grid]} (g2d/area-level-grid
                                          :grid grid
                                          :start start
                                          :max-level max-area-level
                                          :walk-on #{:ground :transition})
        _ (assert (or
                   (= (set (concat [max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))
                   (= (set (concat [:wall max-area-level] (range max-area-level)))
                      (set (g2d/cells area-level-grid)))))
        scaled-area-level-grid (g2d/scale-by area-level-grid scale)
        get-free-position-in-area-level (fn [area-level]
                                          (rand-nth
                                           (filter
                                            (fn [p]
                                              (and (= area-level (get scaled-area-level-grid p))
                                                   (#{:no-cell :undefined}
                                                    (property-value (map-layers/get (moon-tiled-map/get-layers tiled-map) "creatures")
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
    (moon-tiled-map/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position (get-free-position-in-area-level 0)
     :area-level-grid scaled-area-level-grid}))

(defn create
  [world-fn-ctx]
  (let [world-fn-ctx (merge {:initial-grid-fn caves/create
                             :grid2d-fix-nads-fn g2d/fix-nads
                             :world/map-size 5
                             :world/max-area-level 3
                             :world/spawn-rate 0.05}
                            world-fn-ctx)
        {:keys [world/map-size
                world/max-area-level]} world-fn-ctx]
    (assert (<= max-area-level map-size))
    (-> world-fn-ctx
        (assoc :scale [32 20])
        initial-grid
        #_print-grid
        assoc-transitions
        #_print-grid
        create-scaled-grid
        load-schema-tiled-map
        place-modules
        convert-to-tiled-map
        calculate-start
        last-steps)))
