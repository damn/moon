(ns clojure.orthographic-camera.update
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn update! [^OrthographicCamera camera]
  (.update camera))
