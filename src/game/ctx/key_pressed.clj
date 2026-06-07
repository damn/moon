(ns game.ctx.key-pressed
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]))

(defn key-pressed? [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))
