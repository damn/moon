(ns moon.create.impl-draw
  (:require [moon.draws :as draws]))

(defn step
  [ctx
   draw-fns]
  (extend-type (class ctx)
    draws/Draws
    (handle! [ctx draws]
      (doseq [{k 0 :as component} draws
              :when component]
        (apply (get draw-fns k) ctx (rest component)))))
  ctx)
