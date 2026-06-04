(ns game.ctx.key-just-pressed
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.input :as input]))

(defn key-just-pressed? [{:keys [ctx/app]} input-key]
  (input/key-just-pressed? (app/input app) input-key))
