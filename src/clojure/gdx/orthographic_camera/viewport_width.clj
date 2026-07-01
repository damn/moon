(ns clojure.gdx.orthographic-camera.viewport-width
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.viewportWidth camera))
