(ns moon.application.create.shape-drawer-texture
  (:require [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.pixmap.format :as pixmap.format]
            [com.badlogic.gdx.graphics.texture :as texture]))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture (let [pixmap (doto (pixmap/create 1 1 pixmap.format/rgba8888)
                                                      (pixmap/set-color! 1 1 1 1)
                                                      (pixmap/draw-pixel! 0 0))
                                             texture (texture/create pixmap)]
                                         (pixmap/dispose! pixmap)
                                         texture)))
