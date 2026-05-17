(ns game.impl.world-viewport)

(defn create
  [{:keys [ctx/world-unit-scale]}
   {:keys [viewport
           camera]}]
  (let [world-width  (* 1440 world-unit-scale)
        world-height (* 900  world-unit-scale)]
    (viewport world-width
              world-height
              (camera {:y-down? false
                       :world-width world-width
                       :world-height world-height}))))
