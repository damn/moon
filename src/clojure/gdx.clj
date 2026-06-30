(ns clojure.gdx
  (:require [clojure.gdx.clear-color-buffer :as clear-color-buffer]
            [clojure.gdx.draw-tiled-map :as draw-tiled-map]
            ))

(def clear! clear-color-buffer/f!)

(def draw-tiled-map! draw-tiled-map/f!)
