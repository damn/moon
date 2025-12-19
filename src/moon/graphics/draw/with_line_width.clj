(ns moon.graphics.draw.with-line-width
  (:require [gdl.graphics.shape-drawer :as sd]
            [moon.graphics]))

(defn do!
  [{:keys [graphics/shape-drawer]
    :as graphics}
   width
   draws]
  (sd/with-line-width shape-drawer width
    (moon.graphics/draw! graphics draws)))
