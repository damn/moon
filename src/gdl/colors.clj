(ns gdl.colors
  (:require [gdl.rgba.color :refer [rgba->Color]])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn put! [colors]
  (doseq [[name color] colors]
    (Colors/put name (rgba->Color color))))
