(ns gdx.graphics.colors
  (:require [gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn put! [colors-map]
  (doseq [[name rgba] colors-map]
    (Colors/put name (color/create rgba))))
