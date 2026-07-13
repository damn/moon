(ns clojure.gdx.graphics.gl20
  (:require [com.badlogic.gdx.graphics.gl20 :as gl20]))

(defn gl-clear-color! [gl r g b a]
  (gl20/glClearColor gl r g b a))

(defn gl-clear! [gl mask]
  (gl20/glClear gl mask))

(def gl-color-buffer-bit gl20/GL_COLOR_BUFFER_BIT)
