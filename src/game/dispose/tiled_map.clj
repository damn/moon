(ns game.dispose.tiled-map
  (:require [gdl.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/tiled-map]}]
  (dispose! tiled-map))
