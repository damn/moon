(ns ctx.shape-drawer-texture
  (:require [com.badlogic.gdx.utils.disposable :as disposable]
            [clojure.gdx.pixmap.draw-pixel :as draw-pixel]
            [clojure.gdx.pixmap.new :as pixmap]
            [clojure.gdx.pixmap.set-color :as set-color]
            [com.badlogic.gdx.graphics.pixmap$format :as pixmap-format]
            [clojure.gdx.texture.new :as texture]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/f 1 1 pixmap-format/rgba8888)
                 (set-color/f 1 1 1 1)
                 (draw-pixel/f 0 0))
        texture (texture/f pixmap)]
    (disposable/dispose! pixmap)
    texture))
