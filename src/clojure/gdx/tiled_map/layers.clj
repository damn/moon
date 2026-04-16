(ns clojure.gdx.tiled-map.layers
  (:refer-clojure :exclude [get])
  (:require [clj.api.com.badlogic.gdx.maps.map-layers :as layers]))

(def get layers/get)
(def add! layers/add!)
