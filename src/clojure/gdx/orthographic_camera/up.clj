(ns clojure.gdx.orthographic-camera.up
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.up camera))
