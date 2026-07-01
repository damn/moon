(ns clojure.gdx.orthographic-camera.set-zoom
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f! [^OrthographicCamera camera amount]
  (set! (.zoom camera) amount))
