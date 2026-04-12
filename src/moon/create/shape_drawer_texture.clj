(ns moon.create.shape-drawer-texture
  (:require [gdl.context :as context]))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture (context/white-pixel-texture)))
