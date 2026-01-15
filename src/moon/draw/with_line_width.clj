(ns moon.draw.with-line-width
  (:require [clj.api.space.earlygrey.shape-drawer :as sd]
            [moon.graphics :as graphics]))

(defn do!
  [{:keys [graphics/shape-drawer]
    :as graphics}
   width
   draws]
  (sd/with-line-width shape-drawer width
    (graphics/draw! graphics draws)))
