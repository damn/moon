(ns clojure.valid-position
  (:require [clojure.get-cells :refer [get-cells]]
            [clojure.body.touched-tiles :refer [touched-tiles]]
            [clojure.overlaps :refer [overlaps?]]
            [clojure.grid.cell.is-blocked :as blocked?]
            [clojure.cells-entities :as cells->entities]))

(defn valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
  (assert (:body/collides? body))
  (let [cells* (into [] (map deref) (get-cells g2d (touched-tiles body)))]
    (and (not-any? #(blocked?/f % z-order) cells*)
         (->> cells*
              cells->entities/f
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:body/collides? (:entity/body other-entity))
                                 (overlaps? (:entity/body other-entity)
                                                 body)))))))))
