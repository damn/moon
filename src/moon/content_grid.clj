(ns moon.content-grid)

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

(defn add-entity! [this eid]
  (update-entity! this eid))

(defn remove-entity! [_ eid]
  (-> @eid
      :moon.content-grid/content-cell
      (swap! update :entities disj eid)))

(defn position-changed! [this eid]
  (update-entity! this eid))
