(ns clojure.moon.render.animation
  (:require [clojure.moon.render.image :as image]))

(defn f
  [{:keys [frames
           cnt
           frame-duration]}
   entity
   ctx]
  (image/f (frames (min (int (/ (float cnt) (float frame-duration)))
                               (dec (count frames))))
                  entity
                  ctx))
