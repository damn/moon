(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:require [clojure.tiled-map :as tiled-map])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(.bindRoot #'tiled-map/create
           (fn []
             (TiledMap.)))

(extend-type TiledMap
  tiled-map/TiledMap
  (dispose! [this]
    (.dispose this))

  (properties [this]
    (.getProperties this))

  (layers [this]
    (.getLayers this)))
