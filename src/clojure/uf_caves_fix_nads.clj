(ns clojure.uf-caves-fix-nads)

(defn f
  [{:keys [level/grid]
    :as level}]
  (let [grid ((:grid2d-fix-nads-fn level) grid)]
    (assoc level :level/grid grid)))
