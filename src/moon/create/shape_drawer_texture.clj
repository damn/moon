(ns moon.create.shape-drawer-texture
  (:require [clojure.graphics :as graphics]))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture (graphics/white-pixel-texture (:ctx/graphics ctx))))
