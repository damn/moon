(ns clojure.grid.valid-position
  (:require [moon.body :as body]
            [moon.g2d :as g2d]
            [moon.cell :as cell]
            [moon.grid :as grid]
            [clojure.overlaps :refer [overlaps?]]))

(defn valid-position? [g2d {:keys [body/z-order] :as body} entity-id]
  (assert (:body/collides? body))
  (let [cells* (into [] (map deref) (g2d/get-cells g2d (body/touched-tiles body)))]
    (and (not-any? #(cell/blocked? % z-order) cells*)
         (->> cells*
              grid/entities
              (not-any? (fn [other-entity]
                          (let [other-entity @other-entity]
                            (and (not= (:entity/id other-entity) entity-id)
                                 (:body/collides? (:entity/body other-entity))
                                 (overlaps? (:entity/body other-entity)
                                            body)))))))))
