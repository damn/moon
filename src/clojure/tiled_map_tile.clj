(ns clojure.tiled-map-tile
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]))

(defn get-offset-x [& args]
  (apply tiled-map-tile/get-offset-x args))

(defn get-offset-y [& args]
  (apply tiled-map-tile/get-offset-y args))

(defn get-properties [& args]
  (apply tiled-map-tile/get-properties args))

(defn get-texture-region [& args]
  (apply tiled-map-tile/get-texture-region args))

(defn get-tile [& args]
  (apply tiled-map-tile/get-tile args))
