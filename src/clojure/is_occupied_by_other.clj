(ns clojure.is-occupied-by-other)

(defn f [{:keys [occupied]} eid]
  (some #(not= % eid) occupied))
