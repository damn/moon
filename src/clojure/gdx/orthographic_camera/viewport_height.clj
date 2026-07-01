(ns clojure.gdx.orthographic-camera.viewport-height
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.viewportHeight camera))
