(ns moon.tx.update-viewports
  (:require [moon.graphics :as graphics]))

(defn do! [{:keys [ctx/graphics] :as ctx} width height]
  (graphics/update-ui-viewport! graphics width height)
  (graphics/update-world-vp! graphics width height)
  ctx)
