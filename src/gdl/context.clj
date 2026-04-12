(ns gdl.context
  (:require [clj.api.com.badlogic.gdx.graphics.pixmap :as pixmap]
            [clj.api.com.badlogic.gdx.graphics.pixmap.format :as pixmap.format]
            [clj.api.com.badlogic.gdx.utils.disposable :as disposable]))

(defn white-pixel-texture []
  (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (pixmap/texture pixmap)]
    (disposable/dispose! pixmap)
    texture))
