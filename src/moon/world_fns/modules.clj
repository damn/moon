(ns moon.world-fns.modules)

(defn create
  [{:keys [world/map-size
           world/max-area-level
           steps]
    :as world-fn-ctx}]
  (assert (<= max-area-level map-size))
  (reduce (fn [ctx f]
            (f ctx))
          (assoc world-fn-ctx :scale [32 20])
          steps))
