(ns create.put-colors
  (:require [clojure.gdx.color :refer [Color]])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn step [ctx]
  (doseq [[name rgba] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (Colors/put name (Color rgba)))
  ctx)
