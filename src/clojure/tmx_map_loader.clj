(ns clojure.tmx-map-loader
  (:refer-clojure :exclude [new load])
  (:require [com.badlogic.gdx.maps.tiled.tmx-map-loader :as tmx-map-loader]))

(defn new [& args]
  (apply tmx-map-loader/new args))

(defn load! [& args]
  (apply tmx-map-loader/load args))
