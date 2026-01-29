(ns moon.stage
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport])
  (:import (moon Stage)))

(defn mouseover-actor [^Stage stage position]
  (let [[x y] (viewport/unproject (.getViewport stage) position)]
    (.hit stage x y true)))
