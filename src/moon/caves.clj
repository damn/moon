(ns moon.caves
  (:require [moon.caves.create :as create]
            [moon.caves.k-to-adj-num :refer [k->adj-num]]))

(defn create [random min-cells max-cells adjnum-type]
  (create/f random min-cells max-cells (k->adj-num adjnum-type)))
