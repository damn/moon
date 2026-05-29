(ns create.shape-drawer-texture
  (:require [gdx.graphics :as graphics]))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture (graphics/white-pixel-texture)))
