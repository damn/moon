(ns clojure.gl20
  (:import (com.badlogic.gdx.graphics GL20)))

(def color-buffer-bit GL20/GL_COLOR_BUFFER_BIT)

(defn clear! [gl bit-mask]
  (GL20/.glClear gl bit-mask))

(defn clear-color! [gl r g b a]
  (GL20/.glClearColor gl r g b a))
