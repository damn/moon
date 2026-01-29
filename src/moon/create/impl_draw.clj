(ns moon.create.impl-draw
  (:require [moon.ctx :as ctx]))

(defn step
  [ctx
   draw-fns]
  (extend-type (class ctx)
    ctx/Graphics
    (draw! [ctx draws]
      (doseq [{k 0 :as component} draws
              :when component]
        (apply (get draw-fns k) ctx (rest component)))))
  ctx)
