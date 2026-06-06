(ns game.ctx.button-just-pressed
  (:require [gdx.application :as app]
            [clojure.input :as input]))

(defn button-just-pressed?
  [{:keys [ctx/app]} button-code]
  (input/button-just-pressed? (app/input app) button-code))
