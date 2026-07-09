(ns clojure.caves.gen
  (:require [clojure.caves.create :as create]
            [clojure.caves.k-to-adj-num :refer [k->adj-num]]))

(defn create [random min-cells max-cells adjnum-type]
  (create/f random min-cells max-cells (k->adj-num adjnum-type)))
