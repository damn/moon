(ns game.ctx.key-pressed
  (:require [gdx.application :as app]
            [gdx.input :as input]))

(defn key-pressed? [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))
