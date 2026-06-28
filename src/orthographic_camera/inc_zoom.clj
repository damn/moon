(ns orthographic-camera.inc-zoom
  (:require [orthographic-camera.set-zoom :refer [set-zoom!]])
  (:import (com.badlogic.gdx.graphics OrthographicCamera)))

(defn inc-zoom! [^OrthographicCamera cam by]
  (set-zoom! cam (max 0.1 (+ (.zoom cam) by))))
