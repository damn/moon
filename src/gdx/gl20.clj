(ns gdx.gl20
  (:import (com.badlogic.gdx.graphics GL20)))

(defn gl-clear-color! [gl r g b a]
  (.glClearColor ^GL20 gl r g b a))

(defn gl-clear! [gl mask]
  (.glClear ^GL20 gl mask))

(def gl-color-buffer-bit GL20/GL_COLOR_BUFFER_BIT)
