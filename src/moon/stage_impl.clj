(ns moon.stage-impl
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.stage :as stage])
  (:import (moon Stage)))

(extend-type Stage
  moon.stage/Stage
  (add-actor! [stage actor]
    (.addActor stage actor))

  (root [stage]
    (.getRoot stage))

  (mouseover-actor [stage position]
    (let [[x y] (viewport/unproject (stage/viewport stage) position)]
      (.hit stage x y true)))

  (viewport [stage]
    (.getViewport stage)))
