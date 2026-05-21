(ns com.badlogic.gdx.maps.tiled.tmx-map-loader
  (:require [clojure.maps.tiled.tmx-map-loader :as tmx-map-loader])
  (:import (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(.bindRoot #'tmx-map-loader/load!
           (fn [tmx-file]
             (.load (TmxMapLoader.) tmx-file)))
