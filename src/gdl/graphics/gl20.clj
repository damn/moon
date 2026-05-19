(ns gdl.graphics.gl20
  (:import (com.badlogic.gdx.graphics GL20)))

(def color-buffer-bit GL20/GL_COLOR_BUFFER_BIT)

(defn clear-color! [^GL20 gl20 r g b a]
  (.glClearColor gl20 r g b a))

(defn clear! [^GL20 gl20 mask]
  (.glClear gl20 mask))
