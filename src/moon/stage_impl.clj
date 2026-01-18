(ns moon.stage-impl
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.stage :as stage])
  (:import (moon Stage)))

(extend-type Stage
  moon.stage/Stage
  (mouseover-actor [stage position]
    (let [[x y] (viewport/unproject (.getViewport stage) position)]
      (.hit stage x y true))))
