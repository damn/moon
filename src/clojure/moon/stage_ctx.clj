(ns clojure.moon.stage-ctx)

(defn f
  [{:keys [ctx/stage] :as ctx}]
  (or (:stage/ctx stage) ctx))
