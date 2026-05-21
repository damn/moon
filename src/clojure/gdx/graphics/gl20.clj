(ns clojure.gdx.graphics.gl20
  (:require [clojure.graphics.gl20 :as gl20])
  (:import (com.badlogic.gdx.graphics GL20)))

(.bindRoot #'gl20/color-buffer-bit GL20/GL_COLOR_BUFFER_BIT)

(extend-type GL20
  gl20/GL20
  (clear-color! [gl20 r g b a]
    (.glClearColor gl20 r g b a))

  (clear! [gl20 mask]
    (.glClear gl20 mask)))
