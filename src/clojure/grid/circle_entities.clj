(ns clojure.grid.circle-entities
  (:require [com.badlogic.gdx.math.circle :as circle]
            [com.badlogic.gdx.math.intersector :as intersector]
            [moon.body :as body]
            [moon.circle :as moon-circle]
            [moon.rectangle :as rectangle]
            [moon.g2d :as g2d]
            [moon.grid :as grid]))

(defn circle->entities [g2d {:keys [position radius] :as circle}]
  (let [[x y] position
        gdx-circle (circle/new x y radius)]
    (->> circle
         moon-circle/outer-rectangle
         rectangle/touched-tiles
         (g2d/get-cells g2d)
         (map deref)
         grid/entities
         (filter #(intersector/overlaps gdx-circle
                                        (body/rectangle (:entity/body @%)))))))
