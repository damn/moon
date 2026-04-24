(ns clojure.gdx
  (:require clojure.gdx.application
            clojure.gdx.audio
            clojure.gdx.files
            clojure.gdx.graphics
            clojure.gdx.input)
  (:import (com.badlogic.gdx Gdx)))

(defn app []
  Gdx/app)

(defn audio []
  Gdx/audio)

(defn graphics []
  Gdx/graphics)

(defn files []
  Gdx/files)

(defn input []
  Gdx/input)
