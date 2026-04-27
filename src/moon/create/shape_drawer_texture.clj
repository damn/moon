(ns moon.create.shape-drawer-texture
  (:require [clojure.graphics :as graphics]))

(defn step [{:keys [ctx/graphics] :as ctx}]
  (assoc ctx :ctx/shape-drawer-texture (graphics/white-pixel-texture graphics)))
