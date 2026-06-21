(ns clojure.ctx.shape-drawer-texture
  (:require [clojure.pixmap :as pixmap]
            [clojure.pixmap.set-color :as set-color]
            [clojure.pixmap.draw-pixel :as draw-pixel]
            [clojure.pixmap.texture :as pixmap->texture]
            [clojure.pixmap.dispose :as dispose]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/create 1 1)
                 (set-color/f! 1 1 1 1)
                 (draw-pixel/f! 0 0))
        texture (pixmap->texture/f pixmap)]
    (dispose/f! pixmap)
    texture))
