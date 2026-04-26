(ns moon.world-fns.modules
  (:require moon.modules.initial-grid
            #_moon.modules.print
            moon.modules.assoc-transitions
            moon.modules.create-scaled-grid
            moon.modules.load-schema-tiled-map
            moon.modules.place-modules
            moon.modules.convert-to-tiled-map
            moon.modules.calculate-start-position
            moon.modules.last-steps))

(defn create
  [{:keys [world/map-size
           world/max-area-level
           steps]
    :as world-fn-ctx}]
  (assert (<= max-area-level map-size))
  (reduce (fn [ctx f]
            (f ctx))
          (assoc world-fn-ctx :scale [32 20])
          [moon.modules.initial-grid/step
           #_moon.modules.print/step
           moon.modules.assoc-transitions/step
           #_moon.modules.print/step
           moon.modules.create-scaled-grid/step
           moon.modules.load-schema-tiled-map/step
           moon.modules.place-modules/step
           moon.modules.convert-to-tiled-map/step
           moon.modules.calculate-start-position/step
           moon.modules.last-steps/step]))
