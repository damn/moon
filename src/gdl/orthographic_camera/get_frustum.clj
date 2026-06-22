(ns gdl.orthographic-camera.get-frustum
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-frustum [^OrthographicCamera camera]
  (.frustum camera))
