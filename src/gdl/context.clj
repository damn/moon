(ns gdl.context
  (:require [clj.api.com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [clj.api.com.badlogic.gdx.graphics.pixmap :as pixmap]
            [clj.api.com.badlogic.gdx.graphics.pixmap.format :as pixmap.format]
            [clj.api.com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]
            [clj.api.com.badlogic.gdx.utils.viewport.fit-viewport :as fit-viewport]))

(defn sprite-batch []
  (sprite-batch/create))

(defn white-pixel-texture []
  (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (disposable/dispose! pixmap)
    texture))

(defn ui-viewport [width height]
  (fit-viewport/create width height (orthographic-camera/create)))

(defn world-viewport [world-width world-height]
  (fit-viewport/create world-width
                       world-height
                       (doto (orthographic-camera/create)
                         (orthographic-camera/set-to-ortho! false world-width world-height))))
