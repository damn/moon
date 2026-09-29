(ns com.badlogic.gdx.graphics.gl20
  (:import (com.badlogic.gdx.graphics GL20)))

(def GL_COLOR_BUFFER_BIT GL20/GL_COLOR_BUFFER_BIT)

(defn glClear [gl bit-mask]
  (.glClear ^GL20 gl bit-mask))

(defn glClearColor [gl r g b a]
  (.glClearColor ^GL20 gl r g b a))
