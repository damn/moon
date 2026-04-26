(ns clojure.gdx.colors
  (:require [clojure.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn put! [colors]
  (doseq [[name rgba] colors]
    (Colors/put name (color/create rgba))))
