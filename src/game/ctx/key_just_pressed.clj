(ns game.ctx.key-just-pressed
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]))

(defn key-just-pressed? [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))
