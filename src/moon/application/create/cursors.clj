(ns moon.application.create.cursors
  (:require [clojure.edn :as edn]
            [clojure.graphics :as graphics]
            [clojure.java.io :as io])
  (:import (com.badlogic.gdx Application)))

(defn step
  [{:keys [^Application ctx/app]
    :as ctx}]
  (assoc ctx :ctx/cursors (let [{:keys [data path-format]} (-> "cursors.edn" io/resource slurp edn/read-string)]
                            (update-vals data
                                         (fn [[path [hotspot-x hotspot-y]]]
                                           (graphics/new-cursor (.getGraphics app)
                                                                (.internal (.getFiles app) (format path-format path))
                                                                hotspot-x
                                                                hotspot-y))))))
