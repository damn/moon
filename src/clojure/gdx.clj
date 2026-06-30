(ns clojure.gdx
  (:require [clojure.gdx.clear-color-buffer :as clear-color-buffer]
            [clojure.gdx.draw-tiled-map :as draw-tiled-map]
            [clojure.gdx.fit-viewport :as fit-viewport]
            [clojure.gdx.float-bits :as float-bits])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(def clear! clear-color-buffer/f!)

(def draw-tiled-map! draw-tiled-map/f!)

(def fit-viewport fit-viewport/create)

(def float-bits float-bits/f)

(defn add-actor! [stage actor]
  (Stage/.addActor stage actor))
