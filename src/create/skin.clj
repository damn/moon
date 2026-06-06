(ns create.skin
  (:require [game.ctx.create-skin :refer [create-skin]]))

(defn step
  [ctx]
  (create-skin ctx "skin/uiskin.json"))
