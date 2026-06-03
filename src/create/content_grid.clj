(ns create.content-grid
  (:require [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.content-grid :as content-grid]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (props-get (tiled-map/props tiled-map) "width")
                                                    (props-get (tiled-map/props tiled-map) "height")
                                                    16)))
