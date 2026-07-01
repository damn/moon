(ns clojure.gdx.orthographic-camera.update
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f! [^OrthographicCamera camera]
  (.update camera))
