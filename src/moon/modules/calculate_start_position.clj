(ns moon.modules.calculate-start-position)

(defn step [{:keys [start scale] :as w}]
  (assoc w :start-position (mapv * start scale)))
