(ns clojure.gdx.orthographic-camera.frustum
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.frustum camera))
