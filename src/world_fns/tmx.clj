(ns world-fns.tmx
  (:require [clojure.gdx :as gdx]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (gdx/tmx-map-loader-load tmx-file)
   :start-position start-position})
