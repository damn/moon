(ns game.world-fns.tmx)

(defn create
  [{:keys [tmx-file
           start-position
           load-map-fn]}]
  {:tiled-map (load-map-fn tmx-file)
   :start-position start-position})
