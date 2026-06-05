(ns create.shape-drawer-texture
  (:require [clojure.pixmap :as pixmap]))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture
         (let [pixmap (doto (pixmap/create 1 1)
                        (pixmap/set-color! 1 1 1 1)
                        (pixmap/draw-pixel! 0 0))
               texture (pixmap/texture pixmap)]
           (pixmap/dispose! pixmap)
           texture)))
