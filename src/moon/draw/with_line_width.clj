(ns moon.draw.with-line-width
  (:require [clj.api.space.earlygrey.shape-drawer :as sd]
            [moon.ctx :as ctx]))

(defn do!
  [{:keys [ctx/shape-drawer]
    :as ctx}
   width
   draws]
  (sd/with-line-width shape-drawer width
    (ctx/draw! ctx draws)))
