(ns clojure.grid.valid-position
  (:require [clojure.body :as body]
            [clojure.g2d.get-cells :refer [get-cells]]
            [moon.cell.is-blocked :as blocked?]
            [clojure.grid.cells-entities :as cells->entities]
            [clojure.overlaps :refer [overlaps?]]))

(defn valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
  (assert (:body/collides? body))
  (let [cells* (into [] (map deref) (get-cells g2d (body/touched-tiles body)))]
    (and (not-any? #(blocked?/f % z-order) cells*)
         (->> cells*
              cells->entities/f
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:body/collides? (:entity/body other-entity))
                                 (overlaps? (:entity/body other-entity)
                                            body)))))))))
