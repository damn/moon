(ns moon.val-max.to-pos-int)

(defn f [val-max]
  (mapv #(-> % int (max 0)) val-max))
