(ns gdx.graphics.colors
  (:require [com.badlogic.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn put! [colors]
  (doseq [[name color] colors]
    (Colors/put name (color/create color))))
