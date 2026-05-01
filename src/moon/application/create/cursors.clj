(ns moon.application.create.cursors
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.graphics Pixmap)))

(defn step
  [{:keys [^Application ctx/app]
    :as ctx}]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path [hotspot-x hotspot-y]]]
                                           (let [pixmap (Pixmap. (.internal (.getFiles app) (format path-format path)))
                                                 cursor (.newCursor (.getGraphics app) pixmap hotspot-x hotspot-y)]
                                             (.dispose pixmap)
                                             cursor))))))
