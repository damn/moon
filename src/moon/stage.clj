(ns moon.stage
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]))

(defn mouseover-actor [stage position]
  (let [[x y] (viewport/unproject (.getViewport stage) position)]
    (.hit stage x y true)))
