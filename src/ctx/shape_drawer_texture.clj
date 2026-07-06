(ns ctx.shape-drawer-texture
  (:require
            [com.badlogic.gdx.graphics.texture :as texture] [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.pixmap$format :as pixmap-format]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap-format/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (texture/new-from-pixmap pixmap)]
    (disposable/dispose! pixmap)
    texture))
