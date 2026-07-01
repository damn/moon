(ns clojure.gdx.orthographic-camera.position
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.position camera))
