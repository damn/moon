(ns create.shape-drawer-texture
  (:require [clojure.white-pixel-texture :as white-pixel-texture]))

(defn step [ctx]
  (assoc ctx :ctx/shape-drawer-texture (white-pixel-texture/f)))
