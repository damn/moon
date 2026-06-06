(ns create.stage
  (:require [game.ctx.create-stage :refer [create-stage]]))

(defn step
  [ctx]
  (create-stage ctx 1440 900))
