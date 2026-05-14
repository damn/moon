(ns moon.application.create.gdx-colors
  (:require [com.badlogic.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors))) ; TODO colors

(defn step [ctx colors]
  (doseq [[name rgba] colors]
    (Colors/put name (color/create rgba)))
  ctx)
