(ns clojure.tiled-map-tile-layer
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]))

(defn get-cell [& args]
  (apply tiled-map-tile-layer/get-cell args))

(defn get-height [& args]
  (apply tiled-map-tile-layer/get-height args))

(defn get-name [& args]
  (apply tiled-map-tile-layer/get-name args))

(defn get-render-offset-x [& args]
  (apply tiled-map-tile-layer/get-render-offset-x args))

(defn get-render-offset-y [& args]
  (apply tiled-map-tile-layer/get-render-offset-y args))

(defn get-tile-height [& args]
  (apply tiled-map-tile-layer/get-tile-height args))

(defn get-tile-width [& args]
  (apply tiled-map-tile-layer/get-tile-width args))

(defn get-properties [& args]
  (apply tiled-map-tile-layer/get-properties args))

(defn get-width [& args]
  (apply tiled-map-tile-layer/get-width args))

(defn visible? [& args]
  (apply tiled-map-tile-layer/visible? args))

(defn new [& args]
  (apply tiled-map-tile-layer/new args))

(defn set-cell! [& args]
  (apply tiled-map-tile-layer/set-cell! args))

(defn set-name! [& args]
  (apply tiled-map-tile-layer/set-name! args))

(defn set-visible! [& args]
  (apply tiled-map-tile-layer/set-visible! args))
