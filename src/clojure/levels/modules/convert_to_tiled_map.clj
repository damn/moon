(ns clojure.levels.modules.convert-to-tiled-map
  (:require [clojure.tiled-tiled-map :as tiled-map]
            [clojure.grid-to-tiled-map :refer [grid->tiled-map]]))

(defn f
  [{:keys [scaled-grid
           schema-tiled-map]
    :as w}]
  (assoc w :tiled-map (tiled-map/f (grid->tiled-map schema-tiled-map scaled-grid))))
