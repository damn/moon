(ns com.badlogic.gdx.graphics.orthographic-camera.get-viewport-width
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-viewport-width [^OrthographicCamera camera]
  (.viewportWidth camera))
