(ns moon.stage
  (:require [moon.viewport :as viewport])
  (:import (moon Stage)))

(defn mouseover-actor [^Stage stage position]
  (let [[x y] (viewport/unproject (.getViewport stage) position)]
    (.hit stage x y true)))
