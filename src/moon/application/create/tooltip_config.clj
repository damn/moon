(ns moon.application.create.tooltip-config
  (:require [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]))

(defn step [ctx]
  (tooltip-manager/set-initial-time! 0)
  ctx)
