(ns clojure.gdx.orthographic-camera.zoom
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.zoom camera))
