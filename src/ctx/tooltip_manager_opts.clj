(ns ctx.tooltip-manager-opts
  (:require [clojure.gdx :as gdx]))

(defn step [_ctx]
  (gdx/tooltip-manager-set-initial-time! 0))
