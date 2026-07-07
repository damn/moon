(ns clojure.tmx
  (:require [clojure.load-tmx-map :as load-tmx-map]))

(defn create
  [{:keys [tmx-file
           start-position]}]
  {:tiled-map (load-tmx-map/f tmx-file)
   :start-position start-position})
