(ns gdl.gl20
  (:require [com.badlogic.gdx.graphics.gl20 :as gl20]))

(def color-buffer-bit
  gl20/GL_COLOR_BUFFER_BIT)

(defn clear! [& args]
  (apply gl20/glClear args))

(defn clear-color! [& args]
  (apply gl20/glClearColor args))
