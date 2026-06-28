(ns graphics.clear
  (:require [graphics.get-gl20 :as get-gl20])
  (:import (com.badlogic.gdx.graphics GL20)))

(defn f!
  [graphics r g b a]
  (let [gl (get-gl20/f graphics)]
    (GL20/.glClearColor gl r g b a)
    (GL20/.glClear gl GL20/GL_COLOR_BUFFER_BIT)))
