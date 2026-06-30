(ns ctx.shape-drawer-texture
  (:require [clojure.gdx :as gdx]))

(defn step [_ctx]
  (let [pixmap (doto (gdx/pixmap-create 1 1 gdx/pixmap-format-rgba8888)
                 (gdx/pixmap-set-color! 1 1 1 1)
                 (gdx/pixmap-draw-pixel! 0 0))
        texture (gdx/pixmap->texture pixmap)]
    (gdx/pixmap-dispose! pixmap)
    texture))
