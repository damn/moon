(ns create.content-grid
  (:require [clojure.maps.map-properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]
            [moon.content-grid :as content-grid]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (props-get (get-properties tiled-map) "width")
                                                    (props-get (get-properties tiled-map) "height")
                                                    16)))
