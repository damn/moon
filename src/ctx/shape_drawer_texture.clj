(ns ctx.shape-drawer-texture
  (:require [clojure.gdx.disposable.dispose :as dispose]
            [clojure.gdx.pixmap.draw-pixel :as draw-pixel]
            [clojure.gdx.pixmap.new :as pixmap]
            [clojure.gdx.pixmap.set-color :as set-color]
            [clojure.gdx.pixmap$format.rgba8888 :as rgba8888]
            [clojure.gdx.texture.new :as texture]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/f 1 1 rgba8888/v)
                 (set-color/f 1 1 1 1)
                 (draw-pixel/f 0 0))
        texture (texture/f pixmap)]
    (dispose/f pixmap)
    texture))
