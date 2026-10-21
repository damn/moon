(ns moon.content-grid
  (:require [moon.position :as position]))

(defn create [width height cell-size]
  (let [w (inc (int (/ width cell-size)))
        h (inc (int (/ height cell-size)))
        xyfn (fn [idx]
               (atom {:idx idx
                      :entities #{}}))]
    {:grid (mapv (fn [x] (mapv (fn [y] (xyfn [x y]))
                               (range h)))
                 (range w))
     :cell-w cell-size
     :cell-h cell-size}))

(defn update-entity!
  [{:keys [grid
           cell-w
           cell-h]}
   eid]
  (let [{:keys [moon.content-grid/content-cell
                entity/position]} @eid
        [x y] position
        new-cell (get-in grid [(int (/ x cell-w))
                               (int (/ y cell-h))])]
    (when-not (= content-cell new-cell)
      (swap! new-cell update :entities conj eid)
      (swap! eid assoc :moon.content-grid/content-cell new-cell)
      (when content-cell
        (swap! content-cell update :entities disj eid)))))

(defn remove-entity! [_ eid]
  (-> @eid
      :moon.content-grid/content-cell
      (swap! update :entities disj eid)))

(defn active-entities
  [{:keys [grid]} center-entity]
  (->> (let [idx (-> center-entity
                     :moon.content-grid/content-cell
                     deref
                     :idx)]
         (cons idx (position/get-8-neighbours idx)))
       (keep #(get-in grid %))
       (mapcat (comp :entities deref))))
