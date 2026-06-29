(ns clojure.gdx.clear-color-buffer
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics GL20)))

(defn f! [graphics r g b a]
  (let [gl (Graphics/.getGL20 graphics)]
    (GL20/.glClearColor gl r g b a)
    (GL20/.glClear gl GL20/GL_COLOR_BUFFER_BIT)))
