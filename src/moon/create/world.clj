(ns moon.create.world
  (:require [clojure.grid2d :as g2d]
            [moon.db :as db]
            [moon.textures :as textures]
            [moon.utils :as utils]
            [moon.world.create.grid]
            [moon.content-grid :as content-grid]
            [moon.cell :as cell]
            [moon.tiled-map :as tiled-map]
            [moon.world-fns.creature-tiles]))

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

(defn- assoc-state [ctx {:keys [tiled-map
                                  start-position]}]
  (let [width  (.get (.getProperties tiled-map) "width")
        height (.get (.getProperties tiled-map) "height")
        grid (create-world-grid width height
                                #(case (tiled-map/movement-property tiled-map %)
                                   "none" :none
                                   "air"  :air
                                   "all"  :all))]
    (assoc ctx
           :ctx/tiled-map tiled-map
           :ctx/start-position start-position
           :ctx/grid grid
           :ctx/content-grid (create-content-grid width height 16)
           :ctx/explored-tile-corners (create-explored-tile-corners width height)
           :ctx/raycaster (create-raycaster grid)
           :ctx/potential-field-cache (atom nil)
           :ctx/id-counter (atom 0)
           :ctx/entity-ids (atom {}))))

(defn- calculate-max-speed
  [{:keys [ctx/minimum-size
           ctx/max-delta]
    :as ctx}]
  (assoc ctx :ctx/max-speed (/ minimum-size max-delta)))

(defn- define-render-z-order
  [{:keys [ctx/z-orders]
    :as ctx}]
  (assoc ctx :ctx/render-z-order (utils/define-order z-orders)))

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
    (-> ctx
        calculate-max-speed
        define-render-z-order
        (assoc-state world-fn-result))))
