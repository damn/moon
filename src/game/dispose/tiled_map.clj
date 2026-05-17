(ns game.dispose.tiled-map
  (:require [clojure.gdx.utils.disposable :refer [dispose!]]))

(defn do!
  [{:keys [ctx/tiled-map]}]
  (dispose! tiled-map))
