(ns clojure.gdx.orthographic-camera.set-to-ortho
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn f! [^OrthographicCamera camera y-down viewport-width viewport-height]
  (.setToOrtho camera y-down viewport-width viewport-height))
