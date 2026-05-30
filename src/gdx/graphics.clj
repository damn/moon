(ns gdx.graphics
  (:require [com.badlogic.gdx.graphics :as graphics]
            [com.badlogic.gdx.graphics.gl20 :as gl20]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]))

(defn delta-time [graphics]
  (graphics/delta-time graphics))

(defn frames-per-second [graphics]
  (graphics/frames-per-second graphics))

(defn clear! [graphics r g b a]
  (let [gl (graphics/gl20 graphics)]
    (gl20/clear-color! gl r g b a)
    (gl20/clear! gl gl20/color-buffer-bit)))

(defn set-cursor! [graphics cursor]
  (graphics/set-cursor! graphics cursor))

(defn new-cursor [graphics pixmap hotspot-x hotspot-y]
  (graphics/new-cursor graphics pixmap hotspot-x hotspot-y))

(defn white-pixel-texture []
  (let [pixmap (doto (pixmap/create 1 1)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (pixmap/dispose! pixmap)
    texture))
