(ns clojure.graphics.orthographic-camera
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn create
  [{:keys [y-down?
           world-width
           world-height]}]
  (doto (OrthographicCamera.)
    (.setToOrtho y-down? world-width world-height)))
