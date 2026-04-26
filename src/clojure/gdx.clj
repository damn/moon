(ns clojure.gdx
  (:require clojure.gdx.graphics
            clojure.gdx.input)
  (:import (com.badlogic.gdx Gdx)))

(defn audio []
  Gdx/audio)

(defn graphics []
  Gdx/graphics)

(defn files []
  Gdx/files)

(defn input []
  Gdx/input)
