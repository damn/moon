(ns clojure.tiled-map
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]))

(defn get-layers [& args]
  (apply tiled-map/get-layers args))

(defn get-properties [& args]
  (apply tiled-map/get-properties args))

(defn new [& args]
  (apply tiled-map/new args))
