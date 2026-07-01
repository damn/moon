(ns clojure.gdx.gl20.clear-color
  (:import (com.badlogic.gdx.graphics GL20)))

(defn f [gl r g b a]
  (GL20/.glClearColor gl r g b a))
