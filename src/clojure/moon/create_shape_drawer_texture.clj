(ns clojure.moon.create-shape-drawer-texture
  (:require [com.badlogic.gdx.utils.disposable :as disposable]
            [com.badlogic.gdx.graphics.pixmap :as pixmap]
            [com.badlogic.gdx.graphics.pixmap$format :as pixmap-format]
            [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]))

(defn f [ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap-format/RGBA8888)
                 (pixmap/setColor 1 1 1 1)
                 (pixmap/drawPixel 0 0))
        texture (texture/new (pixmap-texture-data/new pixmap
                                                      (pixmap/getFormat pixmap)
                                                      false
                                                      false))]
    (disposable/dispose pixmap)
    (assoc ctx :ctx/shape-drawer-texture texture)))
