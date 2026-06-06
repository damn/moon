(ns game.ctx.key-just-pressed
  (:require [gdx.application :as app]
            [clojure.input :as input]))

(defn key-just-pressed? [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))
