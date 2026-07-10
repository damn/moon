(ns clojure.moon.create-shape-drawer-texture
  (:require [gdl.utils.disposable :as disposable]
            [gdl.graphics.pixmap :as pixmap]
            [gdl.graphics.pixmap.format :as pixmap-format]
            [gdl.graphics.texture :as texture]
            [gdl.graphics.glutils.pixmap-texture-data :as pixmap-texture-data]))

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
