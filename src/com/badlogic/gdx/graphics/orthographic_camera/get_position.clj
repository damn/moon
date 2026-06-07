(ns com.badlogic.gdx.graphics.orthographic-camera.get-position
  (:require [com.badlogic.gdx.math.vector3 :refer [->clj]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn get-position [^OrthographicCamera camera]
  (->clj (.position camera)))
