(ns clojure.remove-entity)

(defn f! [_ eid]
  (-> @eid
      :moon.content-grid/content-cell
      (swap! update :entities disj eid)))
