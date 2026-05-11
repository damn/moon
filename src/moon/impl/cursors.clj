(ns moon.impl.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Application
                             Graphics)
           (com.badlogic.gdx.graphics Pixmap)))

(defn create
  [{:keys [^Application ctx/app]}]
  (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
    (update-vals data
                 (fn [[path [hotspot-x hotspot-y]]]
                   (let [pixmap (Pixmap. (.internal (.getFiles app) (format path-format path)))
                         cursor (.newCursor (.getGraphics app) pixmap hotspot-x hotspot-y)]
                     (.dispose pixmap)
                     cursor)))))
