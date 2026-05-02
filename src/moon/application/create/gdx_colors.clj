(ns moon.application.create.gdx-colors
  (:require [com.badlogic.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn step [ctx]
  (doseq [[name rgba] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (Colors/put name (color/create rgba)))
  ctx)
