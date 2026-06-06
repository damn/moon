(ns gdx.graphics.orthographic-camera.get-position
  (:require [gdx.to-clj :refer [->clj]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-position [^OrthographicCamera camera]
  (->clj (.position camera)))
