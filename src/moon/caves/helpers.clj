(ns moon.caves.helpers
  (:require [clojure.rand :as rand]
            [clojure.math.position :as position]))

(defn create-order [random]
  (rand/sshuffle (range 4) random))

(defn- get-in-order [v order]
  (map #(get v %) order))

(def current-order (atom nil))

(def ^:private turn-ratio 0.25)

(defn create-rand-4-neighbour-posis [posi n random] ; TODO does more than 1 thing
  (when (< (rand/srand random) turn-ratio)
    (reset! current-order (create-order random)))
  (take n
        (get-in-order (position/get-4-neighbours posi)
                      @current-order)))

(defn- get-default-adj-num [open-paths random]
  (if (= open-paths 1)
    (case (int (rand/srand-int 4 random))
      0 1
      1 1
      2 1
      3 2
      1)
    (case (int (rand/srand-int 4 random))
      0 0
      1 1
      2 1
      3 2
      1)))

(defn- get-thin-adj-num [open-paths random]
  (if (= open-paths 1)
    1
    (case (int (rand/srand-int 7 random))
      0 0
      1 2
      1)))

(defn- get-wide-adj-num [open-paths random]
  (if (= open-paths 1)
    (case (int (rand/srand-int 3 random))
      0 1
      2)
    (case (int (rand/srand-int 4 random))
      0 1
      1 2
      2 3
      3 4
      1)))

(def get-adj-num
  {:wide    get-wide-adj-num
   :thin    get-thin-adj-num    ; h�hle mit breite 1 �berall nur -> turn-ratio verringern besser
   :default get-default-adj-num}) ; etwas breiter als 1 aber immernoch zu d�nn f�r m ein game -> turn-ratio verringern besser
