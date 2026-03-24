(ns moon.create.content-grid
  (:require [moon.content-grid :as content-grid]
            [moon.grid2d :as g2d])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn- create* [width height cell-size]
  {:grid (g2d/create-grid
          (inc (int (/ width  cell-size)))
          (inc (int (/ height cell-size)))
          (fn [idx]
            (atom {:idx idx,
                   :entities #{}})))
   :cell-w cell-size
   :cell-h cell-size})

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

(extend-type clojure.lang.PersistentArrayMap
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

(defn step
  [{:keys [^TiledMap ctx/tiled-map]
    :as ctx}
   cell-size]
  (assoc ctx :ctx/content-grid (create* (.get (.getProperties tiled-map) "width")
                                        (.get (.getProperties tiled-map) "height")
                                        cell-size)))

; TODO content-grid protocol
