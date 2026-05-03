(ns clojure.graphics
  (:require [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]))

(def frames-per-second graphics/frames-per-second)

(def delta-time graphics/delta-time)

(def set-cursor! graphics/set-cursor!)

(defn clear! [graphics r g b a]
  (gl20/clear-color! (graphics/gl20 graphics) r g b a)
  (gl20/clear!       (graphics/gl20 graphics) gl20/color-buffer-bit))
