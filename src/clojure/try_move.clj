(ns clojure.try-move
  (:require [clojure.valid-position :refer [valid-position?]]
            [clojure.move :as move]))

(defn f [grid body entity-id movement]
  (let [new-body (update body :body/position move/f movement)]
    (when (valid-position? grid new-body entity-id)
      new-body)))
