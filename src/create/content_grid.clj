(ns create.content-grid
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.content-grid :as content-grid]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/content-grid (content-grid/create (props/get (tiled-map/props tiled-map) "width")
                                                    (props/get (tiled-map/props tiled-map) "height")
                                                    16)))
