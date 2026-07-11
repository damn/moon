(ns clojure.try-move
  (:require [moon.grid :as grid]
            [moon.v2 :as v2]))

(defn f [grid body entity-id movement]
  (let [new-body (update body :body/position v2/move movement)]
    (when (grid/valid-position? grid new-body entity-id)
      new-body)))
