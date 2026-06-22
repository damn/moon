(ns gdl.orthographic-camera.get-zoom
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-zoom [^OrthographicCamera camera]
  (.zoom camera))

