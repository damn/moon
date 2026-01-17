(ns moon.create.world
  (:require [clojure.grid2d :as g2d]
            [clojure.math.raycaster :as raycaster]
            [clojure.math.vector2 :as v]
            [moon.db :as db]
            [moon.textures :as textures]
            [moon.utils :as utils]
            [moon.world :as world]
            [moon.world.create.grid]
            [moon.world.content-grid :as content-grid]
            [moon.world.grid.cell :as cell]
            [moon.world.tiled :as tiled]
            [moon.world-fns.creature-tiles]))

(defn- create-double-ray-endpositions
  [[start-x start-y]
   [target-x target-y]
   path-w]
  {:pre [(< path-w 0.98)]} ; wieso 0.98??
  (let [path-w (+ path-w 0.02) ;etwas gr�sser damit z.b. projektil nicht an ecken anst�sst
        v (v/direction [start-x start-y]
                       [target-y target-y])
        [normal1 normal2] (v/normal-vectors v)
        normal1 (v/scale normal1 (/ path-w 2))
        normal2 (v/scale normal2 (/ path-w 2))
        start1  (v/add [start-x  start-y]  normal1)
        start2  (v/add [start-x  start-y]  normal2)
        target1 (v/add [target-x target-y] normal1)
        target2 (v/add [target-x target-y] normal2)]
    [start1,target1,start2,target2]))

(defn- create-world-grid [width height cell-movement]
  (g2d/create-grid width
                   height
                   (fn [position]
                     (atom (cell/create position (cell-movement position))))))

(defn- update-entity! [{:keys [grid cell-w cell-h]} eid]
  (let [{:keys [moon.content-grid/content-cell
                entity/body]} @eid
        [x y] (:body/position body)
        new-cell (get grid [(int (/ x cell-w))
                            (int (/ y cell-h))])]
    (when-not (= content-cell new-cell)
      (swap! new-cell update :entities conj eid)
      (swap! eid assoc :moon.content-grid/content-cell new-cell)
      (when content-cell
        (swap! content-cell update :entities disj eid)))))

(defrecord ContentGrid []
  content-grid/ContentGrid
  (add-entity! [this eid]
    (update-entity! this eid))

  (remove-entity! [_ eid]
    (-> @eid
        :moon.content-grid/content-cell
        (swap! update :entities disj eid)))

  (position-changed! [this eid]
    (update-entity! this eid))

  (active-entities [{:keys [grid]} center-entity]
    (->> (let [idx (-> center-entity
                       :moon.content-grid/content-cell
                       deref
                       :idx)]
           (cons idx (g2d/get-8-neighbour-positions idx)))
         (keep grid)
         (mapcat (comp :entities deref)))))

(defn- create-content-grid [width height cell-size]
  (map->ContentGrid
   {:grid (g2d/create-grid
           (inc (int (/ width  cell-size)))
           (inc (int (/ height cell-size)))
           (fn [idx]
             (atom {:idx idx,
                    :entities #{}})))
    :cell-w cell-size
    :cell-h cell-size}))

(defn- create-explored-tile-corners [width height]
  (atom (g2d/create-grid width height (constantly false))))

(defn- create-raycaster [g2d]
  (let [width  (g2d/width  g2d)
        height (g2d/height g2d)
        cells  (for [cell (map deref (g2d/cells g2d))]
                 [(:position cell)
                  (boolean (cell/blocks-vision? cell))])]
    (let [arr (make-array Boolean/TYPE width height)]
      (doseq [[[x y] blocked?] cells]
        (aset arr x y (boolean blocked?)))
      [arr width height])))

(defrecord RWorld []
  moon.world/World
  (dispose! [{:keys [world/tiled-map]}]
    (assert tiled-map) ; only dispose after world was created
    (.dispose tiled-map))

  (blocked? [{:keys [world/raycaster]} start target]
    (raycaster/blocked? raycaster start target))

  (path-blocked? [{:keys [world/raycaster]} start target path-w]
    (let [[start1,target1,start2,target2] (create-double-ray-endpositions start target path-w)]
      (or
       (raycaster/blocked? raycaster start1 target1)
       (raycaster/blocked? raycaster start2 target2))))

  (line-of-sight? [{:keys [world/raycaster]} source target]
    (not (raycaster/blocked? raycaster
                             (:body/position (:entity/body source))
                             (:body/position (:entity/body target))))))

(defn- assoc-state [world {:keys [tiled-map
                                  start-position]}]
  (let [width  (.get (.getProperties tiled-map) "width")
        height (.get (.getProperties tiled-map) "height")
        grid (create-world-grid width height
                                #(case (tiled/movement-property tiled-map %)
                                   "none" :none
                                   "air"  :air
                                   "all"  :all))]
    (assoc world
           :world/tiled-map tiled-map
           :world/start-position start-position
           :world/grid grid
           :world/content-grid (create-content-grid width height (:content-grid-cell-size world))
           :world/explored-tile-corners (create-explored-tile-corners width height)
           :world/raycaster (create-raycaster grid)
           :world/potential-field-cache (atom nil)
           :world/id-counter (atom 0)
           :world/entity-ids (atom {}))))

(defn- calculate-max-speed
  [{:keys [world/minimum-size
           world/max-delta]
    :as world}]
  (assoc world :world/max-speed (/ minimum-size max-delta)))

(defn- define-render-z-order
  [{:keys [world/z-orders]
    :as world}]
  (assoc world :world/render-z-order (utils/define-order z-orders)))

(def ^:private world-params
  {
   :content-grid-cell-size 16
   :world/factions-iterations {:good 15 :evil 5}
   :world/max-delta 0.04
   :world/minimum-size 0.39
   :world/z-orders [:z-order/on-ground
                    :z-order/ground
                    :z-order/flying
                    :z-order/effect]
   })

(defn step
  [{:keys [ctx/db
           ctx/textures]
    :as ctx}
   world-fn]
  (let [[f params] world-fn
        world-fn-result (f
                         (assoc params
                                :level/creature-properties (moon.world-fns.creature-tiles/prepare
                                                            (db/all-raw db :properties/creatures)
                                                            #(textures/texture-region textures %))
                                :textures textures))]
    (assoc ctx :ctx/world (-> (merge (map->RWorld {}) world-params)
                              calculate-max-speed
                              define-render-z-order
                              (assoc-state world-fn-result)))))
