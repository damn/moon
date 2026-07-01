(ns clojure.gdx.orthographic-camera.combined
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f [^OrthographicCamera camera]
  (.combined camera))
