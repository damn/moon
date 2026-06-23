(ns ctx.tooltip-manager-opts
  (:require [scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn step [_ctx]
  (tooltip-manager/set-initial-time! 0))
