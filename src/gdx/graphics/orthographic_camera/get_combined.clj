(ns gdx.graphics.orthographic-camera.get-combined
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-combined [^OrthographicCamera camera]
  (.combined camera))
