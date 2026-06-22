(ns clojure.ctx.tooltip-manager-opts
  (:require [gdl.tooltip-manager :as tooltip-manager]))

(defn step [_ctx]
  (tooltip-manager/set-initial-time! 0))
