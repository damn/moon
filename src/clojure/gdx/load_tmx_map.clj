(ns clojure.gdx.load-tmx-map
  (:require [clojure.gdx.tmx-map-loader.new :as tmx-map-loader]
            [clojure.gdx.tmx-map-loader.load :as load]))

(defn f [path]
  (load/f (tmx-map-loader/f) path))
