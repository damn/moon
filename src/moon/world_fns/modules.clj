(ns moon.world-fns.modules
  (:require [clojure.grid2d :as g2d]
            [moon.maps.map-layers :as layers]
            [moon.maps.map-properties :as props]
            [moon.maps.tiled :as tiled-map]
            [moon.maps.tiled.layer :as layer]
            [moon.maps.tiled.layer.cell :as cell]
            [moon.maps.tiled.tiled-map-tile :as tile]
            [moon.maps.tiled.tmx :as tmx]
            [moon.world-fns.area-level-grid :as area-level-grid]
            [moon.world-fns.caves :as caves]
            [moon.world-fns.creature-layer :as creature-layer]
            [moon.world-fns.nads :as nads]
            [moon.world-fns.utils :as helper]
            [moon.world.tiled :as tiled]))

(defn- property-value [layer position property-key]
  (if-let [cell (layer/cell layer position)]
    (if-let [value (props/get (tile/properties (cell/tile cell)) property-key)]
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


        can-spawn? #(= "all" (tiled/movement-property tiled-map %))

        _ (assert (can-spawn? start-position)) ; assuming hoping bottom left is movable

        spawn-positions (helper/flood-fill scaled-grid start-position can-spawn?)
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

        scaled-area-level-grid (helper/scale-grid area-level-grid scale)

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
    (creature-layer/add-creatures-layer! tiled-map creatures)
    {:tiled-map tiled-map
     :start-position (get-free-position-in-area-level 0)
     :area-level-grid scaled-area-level-grid}))

; TODO properties->clj step probably not needed
(defn- grid->tiled-map
  "Creates an empty new tiled-map with same layers and properties as schema-tiled-map.
  The size of the map is as of the grid, which contains also the tile information from the schema-tiled-map."
  [schema-tiled-map grid]
  (tiled/create-tiled-map
   {:properties (merge (props/->clj (tiled-map/properties schema-tiled-map))
                       {"width" (g2d/width grid)
                        "height" (g2d/height grid)})
    :layers (for [layer (tiled-map/layers schema-tiled-map)]
              {:name (layer/name layer)
               :visible? (layer/visible? layer)
               :properties (props/->clj (layer/properties layer))
               :tiles (for [position (g2d/posis grid)
                            :let [local-position (get grid position)]
                            :when local-position]
                        (when (vector? local-position)
                          (when-let [cell (layer/cell layer local-position)]
                            [position (tiled/copy-tile (cell/tile cell))])))})}))

(defn- convert-to-tiled-map
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (grid->tiled-map schema-tiled-map scaled-grid)))

(def ^:private number-modules-x 8)
(def ^:private number-modules-y 4)
(def ^:private module-offset-tiles 1)
(def ^:private transition-modules-row-width 4)
(def ^:private transition-modules-row-height 4)
(def ^:private transition-modules-offset-x 4)
(def ^:private floor-modules-row-width 4)
(def ^:private floor-modules-row-height 4)
(def ^:private floor-idxvalue 0)

(defn- module-index->tiled-map-positions
  [[module-x module-y]
   [modules-width modules-height]]
  (let [start-x (* module-x (+ modules-width  module-offset-tiles))
        start-y (* module-y (+ modules-height module-offset-tiles))]
    (for [x (range start-x (+ start-x modules-width))
          y (range start-y (+ start-y modules-height))]
      [x y])))

(defn- floor->module-index []
  [(rand-int floor-modules-row-width)
   (rand-int floor-modules-row-height)])

(defn- transition-idxvalue->module-index [idxvalue]
  [(+ (rem idxvalue transition-modules-row-width)
      transition-modules-offset-x)
   (int (/ idxvalue transition-modules-row-height))])

(defn- place-module* [modules-scale
                      scaled-grid
                      unscaled-position
                      & {:keys [transition?
                                transition-neighbor?]}]
  (let [[modules-width modules-height] modules-scale
        idxvalue (if transition?
                   (helper/transition-idx-value unscaled-position transition-neighbor?)
                   floor-idxvalue)
        tiled-map-positions (module-index->tiled-map-positions
                             (if transition?
                               (transition-idxvalue->module-index idxvalue)
                               (floor->module-index))
                             modules-scale)
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

(defn- place-modules*
  [modules-tiled-map
   modules-scale
   scaled-grid
   unscaled-grid
   unscaled-floor-positions
   unscaled-transition-positions]
  (let [[modules-width modules-height] modules-scale
        _ (assert (and (= (props/get (tiled-map/properties modules-tiled-map) "width")
                          (* number-modules-x (+ modules-width module-offset-tiles)))
                       (= (props/get (tiled-map/properties modules-tiled-map) "height")
                          (* number-modules-y (+ modules-height module-offset-tiles)))))
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* modules-scale
                                             scaled-grid
                                             unscaled-position
                                             :transition? false))
                            scaled-grid
                            unscaled-floor-positions)
        scaled-grid (reduce (fn [scaled-grid unscaled-position]
                              (place-module* modules-scale
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
  (assoc w :scaled-grid (place-modules* schema-tiled-map
                                       scale
                                       scaled-grid
                                       grid
                                       (filter #(= :ground     (get grid %)) (g2d/posis grid))
                                       (filter #(= :transition (get grid %)) (g2d/posis grid)))))

(defn- create-scaled-grid [w]
  (assoc w :scaled-grid (helper/scale-grid (:grid w) (:scale w))))

(defn- assoc-transitions
  [{:keys [grid] :as world-fn-ctx}]
  (let [grid (reduce #(assoc %1 %2 :transition)
                     grid
                     (helper/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition} (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    (assoc world-fn-ctx :grid grid)))

(defn- print-grid! [{:keys [grid] :as world-fn-ctx}]
  (helper/printgrid grid)
  (println " - ")
  world-fn-ctx)

(defn- cave-grid [& {:keys [size]}]
  (let [{:keys [start grid]} (caves/create (java.util.Random.) size size :wide)
        grid (nads/fix-nads grid)]
    (assert (= #{:wall :ground} (set (g2d/cells grid))))
    {:start start
     :grid grid}))

(defn- create-initial-grid
  [{:keys [world/map-size]
    :as world-fn-ctx}]
  (let [{:keys [start grid]} (cave-grid :size map-size)]
    (assoc world-fn-ctx
           :start start
           :grid grid)))

(defn- calculate-start-position [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))

(defn- load-schema-tiled-map [w]
  (assoc w :schema-tiled-map (tmx/load-map "maps/modules.tmx")))

(defn create
  [{:keys [world/map-size
           world/max-area-level]
    :as world-fn-ctx}]
  (assert (<= max-area-level map-size))
  (reduce (fn [ctx f]
            (f ctx))
          (assoc world-fn-ctx :scale [32 20])
          [create-initial-grid
           print-grid!
           assoc-transitions
           print-grid!
           create-scaled-grid
           load-schema-tiled-map
           place-modules
           convert-to-tiled-map
           calculate-start-position
           last-steps]))
