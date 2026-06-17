(ns clojure.grid2d.get-nads
  (:require [clojure.grid2d.posis :as posis]
            [clojure.grid2d.nads.nad-corner :as nad-corner?]
            [clojure.math.position.diagonal-steps :refer [diagonal-steps]]))

; could be made faster because accessing the same posis oftentimes at nad-corner? check
(defn get-nads [grid]
  (loop [checkposis (filter (fn [{y 1 :as posi}]
                              (and (even? y)
                                   (= :ground (get grid posi))))
                            (posis/f grid))
         result []]
    (if (seq checkposis)
      (let [position (first checkposis)
            diagonal-posis (map #(mapv + position %) diagonal-steps)
            nads (map (fn [nad] [position nad])
                      (filter #(nad-corner?/f grid position %) diagonal-posis))]
        (recur
         (rest checkposis)
         (doall (concat result nads)))) ; doall else stackoverflow error
      result)))
