(ns clojure.tiled-map-tile-layer
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]))

(defn get-cell [& args]
  (apply tiled-map-tile-layer/getCell args))

(defn get-height [& args]
  (apply tiled-map-tile-layer/getHeight args))

(defn get-name [& args]
  (apply tiled-map-tile-layer/getName args))

(defn get-render-offset-x [& args]
  (apply tiled-map-tile-layer/getRenderOffsetX args))

(defn get-render-offset-y [& args]
  (apply tiled-map-tile-layer/getRenderOffsetY args))

(defn get-tile-height [& args]
  (apply tiled-map-tile-layer/getTileHeight args))

(defn get-tile-width [& args]
  (apply tiled-map-tile-layer/getTileWidth args))

(defn get-properties [& args]
  (apply tiled-map-tile-layer/getProperties args))

(defn get-width [& args]
  (apply tiled-map-tile-layer/getWidth args))

(defn visible? [& args]
  (apply tiled-map-tile-layer/isVisible args))

(defn new [& args]
  (apply tiled-map-tile-layer/new args))

(defn set-cell! [& args]
  (apply tiled-map-tile-layer/setCell args))

(defn set-name! [& args]
  (apply tiled-map-tile-layer/setName args))

(defn set-visible! [& args]
  (apply tiled-map-tile-layer/setVisible args))
