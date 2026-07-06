(ns ctx.entity.render.animation
  (:require [ctx.entity.render.image :as render-image]))

(defn f
  [{:keys [frames
           cnt
           frame-duration]}
   entity
   ctx]
  (render-image/f (frames (min (int (/ (float cnt) (float frame-duration)))
                               (dec (count frames))))
                  entity
                  ctx))
