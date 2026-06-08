(ns world-fns.modules.calculate-start)

(defn f [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))
