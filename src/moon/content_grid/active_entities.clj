(ns moon.content-grid.active-entities
  (:require [clojure.math.position.get-8-neighbours :refer [get-8-neighbours]]))

(defn f
  [{:keys [grid]} center-entity]
  (->> (let [idx (-> center-entity
                     :moon.content-grid/content-cell
                     deref
                     :idx)]
         (cons idx (get-8-neighbours idx)))
       (keep grid)
       (mapcat (comp :entities deref))))
