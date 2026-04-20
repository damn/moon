(ns moon.impl.shape-drawer-texture
  (:require [clojure.graphics :as graphics]))

(defn create [{:keys [ctx/graphics]}]
  (graphics/white-pixel-texture graphics))
