(ns create.default-font
  (:require [game.ctx.create-font :refer [create-font]]))

(defn step
  [ctx]
  (assoc ctx :ctx/default-font
         (create-font ctx
                      {:path "fonts/films.EXL_____.ttf"
                       :size 16
                       :quality-scaling 2
                       :use-integer-positions? true})))
