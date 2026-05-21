(ns com.badlogic.gdx.graphics
  (:require [clojure.graphics :as graphics])
  (:import (com.badlogic.gdx Graphics)))

(extend-type Graphics
  graphics/Graphics
  (frames-per-second [graphics]
    (.getFramesPerSecond graphics))

  (delta-time [graphics]
    (.getDeltaTime graphics))

  (new-cursor [graphics pixmap hotspot-x hotspot-y]
    (.newCursor graphics pixmap hotspot-x hotspot-y))

  (set-cursor! [graphics cursor]
    (.setCursor graphics cursor))

  (gl20 [graphics]
    (.getGL20 graphics)))
