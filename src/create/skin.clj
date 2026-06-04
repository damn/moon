(ns create.skin
  (:require [game.ctx.create-skin :refer [create-skin]]))

(defn step
  [ctx]
  (assoc ctx :ctx/skin (create-skin ctx "skin/uiskin.json")))
