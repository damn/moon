(ns create.content-grid
  (:require [moon.content-grid :as content-grid]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (.get (.getProperties tiled-map) "width")
                                                    (.get (.getProperties tiled-map) "height")
                                                    16)))
