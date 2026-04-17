(ns moon.create.tooltip-config
  (:require [clojure.gdx.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn step [ctx {:keys [initial-time]}]
  (tooltip-manager/set-initial-time! initial-time)
  ctx)
