(ns clojure.moon.create-shape-drawer-texture
  (:require [gdl.disposable :as disposable]
            [clojure.pixmap :as pixmap]
            [clojure.pixmap$format :as pixmap-format]
            [clojure.texture :as texture]
            [com.badlogic.gdx.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]))

(defn f [ctx]
  (let [pixmap (doto (pixmap/new 1 1 pixmap-format/rgba8888)
                 (pixmap/set-color! 1 1 1 1)
                 (pixmap/draw-pixel! 0 0))
        texture (texture/new (pixmap-texture-data/new pixmap
                                                      (pixmap/get-format pixmap)
                                                      false
                                                      false))]
    (disposable/dispose! pixmap)
    (assoc ctx :ctx/shape-drawer-texture texture)))
