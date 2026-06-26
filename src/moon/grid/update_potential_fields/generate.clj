(ns moon.grid.update-potential-fields.generate
  (:require [moon.grid.update-potential-fields.step :as step]))

(defn generate-potential-field
  [grid faction tiles->entities max-iterations]
  (let [entity-cell-seq (for [[tile eid] tiles->entities]
                          [eid (grid tile)])
        marked (map second entity-cell-seq)]
    (doseq [[eid cell] entity-cell-seq]
      (swap! cell assoc faction {:distance 0
                                 :eid eid}))
    (loop [marked-cells     marked
           new-marked-cells marked
           iterations 0]
      (if (= iterations max-iterations)
        marked-cells
        (let [new-marked (step/f grid faction new-marked-cells)]
          (recur (concat marked-cells new-marked)
                 new-marked
                 (inc iterations)))))))
