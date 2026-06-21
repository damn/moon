(ns clojure.graphics.orthographic-camera.get-viewport-height
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-viewport-height [^OrthographicCamera camera]
  (.viewportHeight camera))
