(ns clojure.content-grid.active-entities
  (:require [clojure.position.neighbours-8 :refer [get-8-neighbours]]))

(defn f
  [{:keys [grid]} center-entity]
  (->> (let [idx (-> center-entity
                     :moon.content-grid/content-cell
                     deref
                     :idx)]
         (cons idx (get-8-neighbours idx)))
       (keep grid)
       (mapcat (comp :entities deref))))
