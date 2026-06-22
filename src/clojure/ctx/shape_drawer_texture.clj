(ns clojure.ctx.shape-drawer-texture
  (:require [gdl.pixmap :as pixmap]
            [gdl.pixmap.set-color :as set-color]
            [gdl.pixmap.draw-pixel :as draw-pixel]
            [gdl.pixmap.texture :as pixmap->texture]
            [gdl.pixmap.dispose :as dispose]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/create 1 1)
                 (set-color/f! 1 1 1 1)
                 (draw-pixel/f! 0 0))
        texture (pixmap->texture/f pixmap)]
    (dispose/f! pixmap)
    texture))
