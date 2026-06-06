(ns gdx.graphics.orthographic-camera.update
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn update! [^OrthographicCamera camera]
  (.update camera))
