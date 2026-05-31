(ns game.ctx.draw
  (:require [game.constants :refer [draw-fns]]))

(defn draw! [ctx draws]
  (doseq [{k 0 :as component} draws
          :when component]
    (apply (get draw-fns k) ctx (rest component))))
