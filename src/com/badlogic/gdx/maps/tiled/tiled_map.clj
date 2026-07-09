(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn get-layers [tiled-map]
  (TiledMap/.getLayers tiled-map))

; TODO name getProperties ???
; and :refer -> clojure API is
; _ just without the dot ??? -
(defn get-properties [tiled-map]
  (TiledMap/.getProperties tiled-map))

; Constructor at top?
; name TiledMap
(defn new []
  (TiledMap.))
