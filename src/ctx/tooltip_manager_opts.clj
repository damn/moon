(ns ctx.tooltip-manager-opts
  (:require [clojure.gdx.tooltip-manager.get-instance :as get-instance]
            [clojure.gdx.tooltip-manager.set-initial-time :as set-initial-time]))

(defn step [_ctx]
  (set-initial-time/f (get-instance/f) 0))
