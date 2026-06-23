(ns ctx.shape-drawer-texture
  (:require [gdl.pixmap :as pixmap]
            [pixmap.set-color :as set-color]
            [pixmap.draw-pixel :as draw-pixel]
            [pixmap.texture :as pixmap->texture]
            [pixmap.dispose :as dispose]))

(defn step [_ctx]
  (let [pixmap (doto (pixmap/f 1 1)
                 (set-color/f! 1 1 1 1)
                 (draw-pixel/f! 0 0))
        texture (pixmap->texture/f pixmap)]
    (dispose/f! pixmap)
    texture))
