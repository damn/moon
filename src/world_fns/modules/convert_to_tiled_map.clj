(ns world-fns.modules.convert-to-tiled-map
  (:require [tiled.tiled-map :as tiled-map]
            [world-fns.modules.grid-to-tiled-map :refer [grid->tiled-map]]))

(defn f
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (tiled-map/f (grid->tiled-map schema-tiled-map scaled-grid))))
